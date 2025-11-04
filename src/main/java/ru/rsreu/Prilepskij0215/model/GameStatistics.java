package ru.rsreu.Prilepskij0215.model;

import com.prutzkow.projectlogger.ProjectLogger;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class GameStatistics {
    private static final Logger logger = ProjectLogger.logger;

    private final AtomicInteger score;
    private final AtomicInteger health;
    private final AtomicLong gameTime;
    private final AtomicInteger shotsFired;
    private final AtomicInteger shotsHit;
    private final AtomicInteger reloadsCount;

    private long gameStartTime;

    public GameStatistics() {
        logger.config("Initializing game statistics");
        this.score = new AtomicInteger(0);
        this.health = new AtomicInteger(100);
        this.gameTime = new AtomicLong(0);
        this.shotsFired = new AtomicInteger(0);
        this.shotsHit = new AtomicInteger(0);
        this.reloadsCount = new AtomicInteger(0);
        this.gameStartTime = System.currentTimeMillis();
    }

    public void updateGameTime() {
        if (gameStartTime > 0) {
            gameTime.set(System.currentTimeMillis() - gameStartTime);
        }
    }

    public void reset() {
        logger.config("Resetting game statistics");
        score.set(0);
        health.set(100);
        gameTime.set(0);
        shotsFired.set(0);
        shotsHit.set(0);
        reloadsCount.set(0);
        gameStartTime = System.currentTimeMillis();
    }

    public void addScore(int points) {
        int newScore = score.addAndGet(points);
        logger.fine(String.format("Added %d points. Current account: %d", points, newScore));
    }

    public void reduceScore(int points) {
        int newScore = score.updateAndGet(current -> Math.max(0, current - points));
        logger.fine(String.format("Deducted %d points. Current account: %d", points, newScore));
    }

    public void reduceHealth(int damage) {
        int newHealth = health.updateAndGet(current -> Math.max(0, current - damage));
        logger.fine(String.format("Loss of health: -%d. Current health: %d", damage, newHealth));
    }

    public void addHealth(int heal) {
        int newHealth = health.updateAndGet(current -> Math.min(100, current + heal));
        logger.fine(String.format("Health recovery: +%d. Current health: %d", heal, newHealth));
    }

    public void incrementShotsFired() {
        shotsFired.incrementAndGet();
    }

    public void incrementShotsHit() {
        shotsHit.incrementAndGet();
    }

    public void incrementReloadsCount() {
        reloadsCount.incrementAndGet();
    }

    public double getAccuracy() {
        int fired = shotsFired.get();
        return fired > 0 ? (double) shotsHit.get() / fired * 100 : 0.0;
    }

    public void setScore(int score) { this.score.set(score); }
    public void setHealth(int health) { this.health.set(health); }
    public void setGameTime(long gameTime) { this.gameTime.set(gameTime); }
    public void setShotsFired(int shotsFired) { this.shotsFired.set(shotsFired); }
    public void setShotsHit(int shotsHit) { this.shotsHit.set(shotsHit); }
    public void setReloadsCount(int reloadsCount) { this.reloadsCount.set(reloadsCount); }

    public int getScore() { return score.get(); }
    public int getHealth() { return health.get(); }
    public long getGameTime() { return gameTime.get(); }

    public int getShotsFired() { return shotsFired.get(); }
    public int getShotsHit() { return shotsHit.get(); }
    public int getReloadsCount() { return reloadsCount.get(); }
}
