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

    // --- YENİ: Mouse ile hangi shape hücresinin tutulduğunu saklar
    private Point2D shapeOffset = new Point2D(0, 0);

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

    // --- Drag başlatıldığında ---
    public void handleMousePressed(MouseEvent e, int shapeIdx, Pane ui) {
        draggingShapeIdx = shapeIdx;
        draggingUI = ui;

        Point2D[] points = inventory.getShape(shapeIdx);
        if (points == null || points.length == 0) {
            shapeOffset = new Point2D(0, 0);
            return;
        }

        // UI içindeki mouse pozisyonunu grid bloğu olarak bul
        // Önce shape'in min/max X/Y'sini bul
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        for (Point2D pt : points) {
            int x = (int) pt.getX();
            int y = (int) pt.getY();
            if (x < minX) minX = x;
            if (y < minY) minY = y;
        }
        // UI, 2*cellSize'lık kare. Tıklanan yerden hangi grid bloğuna denk geldiğini bul:
        double xInShape = e.getX();
        double yInShape = e.getY();
        int colInShape = (int) ((xInShape * points.length) / (cellSize * 2));
        int rowInShape = (int) ((yInShape * points.length) / (cellSize * 2));

        // En yakın shape grid noktasını bul
        int closestIdx = 0;
        double minDist = Double.MAX_VALUE;
        for (int i = 0; i < points.length; i++) {
            // Blokun envanter UI'sindeki gerçek pozisyonu:
            double px = (points[i].getX() - minX + 0.5) * cellSize * 2 / points.length;
            double py = (points[i].getY() - minY + 0.5) * cellSize * 2 / points.length;
            double dist = Math.pow(xInShape - px, 2) + Math.pow(yInShape - py, 2);
            if (dist < minDist) {
                minDist = dist;
                closestIdx = i;
            }
        }
        // shapeOffset artık tıklanan grid bloğunun shape içindeki indexi
        shapeOffset = new Point2D(points[closestIdx].getX(), points[closestIdx].getY());

        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    // --- Sürüklenirken ---
    public void handleMouseDragged(MouseEvent e) {
        hoverPreview(e.getSceneX(), e.getSceneY());
    }

    // --- Drag bırakıldığında ---
    public void handleMouseReleased(MouseEvent e) {
        clearPreview();

        double x = e.getSceneX() - gridOffset.getX();
        double y = e.getSceneY() - gridOffset.getY();
        int col = (int) (x / cellSize) - (int) shapeOffset.getX();
        int row = (int) (y / cellSize) - (int) shapeOffset.getY();

        if (draggingShapeIdx != -1) {
            Point2D[] shape = inventory.getShape(draggingShapeIdx);
            if (shape != null && canPlace(shape, row, col)) {
                if (callback != null) callback.onPlaced(draggingShapeIdx, row, col);
            }
        }
        draggingShapeIdx = -1;
        draggingUI = null;
    }

    // --- Hover/önizleme fonksiyonu ---
    private void hoverPreview(double sceneX, double sceneY) {
        clearPreview();
        if (draggingShapeIdx == -1) return;
        Point2D[] points = inventory.getShape(draggingShapeIdx);
        Color color = inventory.getColor(draggingShapeIdx);
        if (points == null || color == null) return;

        double x = sceneX - gridOffset.getX();
        double y = sceneY - gridOffset.getY();
        int col = (int) (x / cellSize) - (int) shapeOffset.getX();
        int row = (int) (y / cellSize) - (int) shapeOffset.getY();

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
