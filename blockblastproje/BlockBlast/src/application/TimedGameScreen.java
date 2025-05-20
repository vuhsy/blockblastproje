package application;

//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.layout.Pane;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//
//public abstract class TimedGameScreen {
//    protected int secondsLeft;
//    protected Label timerLabel;
//    protected Timeline timeline;
//    protected Stage stage;
//    protected Pane root;
//
//    public TimedGameScreen(Stage stage, int seconds) {
//        this.stage = stage;
//        this.secondsLeft = seconds;
//        setupUI();
//        startTimer();
//    }
//
//    private void setupUI() {
//        root = new Pane();
//        root.setPrefSize(600, 700);
//        timerLabel = new Label("Süre: " + secondsLeft);
//        timerLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: red;");
//        timerLabel.setLayoutX(20);
//        timerLabel.setLayoutY(10);
//        root.getChildren().add(timerLabel);
//
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.setTitle("Zamana Karşı Oyun");
//        stage.show();
//    }
//
//    private void startTimer() {
//        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
//            secondsLeft--;
//            timerLabel.setText("Süre: " + secondsLeft);
//            if (secondsLeft <= 0) {
//                timeline.stop();
//                onTimeUp();
//            }
//        }));
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//    }
//
//    // Süre bitince ne olacağını alt sınıf belirlesin
//    protected abstract void onTimeUp();
//}


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Ortak “Time Attack” ekranı.  
 * • Verilen saniyeden geriye sayar  
 * • Label’da MM:SS formatında gösterir  
 * • Süre bittiğinde onTimeUp()’ı çağırır  
 * • Oyun mantığını GameController_easyMode’dan alır
 */
public abstract class TimedGameScreen {
    protected Stage stage;              // Sahneyi/referansı sakla
    protected Pane root;                // Tüm UI elemanlarının ekleneceği kök panel
    protected Label timerLabel;         // Kalan süreyi gösteren etiket
    protected Timeline timeline;        // Sayaç animasyonu
    protected int secondsLeft;          // Geri sayılacak toplam saniye
    private String dbUrl;
    private String dbUser;
    private String dbPass;
    protected GameController_easyMode game; // Oyun mantığını yöneten controller

    /**
     * @param stage     Uygulamanın ana penceresi
     * @param seconds   Başlangıç süresi (saniye cinsinden)
     */
//    public TimedGameScreen(Stage stage, int seconds, String dbUrl, String dbUser, String dbPass) {
//        this.stage = stage;
//        this.secondsLeft = seconds;
//        this.dbUrl = dbUrl;
//        this.dbUser = dbUser;
//        this.dbPass = dbPass;
//        initGameController();  // Oyun mantığını başlat
//        setupUI();             // UI'nı kur (sayaç + oyun)
//        startTimer();          // Geri sayımı başlat
//    }
    
    public TimedGameScreen(Stage stage, int seconds, String dbUrl, String dbUser, String dbPass) {
        this.stage = stage;
        this.secondsLeft = seconds;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        initGameController();
        setupUI();
        startTimer();
    }

    /** GameController_easyMode'dan bir örnek oluşturur ve temel root'u alır */
    private void initGameController() {
        game = new GameController_easyMode(stage, dbUrl, dbUser, dbPass);   // Oyun mantığını hazırla
        root = game.getRootPane();              // controller'ın hazırladığı pane'i al
    }

    /**  
     * UI'ı kurar:  
     * 1) Sayaç Label  
     * 2) GameController içeriğini root'a ekleme  
     * 3) Sahneyi oluşturup gösterme  
     */
    private void setupUI() {
        // 1) Sayaç etiketi oluştur ve konumla
        timerLabel = new Label(formatTime(secondsLeft));
        timerLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #FFDD44;");
        timerLabel.setLayoutX(10);
        timerLabel.setLayoutY(10);
        root.getChildren().add(timerLabel);

        // 2) Sahneyi hazırla ve göster
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Zamana Karşı");
        stage.show();
    }

    /**  
     * Geri sayımı başlatır. Her saniye:  
     * - secondsLeft--  
     * - timerLabel güncellenir  
     * - 0 olduğunda timeline durur + onTimeUp()  
     */
    private void startTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            timerLabel.setText(formatTime(secondsLeft));
            if (secondsLeft <= 0) {
                timeline.stop();
                onTimeUp();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * MM:SS formatına çevirir.  
     * @param totalSeconds kalan saniye  
     * @return örn. "00:16", "01:00", "02:00"
     */
    private String formatTime(int totalSeconds) {
        int mins = totalSeconds / 60;
        int secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    /**  
     * Süre dolduğunda alt sınıflarda ne yapılacağını burada belirleyeceksin.  
     * Örneğin input’u engelle, skor göster paneli aç…  
     */
    protected abstract void onTimeUp();
}
