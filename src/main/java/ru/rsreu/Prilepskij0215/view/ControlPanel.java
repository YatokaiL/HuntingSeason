package ru.rsreu.Prilepskij0215.view;

import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import ru.rsreu.Prilepskij0215.controller.GameController;

public class ControlPanel {
    private final VBox panel;
    private final GameController controller;

    public ControlPanel(GameController controller) {
        this.controller = controller;
        this.panel = createPanel();
    }

    private VBox createPanel() {
        VBox controlPanel = new VBox(10);
        controlPanel.setStyle("-fx-padding: 15; -fx-background-color: #4a5568; -fx-spacing: 10;");

        Button startButton = new Button("Старт");
        Button pauseButton = new Button("Пауза");
        Button resumeButton = new Button("Продолжить");
        Button stopButton = new Button("Выход");
        Button reloadButton = new Button("Перезарядить");

        startButton.setOnAction(e -> controller.startGame());
        pauseButton.setOnAction(e -> controller.pauseGame());
        resumeButton.setOnAction(e -> controller.resumeGame());
        stopButton.setOnAction(e -> controller.stopGame());
        reloadButton.setOnAction(e -> controller.reloadWeapon());

        controlPanel.getChildren().addAll(
                startButton, pauseButton, resumeButton, stopButton, reloadButton
        );

        return controlPanel;
    }

}