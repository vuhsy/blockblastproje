package application;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import java.util.Optional;
import java.util.Random;

public class GameController {
    private final Stage stage;
    private final Pane root;
    private final Pane gridPane;
    private final Inventory inventoryObj;
    private final ShapeDragManager dragManager;
    private final ScoreManager scoreManager;
    private final GameBoard gameBoard;
    private final DatabaseManager dbManager;

    private final int GRID_SIZE = 8;
    private final int CELL_SIZE = 60;
    private final Point2D GRID_OFFSET = new Point2D(10, 60);

    private String playerName;
    private int pointsPerBlock = 10;
    private int pointsPerLine = 500;
    private Random rnd = new Random();

    //  MySQL bilgilerini Main'den parametreyle alıyoruz 
    public GameController(Stage stage, String dbUrl, String dbUser, String dbPass) {
        this.stage = stage;
        root = new Pane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #5c73bc, #5763ad 80%, #485090 100%);");
        root.setPrefSize(GRID_SIZE * CELL_SIZE + 20, GRID_SIZE * CELL_SIZE + CELL_SIZE * 2 + 70);

        // Skor etiketleri
        Label scoreLabel = new Label("0");
        scoreLabel.setStyle("-fx-font-size: 36px; -fx-font-family: 'Arial Rounded MT Bold', Arial, sans-serif; -fx-text-fill: #0a061a; -fx-background-color: transparent; -fx-padding: 0;");
        double labelWidth = 120;
        scoreLabel.setPrefWidth(labelWidth);
        scoreLabel.setLayoutX((GRID_SIZE * CELL_SIZE + 20 - labelWidth) / 2.0);
        scoreLabel.setLayoutY(8);
        scoreLabel.setAlignment(javafx.geometry.Pos.CENTER);
        root.getChildren().add(scoreLabel);

        Label highScoreLabel = new Label();
        highScoreLabel.setStyle("-fx-font-size: 18px; -fx-font-family: Arial; -fx-text-fill: #444; -fx-background-color: transparent;");
        highScoreLabel.setLayoutX(10);
        highScoreLabel.setLayoutY(10);
        highScoreLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        root.getChildren().add(highScoreLabel);

        // Grid panel
        gridPane = new Pane();
        gridPane.setLayoutX(GRID_OFFSET.getX());
        gridPane.setLayoutY(GRID_OFFSET.getY());
        gridPane.setPrefSize(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE);
        gridPane.setStyle("-fx-background-color: #2d355e; -fx-border-radius: 10; -fx-background-radius: 10;");
        root.getChildren().add(gridPane);

        // GameBoard ve ScoreManager
        gameBoard = new GameBoard(GRID_SIZE, CELL_SIZE, gridPane);
        scoreManager = new ScoreManager(scoreLabel, highScoreLabel);

        // Inventory
        int invY = (int)(GRID_OFFSET.getY() + GRID_SIZE * CELL_SIZE + 10);
        int invWidth = GRID_SIZE * CELL_SIZE;
        int invHeight = CELL_SIZE * 2;
        inventoryObj = new Inventory(GRID_SIZE, CELL_SIZE, invWidth, invHeight);
        inventoryObj.getBox().setLayoutX(GRID_OFFSET.getX());
        inventoryObj.getBox().setLayoutY(invY);
        root.getChildren().add(inventoryObj.getBox());

        // Drag manager
        dragManager = new ShapeDragManager(
            root, GRID_SIZE, CELL_SIZE, GRID_OFFSET,
            gameBoard.getBoard(), gameBoard.getCellRects(), inventoryObj
        );

       
        dbManager = new DatabaseManager(dbUrl, dbUser, dbPass);

        dragManager.setPlaceCallback((shapeIdx, row, col) -> {
            gameBoard.placeShape(inventoryObj.getShape(shapeIdx), inventoryObj.getColor(shapeIdx), row, col);
            scoreManager.add(pointsPerBlock * inventoryObj.getShape(shapeIdx).length);
            gameBoard.clearFullLines(pointsPerLine, scoreManager);
            inventoryObj.setNull(shapeIdx);
            if (inventoryObj.allUsed()) inventoryObj.generateNewSet(rnd);
            rebuildInventory();
            if (!canPlaceAnyShape()) showGameOverPanel();
        });
    }

    public void startGame() {
        // 1. Oyuncu adını al
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Oyuncu Adı");
        dialog.setHeaderText("Lütfen adınızı girin:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty() || result.get().trim().isEmpty()) {
            Platform.exit();
            return;
        }
        playerName = result.get().trim();

        // 2. MySQL bağlantısı + yüksek skor
        try {
            dbManager.connect();
            String[] high = dbManager.getHighScore();
            int hiscore = Integer.parseInt(high[1]);
            scoreManager.setHighScore(hiscore, high[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.exit();
            return;
        }

        // 3. Oyunu başlat
        inventoryObj.generateNewSet(rnd);
        rebuildInventory();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Block Blast");
        stage.show();
    }

    private void rebuildInventory() {
        inventoryObj.rebuild((idx, ui) -> {
        	 ui.setOnMousePressed(e -> dragManager.handleMousePressed(e, idx, ui));
             ui.setOnMouseDragged(dragManager::handleMouseDragged);
             ui.setOnMouseReleased(e -> {
                 dragManager.handleMouseReleased(e);
                 rebuildInventory(); // blok koyulsa da koyulmasa da inventoryi rebuild edecek
             });
        });
    }

    private boolean canPlaceAnyShape() {
        for (int i = 0; i < inventoryObj.getSlotCount(); i++) {
            Point2D[] shape = inventoryObj.getShape(i);
            if (shape == null) continue;
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    if (gameBoard.canPlace(shape, row, col))
                        return true;
                }
            }
        }
        return false;
    }

    private void showGameOverPanel() {
        Pane panel = new Pane();
        panel.setStyle("-fx-background-color: rgba(255,255,255,0.97); -fx-border-radius: 18; -fx-background-radius: 18; -fx-border-color: #aaaaff; -fx-border-width: 3;");
        panel.setPrefSize(400, 380);
        panel.setLayoutX((root.getWidth() - 400) / 2);
        panel.setLayoutY((root.getHeight() - 380) / 2);

        Label over = new Label("OYUN BİTTİ!");
        over.setStyle("-fx-font-size: 38px; -fx-font-family: Arial Rounded MT Bold; -fx-text-fill: #5c73bc;");
        over.setLayoutX(110); over.setLayoutY(30);

        Label skor = new Label("Skorunuz: " + scoreManager.getScore());
        skor.setStyle("-fx-font-size: 28px; -fx-font-family: Arial; -fx-text-fill: #222;");
        skor.setLayoutX(135); skor.setLayoutY(90);

        // Skorları çek ve tabloya ekle
        dbManager.saveScore(playerName, scoreManager.getScore());
        String[][] scoreArray = dbManager.getAllPlayersHighScoresArray();

        Label tableLabel = new Label("En Yüksek Oyuncu Skorları");
        tableLabel.setStyle("-fx-font-size: 18px; -fx-font-family: Arial; -fx-text-fill: #444;");
        tableLabel.setLayoutX(105); tableLabel.setLayoutY(130);

        Pane scoreTable = new Pane();
        scoreTable.setLayoutX(60);
        scoreTable.setLayoutY(160);
        for (int i = 0; i < scoreArray.length; i++) {
            if (scoreArray[i][0] == null) break; // boş ise devam etme
            Label l = new Label((i+1) + ". " + scoreArray[i][0] + " : " + scoreArray[i][1]);
            l.setStyle("-fx-font-size: 15px; -fx-font-family: Arial; -fx-text-fill: #222;");
            l.setLayoutX(10);
            l.setLayoutY(i * 25);
            scoreTable.getChildren().add(l);
        }

        javafx.scene.control.Button again = new javafx.scene.control.Button("Yeniden Oyna");
        again.setStyle("-fx-font-size: 20px; -fx-background-radius: 18; -fx-background-color: #5c73bc; -fx-text-fill: white; -fx-pref-width: 180;");
        again.setLayoutX(110); again.setLayoutY(310);

        again.setOnAction(ev -> {
            root.getChildren().remove(panel);
            resetGame();
        });

        panel.getChildren().addAll(over, skor, tableLabel, scoreTable, again);
        root.getChildren().add(panel);
    }


    private void resetGame() {
        gameBoard.reset();
        scoreManager.reset();
        inventoryObj.generateNewSet(rnd);
        rebuildInventory();
    }
}
