//package application;
//
//import javafx.stage.Stage;
//
//public class Game30Sec extends TimedGameScreen {
//
//	public Game30Sec(Stage stage, int seconds, String dbUrl, String dbUser, String dbPass) {
//		super(stage, 30, dbUrl, dbUser, dbPass);
//		// TODO Auto-generated constructor stub
//	}
//
//	@Override
//	protected void onTimeUp() {
//		System.out.println("30 saniye doldu.");
//
//	}
//
//}


package application;

import javax.swing.plaf.InternalFrameUI;

import javafx.stage.Stage;

/**
 * 30 saniyelik Zamana Karşı modu
 */
public class Game30Sec extends TimedGameScreen {
    public Game30Sec(Stage stage,  int seconds, String dbUrl, String dbUser, String dbPass) {
        super(stage, 30, dbUrl, dbUser, dbPass);
    }

    @Override
    protected void onTimeUp() {
    	game.showGameOverPanel();
        
    }
}

