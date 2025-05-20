package application;

import javafx.geometry.Point2D;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Random;

public class Inventory {
    private final int slotCount = 4;
    private final int cellSize;
    private final int gridSize;
    private final HBox inventoryBox;
    private final Pane[] slots;
    private final Point2D[][] shapes;
    private final Color[] colors;

    public Inventory(int gridSize, int cellSize, int inventoryWidth, int inventoryHeight) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.inventoryBox = new HBox();
        this.inventoryBox.setPrefSize(inventoryWidth, inventoryHeight);
        this.slots = new Pane[slotCount];
        this.shapes = new Point2D[slotCount][];
        this.colors = new Color[slotCount];

        int slotWidth = inventoryWidth / slotCount;

        for (int i = 0; i < slotCount; i++) {
            Pane slot = new Pane();
            slot.setPrefSize(slotWidth, inventoryHeight);
            slot.setStyle(
                "-fx-background-color: #354074;" +
                "-fx-border-color: #414a6e; -fx-border-width: 1.5; " +
                "-fx-border-radius: 18; -fx-background-radius: 16;"
            );
            slots[i] = slot;
            inventoryBox.getChildren().add(slot);
        }
    }

    // Rastgele yeni 4 şekil ve renk üret
    public void generateNewSet(Random rnd) {
        for (int i = 0; i < slotCount; i++) {
            shapes[i] = null;
            colors[i] = null;
        }
        boolean[] usedIndices = new boolean[ShapeDefinitions.getShapeCount()];
        int added = 0;
        while (added < slotCount) {
            int idx = ShapeDefinitions.getRandomShapeIndex();
            if (!usedIndices[idx]) {
                usedIndices[idx] = true;
                Point2D[] base = ShapeDefinitions.getShape(idx);
                int rot = rnd.nextInt(4);
                Point2D[] pts = new Point2D[base.length];
                for (int i = 0; i < base.length; i++) {
                    Point2D p = base[i];
                    for (int r = 0; r < rot; r++) p = new Point2D(-p.getY(), p.getX());
                    pts[i] = p;
                }
                Color c = ShapeDefinitions.getRandomColor();
                shapes[added] = pts;
                colors[added] = c;
                added++;
            }
        }
    }

    // Slotları arayüz olarak yeniden oluşturur 
    public void rebuild(java.util.function.BiConsumer<Integer, Pane> slotHandler) {
        for (int i = 0; i < slotCount; i++) {
            Pane slot = slots[i];
            slot.getChildren().clear();
            Point2D[] shape = shapes[i];
            Color color = colors[i];
            if (shape == null || color == null) continue;
            Pane ui = ShapeDefinitions.createShapeUI(shape, color, cellSize);
            slot.getChildren().add(ui);
            slotHandler.accept(i, ui);
        }
    }

    // Tüm şekiller kullanıldı mı?
    public boolean allUsed() {
        for (Point2D[] s : shapes)
            if (s != null) return false;
        return true;
    }

    // Getters
    public Point2D[] getShape(int idx) { return shapes[idx]; }
    public Color getColor(int idx) { return colors[idx]; }
    public void setNull(int idx) { shapes[idx] = null; colors[idx] = null; }
    public HBox getBox() { return inventoryBox; }
    public int getSlotCount() { return slotCount; }
}
