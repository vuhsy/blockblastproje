package application;

import javafx.stage.Stage;

import javafx.stage.Stage;



import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;
import java.util.Random;

/**
 * Timer modu için klasik EasyMode kontrollerinin bağımsız bir kopyası.
 * Bu sınıf, GameController_easyMode içeriğini aynen alır,
 * ancak isim çatışması olmaması için farklı bir isimle tanımlanır.
 */
public class TimerGameController {
    private Stage stage;
    private Pane root;
    private Pane gridPane;
    private Inventory inventoryObj;
    private ShapeDragManager dragManager;
    private ScoreManager scoreManager;
    private GameBoard gameBoard;
    private DatabaseManager dbManager;

    private int GRID_SIZE = 10;
    private final int MIN_GRID_SIZE = 7;
    private int CELL_SIZE = 60;
    private final int MAX_CELL_SIZE = 75;
    private final Point2D GRID_OFFSET = new Point2D(10, 60);

    private int nextThresholdScore = 3000;
    private String playerName;
    private int pointsPerBlock = 10;
    private int pointsPerLine = 500;
    private Random rnd = new Random();

    public TimerGameController(Stage stage, String dbUrl, String dbUser, String dbPass) {
        this.stage = stage;
        root = new Pane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #5c73bc, #5763ad 80%, #485090 100%);");
        root.setPrefSize(GRID_SIZE * CELL_SIZE + 20,
                         GRID_SIZE * CELL_SIZE + CELL_SIZE * 2 + 70);

        // Skor etiketi
        Label scoreLabel = new Label("0");
        scoreLabel.setStyle(
            "-fx-font-size: 36px; " +
            "-fx-font-family: 'Arial Rounded MT Bold', Arial, sans-serif; " +
            "-fx-text-fill: #0a061a; " +
            "-fx-background-color: transparent; " +
            "-fx-padding: 0;"
        );
        double labelWidth = 120;
        scoreLabel.setPrefWidth(labelWidth);
        scoreLabel.setLayoutX((GRID_SIZE * CELL_SIZE + 20 - labelWidth) / 2.0);
        scoreLabel.setLayoutY(8);
        scoreLabel.setAlignment(javafx.geometry.Pos.CENTER);
        root.getChildren().add(scoreLabel);

        // Highscore etiketi
        Label highScoreLabel = new Label();
        highScoreLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-family: Arial; " +
            "-fx-text-fill: #444; " +
            "-fx-background-color: transparent;"
        );
        highScoreLabel.setLayoutX(10);
        highScoreLabel.setLayoutY(10);
        highScoreLabel.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        root.getChildren().add(highScoreLabel);

        // Grid panel
        gridPane = new Pane();
        gridPane.setLayoutX(GRID_OFFSET.getX());
        gridPane.setLayoutY(GRID_OFFSET.getY());
        gridPane.setPrefSize(GRID_SIZE * CELL_SIZE,
                             GRID_SIZE * CELL_SIZE);
        gridPane.setStyle(
            "-fx-background-color: #2d355e; " +
            "-fx-border-radius: 10; " +
            "-fx-background-radius: 10;"
        );
        root.getChildren().add(gridPane);

        gameBoard = new GameBoard(GRID_SIZE, CELL_SIZE);
        inventoryObj = new Inventory();
        dragManager = new ShapeDragManager(gridPane, inventoryObj, this);
        scoreManager = new ScoreManager(scoreLabel, highScoreLabel);
        dbManager = new DatabaseManager(dbUrl, dbUser, dbPass);

        // Veritabanı ve yüksek skor yükle
        try {
            dbManager.connect();
            String[] high = dbManager.getHighScore();
            scoreManager.setHighScore(Integer.parseInt(high[1]), high[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
            Platform.exit();
            return;
        }

        // Scene ayarı
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Block Blast — Timer Modu");
        stage.show();

        // Oyun başlat
        startGame();
    }

    /**
     * Oyun akışını başlatır.
     */
    public void startGame() {
        inventoryObj.generateNewSet(rnd);
        rebuildInventory();
    }

    private void rebuildInventory() {
        // Envanteri güncelle
        inventoryObj.placeShapes();
    }

    // Burada GameController_easyMode içindeki diğer metotlar aynı şekilde kopyalanacak,
    // örneğin:
    // - clearFullLines()
    // - resetGame()
    // - any other helper methods ...
}

