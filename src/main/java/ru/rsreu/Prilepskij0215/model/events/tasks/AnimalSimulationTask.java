package ru.rsreu.Prilepskij0215.model.events.tasks;

import com.prutzkow.projectlogger.ProjectLogger;
import ru.rsreu.Prilepskij0215.model.entities.Animal;
import ru.rsreu.Prilepskij0215.model.entities.AnimalType;
import ru.rsreu.Prilepskij0215.model.entities.Hunter;
import ru.rsreu.Prilepskij0215.model.events.EventType;
import ru.rsreu.Prilepskij0215.model.events.GameEvent;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class AnimalSimulationTask implements Runnable {
    private static final Logger logger = ProjectLogger.logger;

    private final Animal animal;
    private final Random random;
    private final BlockingQueue<GameEvent> eventQueue;
    private final Supplier<Hunter> hunterSupplier;
    private final int maxX = 1000 - 8;
    private final int maxY = 800 - 8;

    private int currentDirectionX = 0;
    private int currentDirectionY = 0;
    private int directionChangeCounter = 0;
    private final int directionChangeFrequency;

    public AnimalSimulationTask(Animal animal, BlockingQueue<GameEvent> eventQueue, Supplier<Hunter> hunterSupplier) {
        this.animal = animal;
        this.random = new Random();
        this.eventQueue = eventQueue;
        this.hunterSupplier = hunterSupplier;

        this.directionChangeFrequency = getDirectionChangeFrequency(animal.getType());

        changeDirection();

        logger.fine(String.format("Animal simulation task created for %s", animal.getType()));
    }

    @Override
    public void run() {
        logger.fine(String.format("Starting simulation for animal %s", animal.getType()));

        while (animal.isActive()) {
            try {
                if (animal.getLifeTime() > 20000) {
                    logger.fine(String.format("Animal %s escaped after 20 seconds", animal.getType()));
                    eventQueue.offer(new GameEvent(EventType.ANIMAL_ESCAPED, animal.getX(), animal.getY()));
                    animal.deactivate();
                    break;
                }

                performNormalMovement();

                int sleepTime = getSleepTime(animal.getType());
                Thread.sleep(sleepTime);

            } catch (InterruptedException e) {
                logger.fine(String.format("Animal simulation %s interrupted", animal.getType()));
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.fine(String.format("Animal simulation %s completed", animal.getType()));
    }

    private void performNormalMovement() {
        directionChangeCounter++;

        if (shouldRunAway()) {
            performEscapeMovement();
            return;
        }

        if (directionChangeCounter >= directionChangeFrequency) {
            changeDirection();
            directionChangeCounter = 0;
        }

        int moveX = currentDirectionX * getMoveDistance(animal.getType());
        int moveY = currentDirectionY * getMoveDistance(animal.getType());

        int newX = Math.max(8, Math.min(maxX, animal.getX() + moveX));
        int newY = Math.max(8, Math.min(maxY, animal.getY() + moveY));

        animal.move(newX, newY);

        logger.finest(String.format("Animal %s moved to (%d, %d)",
                animal.getType(), newX, newY));
    }

    private boolean shouldRunAway() {
        Hunter hunter = hunterSupplier.get();
        if (hunter == null) return false;

        int distance = (int) Math.sqrt(
                Math.pow(animal.getX() - hunter.getX(), 2) +
                        Math.pow(animal.getY() - hunter.getY(), 2)
        );

        int escapeDistance = getEscapeDistance(animal.getType());
        return distance < escapeDistance;
    }

    private int getEscapeDistance(AnimalType type) {
        switch (type) {
            case RABBIT: return 100;
            case DEER: return 150;
            case WOLF: return 80;
            case BEAR: return 200;
            default: return 120;
        }
    }

    private void performEscapeMovement() {
        int escapeSpeed = getMoveDistance(animal.getType()) * 2; // Вдвое быстрее

        int hunterX = 500;
        int hunterY = 400;

        int deltaX = animal.getX() - hunterX;
        int deltaY = animal.getY() - hunterY;

        double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        if (length > 0) {
            currentDirectionX = (int)(deltaX / length * 3);
            currentDirectionY = (int)(deltaY / length * 3);
        }

        int newX = Math.max(8, Math.min(maxX, animal.getX() + currentDirectionX * escapeSpeed));
        int newY = Math.max(8, Math.min(maxY, animal.getY() + currentDirectionY * escapeSpeed));

        animal.move(newX, newY);

        logger.finest(String.format("Animal %s escaping to (%d, %d)",
                animal.getType(), newX, newY));
    }

    private void changeDirection() {
        switch (animal.getType()) {
            case RABBIT:
                currentDirectionX = random.nextInt(3) - 1;
                currentDirectionY = random.nextInt(3) - 1;
                break;
            case DEER:
                if (random.nextInt(4) == 0) {
                    currentDirectionX = random.nextInt(3) - 1;
                    currentDirectionY = random.nextInt(3) - 1;
                }
                break;
            case WOLF:
                if (random.nextInt(6) == 0) {
                    currentDirectionX = random.nextInt(3) - 1;
                    currentDirectionY = random.nextInt(3) - 1;
                }
                break;
            case BEAR:
                if (random.nextInt(10) == 0) {
                    currentDirectionX = random.nextInt(3) - 1;
                    currentDirectionY = random.nextInt(3) - 1;
                }
                break;
        }

        if (currentDirectionX == 0 && currentDirectionY == 0) {
            currentDirectionX = random.nextInt(2) == 0 ? -1 : 1;
        }
    }

    private int getSleepTime(AnimalType type) {
        int baseSpeed = type.getBaseSpeed();
        return baseSpeed + random.nextInt(baseSpeed / 2);
    }

    private int getMoveDistance(AnimalType type) {
        switch (type) {
            case RABBIT: return 8;
            case WOLF: return 6;
            case DEER: return 5;
            case BEAR: return 3;
            default: return 4;
        }
    }

    private int getDirectionChangeFrequency(AnimalType type) {
        switch (type) {
            case RABBIT: return 3;
            case WOLF: return 8;
            case DEER: return 5;
            case BEAR: return 15;
            default: return 5;
        }
    }
}