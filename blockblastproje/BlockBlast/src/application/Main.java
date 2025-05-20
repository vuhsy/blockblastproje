package application;

//import javafx.application.Application;
//import javafx.stage.Stage;
//import javafx.stage.Stage;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.VBox;
//import javafx.geometry.Pos;



//public class Main extends Application {
//	
//    @Override
//    public void start(Stage stage) {
//        // MySQL bağlantı bilgileri 
//        String DB_URL  =  "jdbc:mysql://localhost:3306/blockblast?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
//        String DB_USER = "root";
//        String DB_PASS = "Ceng106Proje";
//        
//        // 1) Ana Menü butonları
//        Button btnContinue = new Button("Devam Et");
//        Button btnNewGame  = new Button("Yeni Oyun");
//
//        // 2) Placeholder handler’lar
//        btnContinue.setOnAction(e -> onContinue(stage));
//        btnNewGame .setOnAction(e -> onNewGameOptions(stage));
//
//        // 3) Layout’u hazırla
//        VBox root = new VBox(20, btnContinue, btnNewGame);
//        root.setAlignment(Pos.CENTER);
//
//        // 4) Sahneyi (Scene) oluştur ve göster
//        Scene menuScene = new Scene(root, 800, 600);
//        stage.setTitle("BlockBlast - Ana Menü");
//        stage.setScene(menuScene);
//        stage.show();
//     // Tüm bilgileri GameController'a gönderiyoruz
////        new GameController(stage, DB_URL, DB_USER, DB_PASS).startGame();
//    }
//
//    // “Devam Et” tıklandığında çalışacak metot
//    private void onContinue(Stage stage) {
//        // TODO: veritabanından son kaydettiğin noktayı oku
//        //       GameController’a devam etme metodunu çağır
//        System.out.println(">> Devam Et tıklandı");
//        // örn: new GameController(stage, DB_URL, DB_USER, DB_PASS).continueGame();
//    }
//
//    // “Yeni Oyun” alt menüsünü açan metot
//    private void onNewGameOptions(Stage stage) {
//        Button btnClassic = new Button("Classic");
//        Button btnTimed   = new Button("Zamana Karşı");
//
//        btnClassic.setOnAction(e -> showClassicOptions(stage));
//        btnTimed  .setOnAction(e -> showTimedOptions(stage));
//
//        VBox submenu = new VBox(15, btnClassic, btnTimed);
//        submenu.setAlignment(Pos.CENTER);
//        Scene subScene = new Scene(submenu, 800, 600);
//        stage.setScene(subScene);
//    }
//
//    // Classic içi kolay / zor seçenekleri
//    private void showClassicOptions(Stage stage) {
//        Button btnEasy = new Button("Kolay");
//        Button btnHard = new Button("Zor");
//
//        btnEasy.setOnAction(e -> startClassic(stage, "EASY"));
//        btnHard.setOnAction(e -> startClassic(stage, "HARD"));
//
//        VBox layout = new VBox(15, btnEasy, btnHard);
//        layout.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(layout, 800, 600));
//    }
//
//    // Zamana karşı içi 30sn / 1dk / 2dk
//    private void showTimedOptions(Stage stage) {
//        Button btn30s  = new Button("30 sn");
//        Button btn1m   = new Button("1 dk");
//        Button btn2m   = new Button("2 dk");
//
//        btn30s.setOnAction(e -> startTimed(stage, 30));
//        btn1m .setOnAction(e -> startTimed(stage, 60));
//        btn2m .setOnAction(e -> startTimed(stage, 120));
//
//        VBox layout = new VBox(15, btn30s, btn1m, btn2m);
//        layout.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(layout, 800, 600));
//    }
//
//    // Stub: Classic modu başlat
//    private void startClassic(Stage stage, String difficulty) {
//        System.out.println(">> Classic " + difficulty + " başladı");
//        // TODO: new GameController(stage, DB_URL, DB_USER, DB_PASS).startClassic(difficulty);
//    }
//
//    // Stub: Zamana karşı modu başlat
//    private void startTimed(Stage stage, int seconds) {
//        System.out.println(">> Zamana karşı " + seconds + "s başladı");
//        // TODO: new GameController(stage, DB_URL, DB_USER, DB_PASS).startTimed(seconds);
//    }   
//        
//        
//        // Tüm bilgileri GameController'a gönderiyoruz
//        new GameController(stage, DB_URL, DB_USER, DB_PASS).startGame();
//    
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}










//public class Main extends Application {
//
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/blockblast?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
//    private static final String DB_USER = "root";
//    private static final String DB_PASS = "Ceng106Proje";
//
//    @Override
//    public void start(Stage stage) {
//        Button btnContinue = new Button("Devam Et");
//        Button btnNewGame = new Button("Yeni Oyun");
//
//        btnContinue.setOnAction(e -> onContinue(stage));
//        btnNewGame.setOnAction(e -> onNewGameOptions(stage));
//
//        VBox root = new VBox(20, btnContinue, btnNewGame);
//        root.setAlignment(Pos.CENTER);
//
//        Scene menuScene = new Scene(root, 800, 600);
//        stage.setTitle("BlockBlast - Ana Menü");
//        stage.setScene(menuScene);
//        stage.show();
//    }
//
//    private void onContinue(Stage stage) {
//        System.out.println(">> Devam Et tıklandı");
//        // new GameController(stage, DB_URL, DB_USER, DB_PASS).continueGame();
//    }
//
//    private void onNewGameOptions(Stage stage) {
//        Button btnClassic = new Button("Classic");
//        Button btnTimed = new Button("Zamana Karşı");
//
//        btnClassic.setOnAction(e -> showClassicOptions(stage));
//        btnTimed.setOnAction(e -> showTimedOptions(stage));
//
//        VBox submenu = new VBox(15, btnClassic, btnTimed);
//        submenu.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(submenu, 800, 600));
//    }
//
//    private void showClassicOptions(Stage stage) {
//        Button btnEasy = new Button("Kolay");
//        Button btnHard = new Button("Zor");
//
//        btnEasy.setOnAction(e -> startClassic(stage, "EASY"));
//        btnHard.setOnAction(e -> startClassic(stage, "HARD"));
//
//        VBox layout = new VBox(15, btnEasy, btnHard);
//        layout.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(layout, 800, 600));
//    }
//
//    private void showTimedOptions(Stage stage) {
//        Button btn30s = new Button("30 sn");
//        Button btn1m = new Button("1 dk");
//        Button btn2m = new Button("2 dk");
//
//        btn30s.setOnAction(e -> startTimed(stage, 30));
//        btn1m.setOnAction(e -> startTimed(stage, 60));
//        btn2m.setOnAction(e -> startTimed(stage, 120));
//
//        VBox layout = new VBox(15, btn30s, btn1m, btn2m);
//        layout.setAlignment(Pos.CENTER);
//        stage.setScene(new Scene(layout, 800, 600));
//    }
//
//    private void startClassic(Stage stage, String difficulty) {
//        System.out.println(">> Classic " + difficulty + " başladı");
//        // new GameController(stage, DB_URL, DB_USER, DB_PASS).startClassic(difficulty);
//    }
//
//    private void startTimed(Stage stage, int seconds) {
//        System.out.println(">> Zamana karşı " + seconds + "s başladı");
//        // new GameController(stage, DB_URL, DB_USER, DB_PASS).startTimed(seconds);
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}



//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.stage.Stage;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.VBox;
//import javafx.geometry.Pos;
//import javafx.geometry.Insets;
//import java.util.Stack;
//
//public class Main extends Application {
//    private Stage mainStage;
//    private Stack<Scene> sceneHistory = new Stack<>();
//
//    @Override
//    public void start(Stage stage) {
//        this.mainStage = stage;
//        showMainMenu();
//    }
//
//    private void showMainMenu() {
//        // Buttons
//        Button btnContinue = new Button("Devam Et");
//        Button btnNewGame  = new Button("Yeni Oyun");
//        Button btnExit     = new Button("Çıkış");
//
//        // Inline styling for buttons
//        String btnStyle = "-fx-text-fill: white; -fx-font-size: 18px; -fx-background-radius: 10;";
//        btnContinue.setStyle(btnStyle + "-fx-background-color: #ff7f50;");
//        btnNewGame .setStyle(btnStyle + "-fx-background-color: #4caf50;");
//        btnExit    .setStyle(btnStyle + "-fx-background-color: #f44336;");
//
//        // Button sizes relative to scene
//        btnContinue.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.5));
//        btnNewGame .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.5));
//        btnExit    .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.5));
//        btnContinue.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.1));
//        btnNewGame .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.1));
//        btnExit    .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.1));
//
//        // Actions
//        btnContinue.setOnAction(e -> { /* TODO: implement continue logic */ });
//        btnNewGame .setOnAction(e -> pushScene(buildNewGameMenu()));
//        btnExit    .setOnAction(e -> Platform.exit());
//
//        // Layout
//        VBox menuBox = new VBox(20, btnContinue, btnNewGame, btnExit);
//        menuBox.setAlignment(Pos.CENTER);
//        menuBox.paddingProperty().set(new Insets(20));
//
//        BorderPane root = new BorderPane(menuBox);
//        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2196f3, #21cbf3);");
//
//        Scene scene = new Scene(root, 800, 600);
//        mainStage.setTitle("BlockBlast - Ana Menü");
//        mainStage.setScene(scene);
//        mainStage.show();
//    }
//
//    private Scene buildNewGameMenu() {
//        Button btnClassic = new Button("Classic Mode");
//        Button btnTimed   = new Button("Zamana Karşı");
//        Button btnBack    = new Button("← Geri");
//
//        // Styles
//        String btnStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
//        btnClassic.setStyle(btnStyle + "-fx-background-color: #ff9800;");
//        btnTimed  .setStyle(btnStyle + "-fx-background-color: #03a9f4;");
//        btnBack   .setStyle(btnStyle + "-fx-background-color: transparent; -fx-text-fill: white;");
//
//        // Sizes
//        btnClassic.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.4));
//        btnTimed  .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.4));
//        btnBack   .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.2));
//        btnClassic.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btnTimed  .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btnBack   .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.06));
//
//        // Actions
//        btnClassic.setOnAction(e -> pushScene(buildClassicOptions()));
//        btnTimed  .setOnAction(e -> pushScene(buildTimedOptions()));
//        btnBack   .setOnAction(e -> popScene());
//
//        VBox box = new VBox(15, btnClassic, btnTimed);
//        box.setAlignment(Pos.CENTER);
//        box.setPadding(new Insets(20));
//
//        BorderPane root = new BorderPane(box);
//        root.setTop(btnBack);
//        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
//        root.setStyle("-fx-background-color: linear-gradient(to bottom, #64b5f6, #90caf9);");
//
//        return new Scene(root, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
//    }
//
//    private Scene buildClassicOptions() {
//        Button btnEasy = new Button("Kolay");
//        Button btnHard = new Button("Zor");
//        Button btnBack = new Button("← Geri");
//
//        String btnStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
//        btnEasy.setStyle(btnStyle + "-fx-background-color: #8bc34a;");
//        btnHard.setStyle(btnStyle + "-fx-background-color: #f44336;");
//        btnBack.setStyle(btnStyle + "-fx-background-color: transparent; -fx-text-fill: white;");
//
//        btnEasy.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.3));
//        btnHard.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.3));
//        btnBack.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.2));
//        btnEasy.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btnHard.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btnBack.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.06));
//
//        
//        btnHard.setOnAction(e -> { /* TODO: start classic hard mode */ });
//        btnBack.setOnAction(e -> popScene());
//
//        VBox box = new VBox(15, btnEasy, btnHard);
//        box.setAlignment(Pos.CENTER);
//        box.setPadding(new Insets(20));
//
//        BorderPane root = new BorderPane(box);
//        root.setTop(btnBack);
//        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
//        root.setStyle("-fx-background-color: linear-gradient(to bottom, #a5d6a7, #c8e6c9);");
//
//        return new Scene(root, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
//    }
//
//    private Scene buildTimedOptions() {
//        Button btn30s = new Button("30 sn");
//        Button btn1m  = new Button("1 dk");
//        Button btn2m  = new Button("2 dk");
//        Button btnBack = new Button("← Geri");
//
//        String btnStyle = "-fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 8;";
//        btn30s.setStyle(btnStyle + "-fx-background-color: #4db6ac;");
//        btn1m.setStyle(btnStyle + "-fx-background-color: #29b6f6;");
//        btn2m.setStyle(btnStyle + "-fx-background-color: #42a5f5;");
//        btnBack.setStyle(btnStyle + "-fx-background-color: transparent; -fx-text-fill: white;");
//
//        btn30s.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.25));
//        btn1m .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.25));
//        btn2m .prefWidthProperty().bind(mainStage.widthProperty().multiply(0.25));
//        btnBack.prefWidthProperty().bind(mainStage.widthProperty().multiply(0.2));
//        btn30s.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btn1m .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btn2m .prefHeightProperty().bind(mainStage.heightProperty().multiply(0.08));
//        btnBack.prefHeightProperty().bind(mainStage.heightProperty().multiply(0.06));
//
//        btn30s.setOnAction(e -> { /* TODO: start timed 30s */ });
//        btn1m.setOnAction(e -> { /* TODO: start timed 1m */ });
//        btn2m.setOnAction(e -> { /* TODO: start timed 2m */ });
//        btnBack.setOnAction(e -> popScene());
//
//        VBox box = new VBox(15, btn30s, btn1m, btn2m);
//        box.setAlignment(Pos.CENTER);
//        box.setPadding(new Insets(20));
//
//        BorderPane root = new BorderPane(box);
//        root.setTop(btnBack);
//        BorderPane.setAlignment(btnBack, Pos.TOP_LEFT);
//        root.setStyle("-fx-background-color: linear-gradient(to bottom, #80deea, #b2ebf2);");
//
//        return new Scene(root, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
//    }
//
//    private void pushScene(Scene scene) {
//        sceneHistory.push(mainStage.getScene());
//        mainStage.setScene(scene);
//    }
//
//    private void popScene() {
//        if (!sceneHistory.isEmpty()) {
//            mainStage.setScene(sceneHistory.pop());
//        }
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}



import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String DB_URL  = "jdbc:mysql://localhost:3306/blockblast?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "Ceng106Proje";

    @Override
    public void start(Stage primaryStage) {
        // Ana menüyü başlatıyoruz. MainMenu tüm UI logic'ini içerir.
        new MainMenu(primaryStage, DB_URL, DB_USER, DB_PASS).showMainMenu();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


