package application;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShapeDragManager {
    private Pane root;
    private int gridSize;
    private int cellSize;
    private Point2D gridOffset;
    private Color[][] board;
    private Rectangle[][] cellRects;
    private Inventory inventory;

    private Point2D dragOffset;
    private int draggingShapeIdx = -1;
    private Pane draggingUI;
    private Rectangle[] previewGhost = new Rectangle[16];
    private int previewGhostCount = 0;

    public interface PlaceCallback {
        void onPlaced(int shapeIdx, int row, int col);
    }
    private PlaceCallback callback;

    public ShapeDragManager(Pane root, int gridSize, int cellSize, Point2D gridOffset,
                            Color[][] board, Rectangle[][] cellRects, Inventory inventory) {
        this.root = root;
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.gridOffset = gridOffset;
        this.board = board;
        this.cellRects = cellRects;
        this.inventory = inventory;
    }

    public void setPlaceCallback(PlaceCallback cb) {
        this.callback = cb;
    }

    public void handleMousePressed(MouseEvent e, int shapeIdx, Pane ui) {
        draggingShapeIdx = shapeIdx;
        draggingUI = ui;
        dragOffset = new Point2D(e.getSceneX() - ui.getLayoutX(), e.getSceneY() - ui.getLayoutY());
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    public void handleMouseDragged(MouseEvent e) {
        if (draggingUI != null) {
            draggingUI.setLayoutX(e.getSceneX() - dragOffset.getX());
            draggingUI.setLayoutY(e.getSceneY() - dragOffset.getY());
            hoverPreview(e.getSceneX(), e.getSceneY());
        }
    }

    public void handleMouseReleased(MouseEvent e) {
        clearPreview();
        double x = e.getSceneX() - gridOffset.getX();
        double y = e.getSceneY() - gridOffset.getY();
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        if (draggingShapeIdx != -1) {
            Point2D[] shape = inventory.getShape(draggingShapeIdx);
            if (shape != null && canPlace(shape, row, col)) {
                if (callback != null) callback.onPlaced(draggingShapeIdx, row, col);
            }
        }
        draggingShapeIdx = -1;
        draggingUI = null;
        
    }

    private void hoverPreview(double sceneX, double sceneY) {
        clearPreview();
        if (draggingShapeIdx == -1) return;
        Point2D[] points = inventory.getShape(draggingShapeIdx);
        Color color = inventory.getColor(draggingShapeIdx);
        if (points == null || color == null) return;

        double x = sceneX - gridOffset.getX();
        double y = sceneY - gridOffset.getY();
        int col = (int) (x / cellSize);
        int row = (int) (y / cellSize);

        previewGhostCount = 0;
        for (Point2D p : points) {
            int r = row + (int) p.getY();
            int c = col + (int) p.getX();
            if (r >= 0 && r < gridSize && c >= 0 && c < gridSize) {
                Rectangle ghost = new Rectangle(cellSize - 2, cellSize - 2);
                ghost.setX(gridOffset.getX() + c * cellSize + 1);
                ghost.setY(gridOffset.getY() + r * cellSize + 1);
                ghost.setFill(color.deriveColor(1, 1, 1, 0.24));
                previewGhost[previewGhostCount++] = ghost;
                root.getChildren().add(ghost);
            }
        }
    }

    private void clearPreview() {
        for (int i = 0; i < previewGhostCount; i++) {
            root.getChildren().remove(previewGhost[i]);
        }
        previewGhostCount = 0;
    }

    public boolean canPlace(Point2D[] points, int baseRow, int baseCol) {
        for (Point2D p : points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            if (r < 0 || r >= gridSize || c < 0 || c >= gridSize || board[r][c] != null) return false;
        }
        return true;
    }
}
