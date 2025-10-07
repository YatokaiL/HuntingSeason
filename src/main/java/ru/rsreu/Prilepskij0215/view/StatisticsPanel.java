package ru.rsreu.Prilepskij0215.view;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import ru.rsreu.Prilepskij0215.controller.GameController;

public class StatisticsPanel {
    private final VBox panel;
    private final GameController controller;

    private Label scoreLabel;
    private Label healthLabel;
    private Label weaponLabel;
    private Label ammoLabel;
    private Label accuracyLabel;
    private ProgressBar healthBar;

    public StatisticsPanel(GameController controller) {
        this.controller = controller;

        initializeComponents();

        this.panel = createPanel();

        setupUpdateTimer();
    }

    private void initializeComponents() {
        this.scoreLabel = new Label("Очки: 0");
        this.healthLabel = new Label("Здоровье: 100%");
        this.weaponLabel = new Label("Оружие: Ружье");
        this.ammoLabel = new Label("Патроны: 30/60");
        this.accuracyLabel = new Label("Точность: 0%");
        this.healthBar = new ProgressBar(1.0);
    }

    private VBox createPanel() {
        VBox statsPanel = new VBox(10);
        statsPanel.setStyle("-fx-padding: 15; -fx-background-color: #2d3748; -fx-spacing: 8;");

        Label title = new Label("Статистика");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

        scoreLabel.setStyle("-fx-text-fill: white;");
        healthLabel.setStyle("-fx-text-fill: white;");
        weaponLabel.setStyle("-fx-text-fill: white;");
        ammoLabel.setStyle("-fx-text-fill: white;");
        accuracyLabel.setStyle("-fx-text-fill: white;");

        healthBar.setStyle("-fx-accent: #48bb78;");
        healthBar.setPrefWidth(200);

        statsPanel.getChildren().addAll(
                title, scoreLabel, healthLabel, healthBar,
                weaponLabel, ammoLabel, accuracyLabel
        );

        return statsPanel;
    }

    private void setupUpdateTimer() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateStatistics();
            }
        };
        timer.start();
    }

    private void updateStatistics() {
        try {
            if (controller == null) return;

            var gameState = controller.getGameState();
            if (gameState == null) return;

            var stats = gameState.getStatistics();
            var weapon = gameState.getPlayerWeapon();

            if (scoreLabel != null) scoreLabel.setText("Очки: " + stats.getScore());
            if (healthLabel != null) healthLabel.setText("Здоровье: " + stats.getHealth() + "%");
            if (weaponLabel != null) weaponLabel.setText("Оружие: " + weapon.getType().name());
            if (ammoLabel != null) ammoLabel.setText("Патроны: " + weapon.getCurrentAmmo() + "/" + weapon.getTotalAmmo());
            if (accuracyLabel != null) accuracyLabel.setText("Точность: " + String.format("%.1f", stats.getAccuracy()) + "%");

            if (healthBar != null) {
                healthBar.setProgress(stats.getHealth() / 100.0);

                if (stats.getHealth() < 30) {
                    healthBar.setStyle("-fx-accent: #e53e3e;");
                } else if (stats.getHealth() < 60) {
                    healthBar.setStyle("-fx-accent: #ed8936;");
                } else {
                    healthBar.setStyle("-fx-accent: #48bb78;");
                }
            }

        } catch (Exception e) {
            System.err.println("Error updating statistics: " + e.getMessage());
        }
    }

    public VBox getPanel() {
        return panel;
    }
}