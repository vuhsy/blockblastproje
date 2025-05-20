package application;

import javafx.stage.Stage;

public class Game1Min extends TimedGameScreen {

    public Game1Min(Stage stage, int seconds, String dbUrl, String dbUser, String dbPass) {
        super(stage, 60, dbUrl, dbUser, dbPass); // 60 saniyelik sabit mod
        
    }

    @Override
    protected void onTimeUp() {
        System.out.println("1 dakika doldu.");
        // istersen burada sahne değiştir, skor göster, vs.
    }
}
