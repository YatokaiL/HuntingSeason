package ru.rsreu.Prilepskij0215.model.map;

import com.prutzkow.projectlogger.ProjectLogger;
import ru.rsreu.Prilepskij0215.model.entities.Animal;
import ru.rsreu.Prilepskij0215.model.entities.Bonus;
import ru.rsreu.Prilepskij0215.model.entities.Hunter;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class GameMap {
    private static final Logger logger = ProjectLogger.logger;

    private final int width = 1000;
    private final int height = 800;
    private Hunter hunter;
    private final CopyOnWriteArrayList<Animal> animals;
    private final CopyOnWriteArrayList<Bonus> bonuses;
    private final ReentrantReadWriteLock lock;

    public GameMap() {
        logger.config("Initializing the game card");
        this.hunter = new Hunter(width / 2, height / 2);
        this.animals = new CopyOnWriteArrayList<>();
        this.bonuses = new CopyOnWriteArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        logger.config("The game card has been initialized: " + width + "x" + height);
    }

    public void moveHunter(int deltaX, int deltaY) {
        lock.writeLock().lock();
        try {
            int hunterSize = 24;
            int newX = Math.max(hunterSize/2, Math.min(width - hunterSize/2, hunter.getX() + deltaX));
            int newY = Math.max(hunterSize/2, Math.min(height - hunterSize/2, hunter.getY() + deltaY));
            hunter.move(newX, newY);
            logger.finest(String.format("Hunter moved to (%d, %d)", newX, newY));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void addAnimal(Animal animal) {
        int animalSize = 16;
        int x = Math.max(animalSize/2, Math.min(width - animalSize/2, animal.getX()));
        int y = Math.max(animalSize/2, Math.min(height - animalSize/2, animal.getY()));
        animal.move(x, y);

        animals.add(animal);
        logger.fine(String.format("Animal added to the map: %s in (%d, %d)",
                animal.getType(), animal.getX(), animal.getY()));
    }

    public void removeAnimal(Animal animal) {
        if (animals.remove(animal)) {
            logger.fine(String.format("Animal removed from the card: %s", animal.getType()));
        }
    }

    public Animal getAnimalAt(int x, int y) {
        lock.readLock().lock();
        try {
            int hitRadius = 30;
            for (Animal animal : animals) {
                if (animal.isActive() &&
                        Math.abs(animal.getX() - x) < hitRadius &&
                        Math.abs(animal.getY() - y) < hitRadius) {
                    return animal;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void addBonus(Bonus bonus) {
        bonuses.add(bonus);
        logger.fine(String.format("Bonus added to map: %s at (%d, %d)",
                bonus.getType(), bonus.getX(), bonus.getY()));
    }

    public void removeBonus(Bonus bonus) {
        if (bonuses.remove(bonus)) {
            logger.fine(String.format("Bonus removed from map: %s", bonus.getType()));
        }
    }

    public Bonus getBonusAt(int x, int y) {
        lock.readLock().lock();
        try {
            int bonusRadius = 20;
            for (Bonus bonus : bonuses) {
                if (Math.abs(bonus.getX() - x) < bonusRadius &&
                        Math.abs(bonus.getY() - y) < bonusRadius) {
                    return bonus;
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Hunter getHunter() { return hunter; }
    public CopyOnWriteArrayList<Animal> getAnimals() { return animals; }
    public CopyOnWriteArrayList<Bonus> getBonuses() { return bonuses; }
}