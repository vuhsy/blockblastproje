package application;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameBoard {
    private final int gridSize;
    private final int cellSize;
    private final Color[][] board;
    private final Rectangle[][] cellRects;
    private final Pane gridPane;

    public GameBoard(int gridSize, int cellSize, Pane gridPane) {
        this.gridSize = gridSize;
        this.cellSize = cellSize;
        this.gridPane = gridPane;
        this.board = new Color[gridSize][gridSize];
        this.cellRects = new Rectangle[gridSize][gridSize];
        initBoard();
    }

    private void initBoard() {
        gridPane.getChildren().clear();
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                Rectangle cell = new Rectangle(cellSize - 2, cellSize - 2);
                cell.setStroke(Color.web("#414a6e"));
                cell.setFill(Color.web("#232a4d"));
                cell.setX(c * cellSize + 1);
                cell.setY(r * cellSize + 1);
                board[r][c] = null;
                cellRects[r][c] = cell;
                gridPane.getChildren().add(cell);
            }
        }
    }

    public void reset() {
        for (int r = 0; r < gridSize; r++) {
            for (int c = 0; c < gridSize; c++) {
                board[r][c] = null;
                cellRects[r][c].setFill(Color.web("#232a4d"));
            }
        }
    }

    public void placeShape(Point2D[] points, Color color, int baseRow, int baseCol) {
        for (Point2D p : points) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            board[r][c] = color;
            cellRects[r][c].setFill(color);
        }
        SoundPlayer.play("/sounds/place.wav");
    }

    public int clearFullLines(int pointsPerLine, ScoreManager scoreManager) {
        int cleared = 0;
        // Satır temizleme
        for (int r = 0; r < gridSize; r++) {
            boolean full = true;
            for (int c = 0; c < gridSize; c++) if (board[r][c] == null) full = false;
            if (full) {
                for (int c = 0; c < gridSize; c++) {
                    board[r][c] = null;
                    cellRects[r][c].setFill(Color.web("#232a4d"));
                }
                cleared++;
            }
        }
        // Sütun temizleme
        for (int c = 0; c < gridSize; c++) {
            boolean full = true;
            for (int r = 0; r < gridSize; r++) if (board[r][c] == null) full = false;
            if (full) {
                for (int r = 0; r < gridSize; r++) {
                    board[r][c] = null;
                    cellRects[r][c].setFill(Color.web("#232a4d"));
                }
                cleared++;
            }
        }
        // Skor ekle
        scoreManager.add(pointsPerLine * cleared);
        // Sadece burada ses çal!
        if (cleared > 0) {
            SoundPlayer.play("/sounds/clear.wav");
        }
        return cleared;
    }


    public boolean canPlace(Point2D[] shape, int baseRow, int baseCol) {
        for (Point2D p : shape) {
            int r = baseRow + (int) p.getY();
            int c = baseCol + (int) p.getX();
            if (r < 0 || r >= gridSize || c < 0 || c >= gridSize || board[r][c] != null) return false;
        }
        return true;
    }

    public Color[][] getBoard() {
        return board;
    }

    public Pane getGridPane() {
		return gridPane;
	}

	public Rectangle[][] getCellRects() {
        return cellRects;
    }
}
