package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
    @Override
    public void start(Stage stage) {
        // MySQL bağlantı bilgileri 
        String DB_URL  = "jdbc:mysql://localhost:3306/mydb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
        String DB_USER = "root";
        String DB_PASS = "Emre13901390.";

        // Tüm bilgileri GameController'a gönderiyoruz
        new GameController(stage, DB_URL, DB_USER, DB_PASS).startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
