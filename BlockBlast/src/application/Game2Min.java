//package application;
//
//import javafx.stage.Stage;
//
//public class Game2Min extends TimedGameScreen {
//    public Game2Min(Stage stage, int seconds, String dbUrl, String dbUser, String dbPass) {
//        super(stage, 120, dbUrl, dbUser, dbPass); // 2 dakika
//    }
//
//    @Override
//    protected void onTimeUp() {
//        // Oyun bitince ne olacak?
//        System.out.println("2 dakika doldu. Oyun bitti!");
//    }
//}
package application;

import javafx.stage.Stage;

/**
 * 2 dakikalık Zamana Karşı modu
 */
public class Game2Min extends TimedGameScreen {
    public Game2Min(Stage stage, int seconds,String dbUrl, String dbUser, String dbPass) {
        super(stage, 120, dbUrl, dbUser, dbPass);
    }

    @Override
    protected void onTimeUp() {
    	game.showGameOverPanel();
        
    }
}
