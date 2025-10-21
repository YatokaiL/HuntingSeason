package ru.rsreu.Prilepskij0215.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.GameStatistics;
import ru.rsreu.Prilepskij0215.model.core.GameModel;

public class GameOverScreen {
    private final Stage primaryStage;
    private final GameController controller;
    private final GameStatistics statistics;
    private final String deathReason;

    public GameOverScreen(Stage primaryStage, GameController controller, GameStatistics statistics, String deathReason) {
        this.primaryStage = primaryStage;
        this.controller = controller;
        this.statistics = statistics;
        this.deathReason = deathReason;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #1a202c; -fx-padding: 40;");

        Label title = new Label("GAME OVER");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.RED);
        title.setStyle("-fx-font-weight: bold;");

        Label reasonLabel = new Label(deathReason);
        reasonLabel.setFont(Font.font("Arial", 24));
        reasonLabel.setTextFill(Color.ORANGE);
        reasonLabel.setStyle("-fx-font-weight: bold;");

        VBox statsBox = createStatisticsBox();

        VBox buttonBox = createButtonBox();

        root.getChildren().addAll(title, reasonLabel, statsBox, buttonBox);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Сезон охоты - Конец игры");
        primaryStage.show();
    }

    private void restartGame() {
        try {
            GameModel newModel = new GameModel();
            GameController newController = new GameController(newModel);
            GameInterface gameInterface = new GameInterface(primaryStage, newController);
            gameInterface.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToMainMenu() {
        StartScreen startScreen = new StartScreen(primaryStage, null);
        startScreen.show();
    }

    private VBox createStatisticsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setStyle("-fx-background-color: #2d3748; -fx-padding: 20; -fx-background-radius: 10;");

        Label statsTitle = new Label("GAME STATISTICS");
        statsTitle.setFont(Font.font("Arial", 20));
        statsTitle.setTextFill(Color.WHITE);
        statsTitle.setStyle("-fx-font-weight: bold;");

        Label scoreLabel = new Label("Score: " + statistics.getScore());
        scoreLabel.setFont(Font.font("Arial", 16));
        scoreLabel.setTextFill(Color.WHITE);

        Label accuracyLabel = new Label("Accuracy: " + String.format("%.1f", statistics.getAccuracy()) + "%");
        accuracyLabel.setFont(Font.font("Arial", 16));
        accuracyLabel.setTextFill(Color.WHITE);

        Label shotsLabel = new Label("Shoots: " + statistics.getShotsFired() + " (Hits: " + statistics.getShotsHit() + ")");
        shotsLabel.setFont(Font.font("Arial", 16));
        shotsLabel.setTextFill(Color.WHITE);

        Label reloadsLabel = new Label("Reloads: " + statistics.getReloadsCount());
        reloadsLabel.setFont(Font.font("Arial", 16));
        reloadsLabel.setTextFill(Color.WHITE);

        Label timeLabel = new Label("Survive time: " + formatGameTime(statistics.getGameTime()));
        timeLabel.setFont(Font.font("Arial", 16));
        timeLabel.setTextFill(Color.WHITE);

        statsBox.getChildren().addAll(statsTitle, scoreLabel, accuracyLabel, shotsLabel, reloadsLabel, timeLabel);

        return statsBox;
    }

    private VBox createButtonBox() {
        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button restartButton = createMenuButton("NEW GAME");
        Button mainMenuButton = createMenuButton("MAIN MENU");
        Button exitButton = createMenuButton("EXIT");

        restartButton.setOnAction(e -> restartGame());
        mainMenuButton.setOnAction(e -> returnToMainMenu());
        exitButton.setOnAction(e -> exitGame());

        buttonBox.getChildren().addAll(restartButton, mainMenuButton, exitButton);

        return buttonBox;
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 16));
        button.setStyle(
                "-fx-background-color: #4a5568; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 30 12 30; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 200;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #5a6576; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 30 12 30; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 200;"
        ));
        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: #4a5568; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 30 12 30; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 200;"
        ));

        return button;
    }

    private String formatGameTime(long gameTime) {
        long seconds = gameTime / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void exitGame() {
        if (controller != null) {
            controller.stopGame();
        }
        System.exit(0);
    }
}