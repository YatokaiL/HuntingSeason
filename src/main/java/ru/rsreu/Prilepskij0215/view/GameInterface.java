package ru.rsreu.Prilepskij0215.view;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.entities.WeaponType;
import ru.rsreu.Prilepskij0215.model.events.GameEvent;
import ru.rsreu.Prilepskij0215.model.events.GameEventListener;

public class GameInterface implements GameEventListener {
    private final Stage primaryStage;
    private final GameController controller;
    private GameCanvas gameCanvas;

    private Label scoreLabel;
    private Label healthLabel;

    private VBox pauseMenu;
    private boolean isPauseMenuVisible = false;

    public GameInterface(Stage primaryStage, GameController controller) {
        this.primaryStage = primaryStage;
        this.controller = controller;
    }

    public void show() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #1a202c;");

        gameCanvas = new GameCanvas(controller);

        VBox statsText = createStatsText();
        statsText.setMouseTransparent(true);

        pauseMenu = createPauseMenu();
        pauseMenu.setVisible(false);

        root.getChildren().addAll(gameCanvas.getCanvas(), statsText, pauseMenu);

        Scene scene = new Scene(root, 1000, 800);
        setupEventHandlers(scene);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hunting Season");

        this.controller.setOnGameOver(this::showGameOverScreen);
        this.controller.getGameModel().addEventListener(this);

        startGameLoop();

        Platform.runLater(() -> {
            if (gameCanvas != null && gameCanvas.getCanvas() != null) {
                gameCanvas.getCanvas().requestFocus();
                System.out.println("Canvas focus requested after show");
            }
        });
    }

    private void showGameOverScreen(String deathReason) {
        Platform.runLater(() -> {
            GameOverScreen gameOverScreen = new GameOverScreen(
                    primaryStage,
                    controller,
                    controller.getGameState().getStatistics(),
                    deathReason
            );
            gameOverScreen.show();
        });
    }

    private VBox createStatsText() {
        VBox statsText = new VBox(5);
        statsText.setStyle("-fx-padding: 0; -fx-background-color: transparent;");
        statsText.setAlignment(Pos.TOP_LEFT);

        scoreLabel = createTextLabel("Score: 0", "white");
        healthLabel = createTextLabel("Health: 100", "white");

        statsText.getChildren().addAll(scoreLabel, healthLabel);

        StackPane.setAlignment(statsText, Pos.TOP_LEFT);
        StackPane.setMargin(statsText, new javafx.geometry.Insets(10, 10, 0, 0));

        return statsText;
    }

    private VBox createPauseMenu() {
        VBox pauseMenu = new VBox(15);
        pauseMenu.setStyle("-fx-padding: 30; -fx-background-color: #2d3748; -fx-background-radius: 10;");
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setPrefSize(300, 300);

        Label title = new Label("PAUSED");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-font-weight: bold;");

        Button resumeButton = createMenuButton("RESUME");
        Button mainMenuButton = createMenuButton("MAIN MENU");
        Button exitButton = createMenuButton("EXIT GAME");

        resumeButton.setOnAction(e -> resumeGame());
        mainMenuButton.setOnAction(e -> returnToMainMenu());
        exitButton.setOnAction(e -> exitGame());

        pauseMenu.getChildren().addAll(title, resumeButton, mainMenuButton, exitButton);

        StackPane.setAlignment(pauseMenu, Pos.CENTER);

        return pauseMenu;
    }

    private void returnToMainMenu() {
        if (controller != null) {
            controller.stopGame();
        }
        StartScreen startScreen = new StartScreen(primaryStage, null);
        startScreen.show();
    }

    private void exitGame() {
        if (controller != null) {
            controller.stopGame();
        }
        Platform.exit();
        System.exit(0);
    }

    private void showMessage(String message) {
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-background-color: rgba(0,0,0,0.7); -fx-padding: 10;");
        StackPane.setAlignment(messageLabel, Pos.TOP_CENTER);
        StackPane.setMargin(messageLabel, new javafx.geometry.Insets(50, 0, 0, 0));

        StackPane root = (StackPane) primaryStage.getScene().getRoot();
        root.getChildren().add(messageLabel);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> root.getChildren().remove(messageLabel));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 14));
        button.setStyle(
                "-fx-background-color: #4a5568; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8 20 8 20; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 160;"
        );

        return button;
    }

    private Label createTextLabel(String text, String color) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14; -fx-font-weight: bold;");
        return label;
    }

    private void setupEventHandlers(Scene scene) {
        if (gameCanvas != null) {
            gameCanvas.getCanvas().setFocusTraversable(true);

            gameCanvas.getCanvas().setOnMousePressed(event -> {
                gameCanvas.getCanvas().requestFocus();
            });
        }

        scene.setOnKeyPressed(event -> {
            if (controller == null) return;

            switch (event.getCode()) {
                case ESCAPE:
                    togglePauseMenu();
                    break;
                case W:
                case UP:
                    if (!isPauseMenuVisible && controller.getGameState().isRunning() && !controller.getGameState().isPaused()) {
                        controller.moveHunter(0, -10);
                    }
                    break;
                case S:
                case DOWN:
                    if (!isPauseMenuVisible && controller.getGameState().isRunning() && !controller.getGameState().isPaused()) {
                        controller.moveHunter(0, 10);
                    }
                    break;
                case A:
                case LEFT:
                    if (!isPauseMenuVisible && controller.getGameState().isRunning() && !controller.getGameState().isPaused()) {
                        controller.moveHunter(-10, 0);
                    }
                    break;
                case D:
                case RIGHT:
                    if (!isPauseMenuVisible && controller.getGameState().isRunning() && !controller.getGameState().isPaused()) {
                        controller.moveHunter(10, 0);
                    }
                    break;
                case R:
                    if (!isPauseMenuVisible && controller.getGameState().isRunning() && !controller.getGameState().isPaused()) {
                        controller.reloadWeapon();
                    }
                    break;
            }
        });

        if (gameCanvas != null) {
            gameCanvas.getCanvas().setOnMouseClicked(event -> {
                if (controller != null && !isPauseMenuVisible) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    controller.shoot(x, y);
                    if (gameCanvas != null) {
                        gameCanvas.onShotFired();
                    }
                }
            });

            gameCanvas.getCanvas().setOnScroll(event -> {
                if (controller != null && !isPauseMenuVisible) {
                    if (event.getDeltaY() > 0) {
                        cycleWeaponForward();
                    } else if (event.getDeltaY() < 0) {
                        cycleWeaponBackward();
                    }
                }
            });
        }

        if (gameCanvas != null && gameCanvas.getCanvas() != null) {
            gameCanvas.getCanvas().requestFocus();
            System.out.println("Initial canvas focus requested");
        }
    }

    private void cycleWeaponForward() {
        WeaponType current = controller.getGameState().getPlayerWeapon().getType();
        WeaponType next;

        switch (current) {
            case RIFLE: next = WeaponType.SHOTGUN; break;
            case SHOTGUN: next = WeaponType.PISTOL; break;
            case PISTOL: next = WeaponType.RIFLE; break;
            default: next = WeaponType.RIFLE;
        }

        controller.switchWeapon(next);
        if (gameCanvas != null) {
            gameCanvas.onWeaponSwitch();
        }

        System.out.println("Weapon switched to: " + next +
                ", Current ammo: " + controller.getGameState().getPlayerWeapon().getCurrentAmmo() +
                ", Total ammo: " + controller.getGameState().getPlayerWeapon().getTotalAmmo());
    }

    private void cycleWeaponBackward() {
        WeaponType current = controller.getGameState().getPlayerWeapon().getType();
        WeaponType previous;

        switch (current) {
            case RIFLE: previous = WeaponType.PISTOL; break;
            case SHOTGUN: previous = WeaponType.RIFLE; break;
            case PISTOL: previous = WeaponType.SHOTGUN; break;
            default: previous = WeaponType.RIFLE;
        }

        controller.switchWeapon(previous);
        if (gameCanvas != null) {
            gameCanvas.onWeaponSwitch();
        }

        System.out.println("Weapon switched to: " + previous +
                ", Current ammo: " + controller.getGameState().getPlayerWeapon().getCurrentAmmo() +
                ", Total ammo: " + controller.getGameState().getPlayerWeapon().getTotalAmmo());
    }

    private void togglePauseMenu() {
        if (isPauseMenuVisible) {
            pauseMenu.setVisible(false);
            isPauseMenuVisible = false;
            controller.resumeGame();
            gameCanvas.getCanvas().requestFocus();
        } else {
            pauseMenu.setVisible(true);
            isPauseMenuVisible = true;
            controller.pauseGame();
        }
    }

    private void resumeGame() {
        pauseMenu.setVisible(false);
        isPauseMenuVisible = false;
        controller.resumeGame();
        gameCanvas.getCanvas().requestFocus();
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateGameState();
            }
        };
        timer.start();
    }

    private void updateGameState() {
        if (controller == null) return;

        try {
            var gameState = controller.getGameState();
            if (gameState == null) return;

            var stats = gameState.getStatistics();

            scoreLabel.setText("Score: " + stats.getScore());
            healthLabel.setText("Health: " + stats.getHealth());

            if (stats.getHealth() < 30) {
                healthLabel.setStyle("-fx-text-fill: #e53e3e; -fx-font-size: 14; -fx-font-weight: bold;");
            } else if (stats.getHealth() < 60) {
                healthLabel.setStyle("-fx-text-fill: #ed8936; -fx-font-size: 14; -fx-font-weight: bold;");
            } else {
                healthLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold;");
            }

            if (gameCanvas != null) {
                gameCanvas.update();
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onGameEvent(GameEvent event) {
        Platform.runLater(() -> {
            switch (event.getType()) {
                case GAME_STATE_UPDATE:
                    scoreLabel.setText("Score: " + event.getX());
                    healthLabel.setText("Health: " + event.getY());
                    break;
            }
        });
    }
}