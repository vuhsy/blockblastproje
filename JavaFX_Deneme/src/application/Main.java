package application;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private static final int GRID_SIZE = 8;
    private static final int CELL_SIZE = 60;
    private static final Point2D GRID_OFFSET = new Point2D(10, 10);
    private static final List<Point2D[]> TETROMINOS = Arrays.asList(
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(-1,0), new Point2D(0,1) }, // T
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(-1,0), new Point2D(-2,0) }, // I
        new Point2D[]{ new Point2D(0,0), new Point2D(0,1), new Point2D(1,1), new Point2D(1,0) },   // O
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(0,1), new Point2D(0,2) },   // L
        new Point2D[]{ new Point2D(0,0), new Point2D(-1,0), new Point2D(0,1), new Point2D(0,2) },  // J
        new Point2D[]{ new Point2D(0,0), new Point2D(1,0), new Point2D(0,1), new Point2D(-1,1) },  // S
        new Point2D[]{ new Point2D(0,0), new Point2D(-1,0), new Point2D(0,1), new Point2D(1,1) }   // Z
    );
    private static final List<Color> COLORS = Arrays.asList(
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE, Color.ORANGE, Color.CYAN
    );

    private Color[][] board = new Color[GRID_SIZE][GRID_SIZE];
    private Rectangle[][] cellRects = new Rectangle[GRID_SIZE][GRID_SIZE];

    private Pane root;
    private Pane gridPane;
    private HBox inventory;
    private List<ShapeInfo> upcoming = new ArrayList<>();
    private Random rnd = new Random();
    private List<Rectangle> previewGhost = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        root.setPrefSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + CELL_SIZE * 2 + 30);

        // Grid area
        gridPane = new Pane();
        gridPane.setLayoutX(GRID_OFFSET.getX());
        gridPane.setLayoutY(GRID_OFFSET.getY());
        gridPane.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        gridPane.setStyle("-fx-background-color: lightgray;");
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Rectangle cell = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                cell.setStroke(Color.WHITE);
                cell.setFill(Color.WHITE);
                cell.setX(c * CELL_SIZE + 1);
                cell.setY(r * CELL_SIZE + 1);
                board[r][c] = null;
                cellRects[r][c] = cell;
                gridPane.getChildren().add(cell);
            }
        }
        root.getChildren().add(gridPane);

        // Inventory area (bottom) subdivided into 4 slots
        int invY = (int)(GRID_OFFSET.getY() + GRID_SIZE * CELL_SIZE + 10);
        int invWidth = GRID_SIZE * CELL_SIZE;
        int invHeight = CELL_SIZE * 2;
        int slotWidth = invWidth / 4;
        inventory = new HBox();
        inventory.setLayoutX(GRID_OFFSET.getX());
        inventory.setLayoutY(invY);
        inventory.setPrefSize(invWidth, invHeight);
        for (int i = 0; i < 4; i++) {
            Pane slot = new Pane();
            slot.setPrefSize(slotWidth, invHeight);
            slot.setStyle("-fx-border-color: gray; -fx-background-color: rgba(200,200,200,0.5);");
            inventory.getChildren().add(slot);
        }
        root.getChildren().add(inventory);

        // Initialize upcoming shapes
        upcoming.clear();
        for (int i = 0; i < 3; i++) {
            upcoming.add(generateRandomShape());
        }
        rebuildInventory();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Block Blast");
        primaryStage.show();
    }

    private void rebuildInventory() {
        // Clear all slots
        for (int i = 0; i < inventory.getChildren().size(); i++) {
            Pane slot = (Pane) inventory.getChildren().get(i);
            slot.getChildren().clear();
        }
        // Place each upcoming shape in its slot, centered
        for (int i = 0; i < upcoming.size(); i++) {
            ShapeInfo info = upcoming.get(i);
            Pane ui = info.createUI();
            Pane slot = (Pane) inventory.getChildren().get(i);
            double x = (slot.getPrefWidth() - ui.getPrefWidth()) / 2;
            double y = (slot.getPrefHeight() - ui.getPrefHeight()) / 2;
            ui.setLayoutX(x);
            ui.setLayoutY(y);
            slot.getChildren().add(ui);
            ui.setOnMousePressed(this::startDrag);
            ui.setOnMouseDragged(this::doDrag);
            ui.setOnMouseReleased(this::endDrag);
        }
    }

    private ShapeInfo generateRandomShape() {
        Point2D[] base = TETROMINOS.get(rnd.nextInt(TETROMINOS.size()));
        int rot = rnd.nextInt(4);
        Point2D[] pts = new Point2D[base.length];
        for (int i = 0; i < base.length; i++) {
            Point2D p = base[i];
            for (int r = 0; r < rot; r++) p = new Point2D(-p.getY(), p.getX());
            pts[i] = p;
        }
        Color c = COLORS.get(rnd.nextInt(COLORS.size()));
        return new ShapeInfo(pts, c);
    }

    private Point2D dragOffset;
    private ShapeInfo draggingShape;
    private Pane draggingUI;

    private void startDrag(MouseEvent e) {
        Pane source = (Pane) e.getSource();
        int idx = -1;
        for (int i = 0; i < inventory.getChildren().size(); i++) {
            if (inventory.getChildren().get(i) == source.getParent()) {
                idx = i;
                break;
            }
        }
        draggingShape = upcoming.get(idx);
        draggingUI = source;
        dragOffset = new Point2D(e.getSceneX() - source.getLayoutX(), e.getSceneY() - source.getLayoutY());
        originalHoverPreview(e.getSceneX(), e.getSceneY());
    }

    private void doDrag(MouseEvent e) {
        draggingUI.setLayoutX(e.getSceneX() - dragOffset.getX());
        draggingUI.setLayoutY(e.getSceneY() - dragOffset.getY());
        originalHoverPreview(e.getSceneX(), e.getSceneY());
    }

    private void endDrag(MouseEvent e) {
        clearPreview();
        double x = e.getSceneX() - GRID_OFFSET.getX();
        double y = e.getSceneY() - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);
        if (draggingShape != null && canPlace(draggingShape, row, col)) {
            placeShape(draggingShape, row, col);
            clearFullLines();
            int idx = upcoming.indexOf(draggingShape);
            upcoming.set(idx, generateRandomShape());
            rebuildInventory();
        }
        draggingShape = null;
    }

    private void originalHoverPreview(double sceneX, double sceneY) {
        clearPreview();
        if (draggingShape == null) return;
        double x = sceneX - GRID_OFFSET.getX();
        double y = sceneY - GRID_OFFSET.getY();
        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);
        for (Point2D p : draggingShape.points) {
            int r = row + (int) p.getY();
            int c = col + (int) p.getX();
            if (r >= 0 && r < GRID_SIZE && c >= 0 && c < GRID_SIZE) {
                Rectangle ghost = new Rectangle(CELL_SIZE - 2, CELL_SIZE - 2);
                ghost.setX(GRID_OFFSET.getX() + c * CELL_SIZE + 1);
                ghost.setY(GRID_OFFSET.getY() + r * CELL_SIZE + 1);
                ghost.setFill(draggingShape.color.deriveColor(1, 1, 1, 0.3));
                previewGhost.add(ghost);
                root.getChildren().add(ghost);
            }
        }
    }

    private void clearPreview() {
        for (Rectangle ghost : previewGhost) root.getChildren().remove(ghost);
        previewGhost.clear();
    }

    private boolean canPlace(ShapeInfo shape, int baseRow, int baseCol) {
        for (Point2D p : shape.points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            if (r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE) return false;
            if (board[r][c] != null) return false;
        }
        return true;
    }

    private void placeShape(ShapeInfo shape, int baseRow, int baseCol) {
        for (Point2D p : shape.points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            board[r][c] = shape.color;
            cellRects[r][c].setFill(shape.color);
        }
    }

    private void clearFullLines() {
        for (int r = 0; r < GRID_SIZE; r++) {
            boolean full = true;
            for (int c = 0; c < GRID_SIZE; c++) if (board[r][c] == null) { full = false; break; }
            if (full) for (int c = 0; c < GRID_SIZE; c++) { board[r][c] = null; cellRects[r][c].setFill(Color.WHITE); }
        }
        for (int c = 0; c < GRID_SIZE; c++) {
            boolean full = true;
            for (int r = 0; r < GRID_SIZE; r++) if (board[r][c] == null) { full = false; break; }
            if (full) for (int r = 0; r < GRID_SIZE; r++) { board[r][c] = null; cellRects[r][c].setFill(Color.WHITE); }
        }
    }

    private class ShapeInfo {
        Point2D[] points;
        Color color;
        public ShapeInfo(Point2D[] points, Color color) { this.points = points; this.color = color; }
        public Pane createUI() {
            Pane p = new Pane();
            p.setPrefSize(CELL_SIZE, CELL_SIZE);
            for (Point2D pt : points) {
                Rectangle rect = new Rectangle(CELL_SIZE * 0.4, CELL_SIZE * 0.4);
                rect.setFill(color);
                rect.setStroke(Color.BLACK);
                rect.setLayoutX((pt.getX() + 1.5) * CELL_SIZE * 0.4);
                rect.setLayoutY((pt.getY() + 1.5) * CELL_SIZE * 0.4);
                p.getChildren().add(rect);
            }
            return p;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}