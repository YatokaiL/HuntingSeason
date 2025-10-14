package ru.rsreu.Prilepskij0215.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.entities.Animal;
import ru.rsreu.Prilepskij0215.model.entities.AnimalType;
import ru.rsreu.Prilepskij0215.model.entities.BonusType;

import java.util.Random;

public class GameCanvas {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameController controller;
    private final Random random;

    private final int[][] trees = new int[25][2];
    private final int[][] bushes = new int[40][2];
    private final int[][] rocks = new int[15][2];

    public GameCanvas(GameController controller) {
        this.controller = controller;
        this.canvas = new Canvas(1000, 800);
        this.canvas.setFocusTraversable(true);
        this.gc = canvas.getGraphicsContext2D();
        this.random = new Random();

        generateNatureElements();
    }

    public void update() {
        drawGameState();
    }

    private void generateNatureElements() {
        for (int i = 0; i < trees.length; i++) {
            trees[i][0] = 50 + random.nextInt(900); // x координата
            trees[i][1] = 50 + random.nextInt(700); // y координата
        }

        for (int i = 0; i < bushes.length; i++) {
            bushes[i][0] = 30 + random.nextInt(940); // x координата
            bushes[i][1] = 30 + random.nextInt(740); // y координата
        }

        for (int i = 0; i < rocks.length; i++) {
            rocks[i][0] = 20 + random.nextInt(960); // x координата
            rocks[i][1] = 20 + random.nextInt(760); // y координата
        }
    }

    private void drawGameState() {
        gc.setFill(Color.FORESTGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        drawNatureElements();

        if (controller == null) return;

        try {
            var gameState = controller.getGameState();
            if (gameState == null) return;

            var gameMap = gameState.getGameMap();
            var hunter = gameMap.getHunter();

            gc.setFill(Color.ROYALBLUE);
            gc.fillOval(hunter.getX() - 12, hunter.getY() - 12, 24, 24);
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2);
            gc.strokeOval(hunter.getX() - 12, hunter.getY() - 12, 24, 24);

            for (var animal : gameMap.getAnimals()) {
                if (animal.isActive()) {
                    int animalX = Math.max(8, Math.min(1000 - 8, animal.getX()));
                    int animalY = Math.max(8, Math.min(800 - 8, animal.getY()));

                    long timeLeft = 20000 - animal.getLifeTime();
                    boolean isAboutToEscape = timeLeft < 5000 && timeLeft > 0;

                    Color animalColor = getAnimalColor(animal.getType());
                    if (isAboutToEscape) {
                        long time = System.currentTimeMillis();
                        if ((time / 300) % 2 == 0) {
                            animalColor = Color.ORANGE;
                        }
                    }

                    if (animal.getType().isAggressive()) {
                        long time = System.currentTimeMillis();
                        if ((time / 500) % 2 == 0) {
                            animalColor = Color.RED;
                        }
                    }

                    gc.setFill(Color.rgb(0, 0, 0, 0.3));
                    gc.fillRect(animalX - 4, animalY - 2, 12, 12);

                    gc.setFill(animalColor);
                    gc.fillRect(animalX - 6, animalY - 6, 12, 12);

                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);
                    gc.strokeRect(animalX - 6, animalY - 6, 12, 12);

                    if (animal.getHealth() < animal.getMaxHealth()) {
                        drawHealthBar(animal, animalX, animalY);
                    }

                    if (timeLeft < 10000 && timeLeft > 0) {
                        double secondsLeft = timeLeft / 1000.0;
                        gc.setFill(Color.WHITE);
                        gc.setStroke(Color.BLACK);
                        gc.setLineWidth(1);

                        gc.setFill(Color.rgb(0, 0, 0, 0.5));
                        gc.fillRect(animalX + 6, animalY - 20, 25, 12);

                        gc.setFill(Color.WHITE);
                        gc.fillText(String.format("%.1f", secondsLeft), animalX + 8, animalY - 10);
                    }

                    if (animal.getType().isAggressive()) {
                        drawAggressionIndicator(animal, animalX, animalY);
                    }
                }
            }

            for (var bonus : gameMap.getBonuses()) {
                gc.setFill(getBonusGlowColor(bonus.getType()));
                gc.fillOval(bonus.getX() - 8, bonus.getY() - 8, 16, 16);

                gc.setFill(getBonusColor(bonus.getType()));
                gc.fillOval(bonus.getX() - 6, bonus.getY() - 6, 12, 12);

                gc.setStroke(Color.WHITE);
                gc.setLineWidth(1);
                gc.strokeOval(bonus.getX() - 6, bonus.getY() - 6, 12, 12);
            }

        } catch (Exception e) {
        }
    }

    private void drawAggressionIndicator(Animal animal, int x, int y) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        int indicatorSize = 25;
        if (animal.getType() == AnimalType.BEAR) {
            indicatorSize = 30;
        }

        gc.strokeOval(x - indicatorSize/2, y - indicatorSize/2, indicatorSize, indicatorSize);

        gc.setFill(Color.RED);
        gc.setFont(new javafx.scene.text.Font(12));

        String aggressionText = "";
        switch (animal.getType()) {
            case WOLF: aggressionText = "WOLF"; break;
            case BEAR: aggressionText = "BEAR"; break;
        }

        gc.fillText(aggressionText, x - 20, y - 30);
    }

    private void drawNatureElements() {

        drawGrassTexture();

        for (int[] rock : rocks) {
            drawRock(rock[0], rock[1]);
        }

        for (int[] bush : bushes) {
            drawBush(bush[0], bush[1]);
        }

        for (int[] tree : trees) {
            drawTree(tree[0], tree[1]);
        }
    }

    private void drawGrassTexture() {
        gc.setFill(Color.rgb(34, 139, 34, 0.1));
        for (int i = 0; i < 200; i++) {
            int x = random.nextInt(1000);
            int y = random.nextInt(800);
            int size = 2 + random.nextInt(4);
            gc.fillOval(x, y, size, size);
        }
    }

    private void drawTree(int x, int y) {
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(x - 3, y, 6, 25);

        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - 12, y - 15, 24, 20);

        gc.setFill(Color.FORESTGREEN);
        gc.fillOval(x - 10, y - 18, 20, 18);

        gc.setFill(Color.GREEN);
        gc.fillOval(x - 8, y - 20, 16, 16);
    }

    private void drawBush(int x, int y) {
        gc.setFill(Color.DARKGREEN);
        gc.fillOval(x - 6, y - 4, 12, 8);
        gc.fillOval(x - 4, y - 6, 10, 10);
        gc.fillOval(x - 8, y - 2, 10, 8);

        gc.setFill(Color.GREEN);
        gc.fillOval(x - 5, y - 5, 10, 8);
    }

    private void drawRock(int x, int y) {
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(x - 4, y - 3, 9, 6);
        gc.fillOval(x - 3, y - 4, 7, 8);

        gc.setFill(Color.LIGHTGRAY);
        gc.fillOval(x - 2, y - 2, 3, 2);
    }

    private Color getAnimalColor(AnimalType type) {
        switch (type) {
            case DEER: return Color.SANDYBROWN;
            case WOLF: return Color.DARKSLATEGRAY;
            case RABBIT: return Color.GHOSTWHITE;
            case BEAR: return Color.DARKRED;
            default: return Color.ORANGERED;
        }
    }

    private Color getBonusColor(BonusType type) {
        switch (type) {
            case HEALTH: return Color.RED;
            case AMMO: return Color.GOLD;
            case SCORE_BOOST: return Color.DEEPSKYBLUE;
            default: return Color.WHITE;
        }
    }

    private void drawHealthBar(Animal animal, int x, int y) {
        int barWidth = 20;
        int barHeight = 3;
        int barX = x - barWidth / 2;
        int barY = y - 15;

        gc.setFill(Color.BLACK);
        gc.fillRect(barX, barY, barWidth, barHeight);

        double healthPercent = (double) animal.getHealth() / animal.getMaxHealth();
        Color healthColor;
        if (healthPercent > 0.6) healthColor = Color.GREEN;
        else if (healthPercent > 0.3) healthColor = Color.YELLOW;
        else healthColor = Color.RED;

        gc.setFill(healthColor);
        gc.fillRect(barX, barY, (int)(barWidth * healthPercent), barHeight);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(0.5);
        gc.strokeRect(barX, barY, barWidth, barHeight);
    }

    private Color getBonusGlowColor(BonusType type) {
        switch (type) {
            case HEALTH: return Color.rgb(255, 100, 100, 0.4);
            case AMMO: return Color.rgb(255, 215, 0, 0.4);
            case SCORE_BOOST: return Color.rgb(100, 200, 255, 0.4);
            default: return Color.rgb(255, 255, 255, 0.4);
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}