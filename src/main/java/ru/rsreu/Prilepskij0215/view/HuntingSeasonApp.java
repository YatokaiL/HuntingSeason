package ru.rsreu.Prilepskij0215.view;

import javafx.application.Application;
import javafx.stage.Stage;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.core.GameModel;

public class HuntingSeasonApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            GameModel gameModel = new GameModel();
            GameController controller = new GameController(gameModel);

            StartScreen startScreen = new StartScreen(primaryStage, controller);
            startScreen.show();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}