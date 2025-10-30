package ru.rsreu.Prilepskij0215.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.core.GameModel;

public class StartScreen {
    private final Stage primaryStage;
    private final GameController controller;

    public StartScreen(Stage primaryStage, GameController controller) {
        this.primaryStage = primaryStage;
        this.controller = controller;
    }

    public void show() {
        BackgroundFill backgroundFill = new BackgroundFill(
                Color.DARKGREEN,
                CornerRadii.EMPTY,
                javafx.geometry.Insets.EMPTY
        );
        Background background = new Background(backgroundFill);

        VBox root = new VBox(30);
        root.setBackground(background);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 50;");

        Label title = new Label("HUNTING SEASON");
        title.setFont(Font.font("Arial", 48));
        title.setTextFill(Color.GOLD);
        title.setStyle("-fx-font-weight: bold;");

        Label subtitle = new Label("Hunting Adventure");
        subtitle.setFont(Font.font("Arial", 20));
        subtitle.setTextFill(Color.WHITE);

        Button startButton = createSimpleButton("START GAME");
        startButton.setOnAction(e -> startGame());

        Button exitButton = createSimpleButton("EXIT");
        exitButton.setOnAction(e -> exitGame());

        Label controlsInfo = new Label("Controls:\nWASD - movement\nLMB - shoot\nR - reload\nMouse wheel - switch weapon\nESC - pause menu\n\nWarning: wolves and bears attack!\nMisses reduce health!");

        controlsInfo.setFont(Font.font("Arial", 14));
        controlsInfo.setTextFill(Color.LIGHTGRAY);
        controlsInfo.setStyle("-fx-text-alignment: center;");

        VBox buttonBox = new VBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startButton, exitButton, controlsInfo);

        root.getChildren().addAll(title, subtitle, buttonBox);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Сезон охоты");
        primaryStage.show();
    }

    private Button createSimpleButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 18));
        button.setStyle(
                "-fx-background-color: #8B4513; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 12 25 12 25; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 200;"
        );

        return button;
    }
    private void startGame() {
        GameModel gameModel = new GameModel();
        GameController newController = new GameController(gameModel);
        GameInterface gameInterface = new GameInterface(primaryStage, newController);

        newController.startGame();

        gameInterface.show();
    }

    private void exitGame() {
        if (controller != null) {
            controller.stopGame();
        }
        System.exit(0);
    }
}