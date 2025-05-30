package application;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.Stack;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainMenu {
    private final Stage stage;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPass;
    private final Stack<Scene> history = new Stack<>();

    public MainMenu(Stage stage, String dbUrl, String dbUser, String dbPass) {
        this.stage = stage;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
    }

        public void showMainMenu() {
            // Logo yükleme
            Image logoImage = new Image(getClass().getResourceAsStream("/blastrix_logo.png"));
            ImageView logoView = new ImageView(logoImage);
            logoView.setPreserveRatio(true);
            logoView.setFitWidth(300);
            VBox logoBox = new VBox(logoView);
            logoBox.setAlignment(Pos.CENTER);
            logoBox.setPadding(new Insets(20, 0, 0, 0));

            // Butonlar: Devam Et kaldırıldı
            Button btnNewGame = new Button("Yeni Oyun");
            Button btnExit    = new Button("Çıkış");

            // Stil
            String baseStyle = "-fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 10;";
            btnNewGame.setStyle(baseStyle + "-fx-background-color: #4caf50;");
            btnExit   .setStyle(baseStyle + "-fx-background-color: #f44336;");

            // Boyut binding
            btnNewGame.prefWidthProperty().bind(stage.widthProperty().multiply(0.5));
            btnExit   .prefWidthProperty().bind(stage.widthProperty().multiply(0.5));
            btnNewGame.prefHeightProperty().bind(stage.heightProperty().multiply(0.1));
            btnExit   .prefHeightProperty().bind(stage.heightProperty().multiply(0.1));

            // İşlemler
            btnNewGame.setOnAction(e -> push(buildNewGameMenu()));
            btnExit   .setOnAction(e -> Platform.exit());

            // Layout: butonlar arası mesafe arttırıldı ve yukarı taşındı
            VBox menuBox = new VBox(30, btnNewGame, btnExit);
            menuBox.setAlignment(Pos.TOP_CENTER);
            menuBox.setPadding(new Insets(30, 20, 20, 20));

            // Root ve scene
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: linear-gradient(to bottom, #2196f3, #21cbf3);");
            root.setTop(logoBox);
            root.setCenter(menuBox);

            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setTitle("Blastrix");
            stage.show();
        }
    
    	
    private Scene buildNewGameMenu() {
        Button btnClassic = new Button("Classic Mode");
        Button btnTimed   = new Button("BlastRush Mode");
        Button btnBack    = new Button("← Geri");

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
        btnClassic.setStyle(baseStyle + "-fx-background-color: #ff9800;");
        btnTimed  .setStyle(baseStyle + "-fx-background-color: #03a9f4;");
        btnBack   .setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: white;");

        // Boyut
        btnClassic.prefWidthProperty().bind(stage.widthProperty().multiply(0.4));
        btnTimed  .prefWidthProperty().bind(stage.widthProperty().multiply(0.4));
        btnBack   .prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        btnClassic.prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btnTimed  .prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btnBack   .prefHeightProperty().bind(stage.heightProperty().multiply(0.06));

        // İşlemler
        btnClassic.setOnAction(e -> push(buildClassicOptions()));
        btnTimed  .setOnAction(e -> push(buildTimedOptions()));
        btnBack   .setOnAction(e -> pop());

        VBox box = new VBox(15, btnClassic, btnTimed);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        BorderPane root = new BorderPane(box);
        root.setTop(btnBack);
        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #64b5f6, #90caf9);");

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private Scene buildClassicOptions() {
        Button btnEasy = new Button("Kolay");
        Button btnHard = new Button("Zor");
        Button btnBack = new Button("← Geri");

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
        btnEasy.setStyle(baseStyle + "-fx-background-color: #8bc34a;");
        btnHard.setStyle(baseStyle + "-fx-background-color: #f44336;");
        btnBack.setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: white;");

        btnEasy.prefWidthProperty().bind(stage.widthProperty().multiply(0.3));
        btnHard.prefWidthProperty().bind(stage.widthProperty().multiply(0.3));
        btnBack.prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        btnEasy.prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btnHard.prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btnBack.prefHeightProperty().bind(stage.heightProperty().multiply(0.06));

        btnEasy.setOnAction(e -> new GameController_easyMode(stage, dbUrl, dbUser, dbPass).startGame());

        btnHard.setOnAction(e -> new GameController_hardMode(stage, dbUrl, dbUser, dbPass).startGame());
        btnBack.setOnAction(e -> pop());

        VBox box = new VBox(15, btnEasy, btnHard);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        BorderPane root = new BorderPane(box);
        root.setTop(btnBack);
        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #a5d6a7, #c8e6c9);");

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private Scene buildTimedOptions() {
        Button btn30s = new Button("30 sn");
        Button btn1m  = new Button("1 dk");
        Button btn2m  = new Button("2 dk");
        Button btnBack = new Button("← Geri");

        String baseStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
        btn30s.setStyle(baseStyle + "-fx-background-color: #4db6ac;");
        btn1m.setStyle(baseStyle + "-fx-background-color: #29b6f6;");
        btn2m.setStyle(baseStyle + "-fx-background-color: #42a5f5;");
        btnBack.setStyle(baseStyle + "-fx-background-color: transparent; -fx-text-fill: white;");

        btn30s.prefWidthProperty().bind(stage.widthProperty().multiply(0.25));
        btn1m .prefWidthProperty().bind(stage.widthProperty().multiply(0.25));
        btn2m .prefWidthProperty().bind(stage.widthProperty().multiply(0.25));
        btnBack.prefWidthProperty().bind(stage.widthProperty().multiply(0.2));
        btn30s.prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btn1m .prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btn2m .prefHeightProperty().bind(stage.heightProperty().multiply(0.08));
        btnBack.prefHeightProperty().bind(stage.heightProperty().multiply(0.06));

        btn30s.setOnAction(e -> new Game30Sec(stage, 30, dbUrl, dbUser, dbPass ));
        btn1m.setOnAction(e -> new Game1Min(stage, 60, dbUrl, dbUser, dbPass));
        btn2m.setOnAction(e -> new Game2Min(stage, 120, dbUrl, dbUser, dbPass));
        btnBack.setOnAction(e -> pop());

        VBox box = new VBox(15, btn30s, btn1m, btn2m);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        BorderPane root = new BorderPane(box);
        root.setTop(btnBack);
        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #80deea, #b2ebf2);");

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private void push(Scene scene) {
        history.push(stage.getScene());
        stage.setScene(scene);
    }

    private void pop() {
        if (!history.isEmpty()) {
            stage.setScene(history.pop());
        }
    }
}
