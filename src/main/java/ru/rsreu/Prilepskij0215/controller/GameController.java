package ru.rsreu.Prilepskij0215.controller;

import ru.rsreu.Prilepskij0215.model.core.GameModel;
import com.prutzkow.projectlogger.ProjectLogger;
import ru.rsreu.Prilepskij0215.model.entities.Hunter;
import ru.rsreu.Prilepskij0215.model.entities.Weapon;
import ru.rsreu.Prilepskij0215.model.entities.WeaponType;
import ru.rsreu.Prilepskij0215.model.map.GameMap;
import ru.rsreu.Prilepskij0215.model.GameStatistics;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import java.util.function.Consumer;

public class GameController {
    private static final Logger logger = ProjectLogger.logger;

    private final GameModel gameModel;
    private Consumer<String> onGameOver;

    public GameController(GameModel gameModel) {
        logger.config("Initializing the game controller");
        this.gameModel = gameModel;
        this.gameModel.setOnGameOver(this::handleGameOver);
    }

    public void setOnGameOver(Consumer<String> onGameOver) {
        this.onGameOver = onGameOver;
    }

    private void handleGameOver(String deathReason) {
        if (onGameOver != null) {
            onGameOver.accept(deathReason);
        }
    }


    public void startGame() {
        logger.info("Controller: starting the game");
        gameModel.startGame();
    }

    public void pauseGame() {
        logger.info("Controller: pause the game");
        gameModel.pauseGame();
    }

    public void resumeGame() {
        logger.info("Controller: resuming the game");
        gameModel.resumeGame();
    }

    public void stopGame() {
        logger.info("Controller: stop the game");
        gameModel.stopGame();
    }

    public void moveHunter(int deltaX, int deltaY) {
        logger.fine(String.format("Controller: hunter movement (%d, %d)", deltaX, deltaY));
        gameModel.moveHunter(deltaX, deltaY);
    }

    public void shoot(int targetX, int targetY) {
        logger.fine(String.format("Controller: shot at (%d, %d)", targetX, targetY));
        gameModel.shoot(targetX, targetY);
    }

    public void reloadWeapon() {
        logger.fine("Controller: reloading weapon");

        Hunter hunter = gameModel.getGameMap().getHunter();
        boolean reloaded = hunter.getWeapon().reload();
        if (reloaded) {
            logger.info("Weapon reloaded successfully");
            gameModel.getStatistics().incrementReloadsCount();
        } else {
            logger.warning("Cannot reload - no ammo or already full");
        }
    }

    public void switchWeapon(WeaponType weaponType) {
        logger.fine("Controller: switching weapon to " + weaponType);
        gameModel.getGameMap().getHunter().switchWeapon(weaponType);
    }

    public GameState getGameState() {
        return new GameState(
                gameModel.getGameMap(),
                gameModel.getStatistics(),
                gameModel.isRunning(),
                gameModel.isPaused()
        );
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public static class GameState {
        private final GameMap gameMap;
        private final GameStatistics statistics;
        private final boolean isRunning;
        private final boolean isPaused;
        private final Weapon playerWeapon;

        public GameState(GameMap gameMap, GameStatistics statistics, boolean isRunning, boolean isPaused) {
            this.gameMap = gameMap;
            this.statistics = statistics;
            this.isRunning = isRunning;
            this.isPaused = isPaused;
            this.playerWeapon = gameMap.getHunter().getWeapon();
        }

        public GameMap getGameMap() { return gameMap; }
        public GameStatistics getStatistics() { return statistics; }
        public boolean isRunning() { return isRunning; }
        public boolean isPaused() { return isPaused; }
        public Weapon getPlayerWeapon() { return playerWeapon; }
    }
}