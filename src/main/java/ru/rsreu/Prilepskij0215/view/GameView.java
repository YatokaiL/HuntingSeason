package ru.rsreu.Prilepskij0215.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.rsreu.Prilepskij0215.controller.GameController;
import ru.rsreu.Prilepskij0215.model.entities.AnimalType;
import ru.rsreu.Prilepskij0215.model.entities.BonusType;

public class GameView {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final GameController controller;

    public GameView(GameController controller) {
        this.controller = controller;
        this.canvas = new Canvas(1000, 800);
        this.gc = canvas.getGraphicsContext2D();
        drawBackground();
    }

    private void drawBackground() {
        gc.setFill(Color.FORESTGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void update() {
        drawBackground();
        drawHunter();
        drawAnimals();
        drawBonuses();
    }

    private void drawHunter() {
        gc.setFill(Color.DARKBLUE);
        int hunterX = controller.getGameState().getGameMap().getHunter().getX();
        int hunterY = controller.getGameState().getGameMap().getHunter().getY();
        gc.fillOval(hunterX - 10, hunterY - 10, 20, 20);
    }

    private void drawAnimals() {
        var animals = controller.getGameState().getGameMap().getAnimals();
        for (var animal : animals) {
            gc.setFill(getAnimalColor(animal.getType()));
            gc.fillRect(animal.getX() - 8, animal.getY() - 8, 16, 16);
        }
    }

    private void drawBonuses() {
        var bonuses = controller.getGameState().getGameMap().getBonuses();
        for (var bonus : bonuses) {
            gc.setFill(getBonusColor(bonus.getType()));
            gc.fillRect(bonus.getX() - 6, bonus.getY() - 6, 12, 12);
        }
    }

    private Color getAnimalColor(AnimalType type) {
        switch (type) {
            case DEER: return Color.SANDYBROWN;
            case WOLF: return Color.GRAY;
            case RABBIT: return Color.WHITE;
            case BEAR: return Color.DARKRED;
            default: return Color.BLACK;
        }
    }

    private Color getBonusColor(BonusType type) {
        switch (type) {
            case HEALTH: return Color.RED;
            case AMMO: return Color.YELLOW;
            case SCORE_BOOST: return Color.GOLD;
            default: return Color.WHITE;
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}