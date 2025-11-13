package ru.rsreu.Prilepskij0215.model.core;

import com.prutzkow.projectlogger.ProjectLogger;
import ru.rsreu.Prilepskij0215.model.GameStatistics;
import ru.rsreu.Prilepskij0215.model.entities.*;
import ru.rsreu.Prilepskij0215.model.events.EventType;
import ru.rsreu.Prilepskij0215.model.events.GameEvent;
import ru.rsreu.Prilepskij0215.model.events.GameEventListener;
import ru.rsreu.Prilepskij0215.model.events.tasks.AnimalSimulationTask;
import ru.rsreu.Prilepskij0215.model.map.GameMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class GameModel {
    private static final Logger logger = ProjectLogger.logger;

    private final GameMap gameMap;
    private final GameStatistics statistics;
    private final BlockingQueue<GameEvent> eventQueue;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isPaused;

    private ExecutorService gameExecutor;
    private ScheduledExecutorService resourceGenerationExecutor;
    private final List<GameEventListener> eventListeners = new CopyOnWriteArrayList<>();

    private Consumer<String> onGameOver;

    public GameModel() {
        logger.config("Initializing game model...");
        this.gameMap = new GameMap();
        this.statistics = new GameStatistics();
        this.eventQueue = new LinkedBlockingQueue<>();
        this.isRunning = new AtomicBoolean(false);
        this.isPaused = new AtomicBoolean(false);

        this.gameExecutor = Executors.newCachedThreadPool();
        this.resourceGenerationExecutor = Executors.newScheduledThreadPool(1);
        logger.config("The game model was initialized successfully");
    }

    public void setOnGameOver(Consumer<String> onGameOver) {
        this.onGameOver = onGameOver;
    }

    public void startGame() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting game");
            isPaused.set(false);
            startAnimalSimulation();
            startResourceGeneration();
            startEventProcessing();
            startObjectInfoCollection();
            statistics.reset();
            logger.info("Game started successfully");
        } else {
            logger.warning("Attempt to start already running game");
        }
    }

    public void pauseGame() {
        if (isRunning.get() && !isPaused.get()) {
            logger.info("Game paused");
            isPaused.set(true);
        }
    }

    public void resumeGame() {
        if (isRunning.get() && isPaused.get()) {
            logger.info("Game resumed");
            isPaused.set(false);
        }
    }
    public void stopGame() {
        if (isRunning.compareAndSet(true, false)) {
            logger.info("Stopping game");
            shutdownExecutors();
            logger.info("Game stopped");
        }
    }

    public void moveHunter(int deltaX, int deltaY) {
        if (!isPaused.get() && isRunning.get()) {
            logger.fine(String.format("Moving the hunter: deltaX=%d, deltaY=%d", deltaX, deltaY));
            gameMap.moveHunter(deltaX, deltaY);
            checkBonusCollection();
        }
    }

    public void shoot(int targetX, int targetY) {
        if (!isPaused.get() && isRunning.get()) {
            logger.fine(String.format("Shot at the coordinates: x=%d, y=%d", targetX, targetY));
            eventQueue.offer(new GameEvent(EventType.SHOOT, targetX, targetY));
        }
    }

    public GameMap getGameMap() { return gameMap; }
    public GameStatistics getStatistics() { return statistics; }
    public boolean isRunning() { return isRunning.get(); }
    public boolean isPaused() { return isPaused.get(); }

    private void startResourceGeneration() {
        logger.config("Starting resource generation");

        resourceGenerationExecutor.scheduleAtFixedRate(() -> {
            if (!isPaused.get()) {
                generateNewAnimal();
            }
        }, 0, 5, TimeUnit.SECONDS);

        resourceGenerationExecutor.scheduleAtFixedRate(() -> {
            if (!isPaused.get()) {
                generateNewBonus();
            }
        }, 0, 15, TimeUnit.SECONDS);
    }

    private void startEventProcessing() {
        logger.config("Starting event processing");
        gameExecutor.submit(() -> {
            logger.config("The event processing thread is running");
            while (isRunning.get()) {
                try {
                    GameEvent event = eventQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (event != null && !isPaused.get()) {
                        logger.fine("Event processing: " + event.getType());
                        processEvent(event);
                    }
                } catch (InterruptedException e) {
                    logger.warning("The event processing flow has been interrupted");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            logger.config("The event processing flow is completed");
        });
    }

    private void startAnimalSimulation() {
        logger.config("Running an animal simulation");
        for (Animal animal : gameMap.getAnimals()) {
            gameExecutor.submit(new AnimalSimulationTask(animal, eventQueue, () -> gameMap.getHunter()));
        }
        logger.config("Animal simulation is running");
    }

    private void generateNewAnimal() {
        Random random = new Random();
        int x = random.nextInt(gameMap.getWidth());
        int y = random.nextInt(gameMap.getHeight());

        Animal animal = new Animal(x, y, AnimalType.values()[random.nextInt(AnimalType.values().length)]);
        gameMap.addAnimal(animal);
        gameExecutor.submit(new AnimalSimulationTask(animal, eventQueue, () -> gameMap.getHunter()));
        logger.fine(String.format("A new animal has been generated: %s in (%d, %d)", animal.getType(), x, y));
    }

    private void generateNewBonus() {
        if (gameMap.getBonuses().size() >= 3) {
            return;
        }

        Random random = new Random();
        int x = random.nextInt(gameMap.getWidth());
        int y = random.nextInt(gameMap.getHeight());

        BonusType[] bonusTypes = BonusType.values();
        BonusType bonusType = bonusTypes[random.nextInt(bonusTypes.length)];

        Bonus bonus = new Bonus(x, y, bonusType);
        gameMap.addBonus(bonus);
        logger.fine(String.format("A new bonus has been generated: %s in (%d, %d)", bonusType, x, y));
    }

    private void checkBonusCollection() {
        Bonus collectedBonus = gameMap.getBonusAt(
                gameMap.getHunter().getX(),
                gameMap.getHunter().getY()
        );

        if (collectedBonus != null) {
            applyBonus(collectedBonus);
            gameMap.removeBonus(collectedBonus);
            logger.info(String.format("Bonus collected: %s", collectedBonus.getType()));
        }
    }

    private void applyBonus(Bonus bonus) {
        switch (bonus.getType()) {
            case HEALTH:
                statistics.addHealth(25);
                logger.info("Health bonus applied: +25 health");
                break;
            case AMMO:
                gameMap.getHunter().getWeapon().addAmmo(20);
                logger.info("Ammo bonus applied: +20 ammo");
                break;
            case SCORE_BOOST:
                statistics.addScore(50);
                logger.info("Score bonus applied: +50 points");
                break;
        }

        GameEvent bonusEvent = new GameEvent(EventType.BONUS_COLLECTED,
                bonus.getX(), bonus.getY());
        notifyEventListeners(bonusEvent);
    }

    private void processEvent(GameEvent event) {
        switch (event.getType()) {
            case SHOOT:
                handleShoot(event.getX(), event.getY());
                notifyEventListeners(event);
                break;
            case ANIMAL_ESCAPED:
                handleAnimalEscaped(event.getX(), event.getY());
                notifyEventListeners(event);
                break;
            case ANIMAL_ATTACK:
                handleAnimalAttack(event.getDamage());
                notifyEventListeners(event);
                break;
            case GAME_OVER:
                handleGameOver();
                notifyEventListeners(event);
                break;
            default:
                logger.warning("Unknown type of event: " + event.getType());
        }
    }

    private void handleShoot(int x, int y) {
        if (!gameMap.getHunter().getWeapon().shoot()) {
            logger.warning("Cannot shoot - no ammo!");
            return;
        }

        statistics.incrementShotsFired();

        Hunter hunter = gameMap.getHunter();
        int hunterX = hunter.getX();
        int hunterY = hunter.getY();

        double distance = Math.sqrt(Math.pow(x - hunterX, 2) + Math.pow(y - hunterY, 2));
        double accuracy = calculateAccuracy(distance, hunter.getWeapon().getType());

        Animal hitAnimal = findAnimalWithAccuracy(x, y, accuracy, hunter.getWeapon().getType());

        GameEvent shootEvent = new GameEvent(EventType.SHOOT_DETAIL, x, y);
        notifyEventListeners(shootEvent);

        if (hitAnimal != null) {

            int damage = calculateDamage(hunter.getWeapon().getType(), distance, hitAnimal.getType());
            hitAnimal.takeDamage(damage);

            GameEvent hitEvent = new GameEvent(EventType.ANIMAL_HIT, x, y, damage);
            notifyEventListeners(hitEvent);

            if (!hitAnimal.isActive()) {
                int points = hitAnimal.getType().getValue();
                statistics.addScore(points);
                statistics.incrementShotsHit();
                gameMap.removeAnimal(hitAnimal);
                logger.info(String.format("Hit! %s destroyed. +%d points", hitAnimal.getType(), points));
            } else {
                logger.info(String.format("Hit! %s took %d damage. Health: %d/%d",
                        hitAnimal.getType(), damage, hitAnimal.getHealth(), hitAnimal.getMaxHealth()));
            }
        } else {
            statistics.reduceHealth(3);
            int currentHealth = statistics.getHealth();
            logger.warning(String.format("Miss! Health penalty. Current health: %d", currentHealth));

            if (currentHealth <= 0) {
                String deathReason = "You ran out of health from missed shots!";
                logger.severe("Player lost all health! Game Over!");

                if (onGameOver != null) {
                    onGameOver.accept(deathReason);
                }
                eventQueue.offer(new GameEvent(EventType.GAME_OVER));
            }
        }
    }

    public void saveGame(String filename) {
        try {
            GameSaveData saveData = createSaveData();

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("saves/" + filename + ".sav"))) {
                oos.writeObject(saveData);
                logger.info("Game saved successfully: " + filename);

                GameEvent saveEvent = new GameEvent(EventType.GAME_SAVED);
                notifyEventListeners(saveEvent);
            }

        } catch (IOException e) {
            logger.severe("Failed to save game: " + e.getMessage());
        }
    }

    public void loadGame(String filename) {
        File file = new File("saves/" + filename + ".sav");

        if (!file.exists()) {
            logger.severe("Save file does not exist: " + file.getAbsolutePath());
            GameEvent errorEvent = new GameEvent(EventType.LOAD_ERROR);
            notifyEventListeners(errorEvent);
            return;
        }

        try {
            GameSaveData saveData;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(file))) {
                saveData = (GameSaveData) ois.readObject();
            }

            restoreFromSaveData(saveData);

            startGameWithoutReset();

            logger.info("Game loaded successfully: " + filename);

            GameEvent loadEvent = new GameEvent(EventType.GAME_LOADED);
            notifyEventListeners(loadEvent);

        } catch (IOException | ClassNotFoundException e) {
            logger.severe("Failed to load game: " + e.getMessage());
            e.printStackTrace();

            GameEvent errorEvent = new GameEvent(EventType.LOAD_ERROR);
            notifyEventListeners(errorEvent);
        }
    }

    private void startGameWithoutReset() {
        if (isRunning.compareAndSet(false, true)) {
            logger.info("Starting game without reset");
            isPaused.set(false);
            startAnimalSimulation();
            startResourceGeneration();
            startEventProcessing();
            startObjectInfoCollection();
            logger.info("Game started successfully (without reset)");
        } else {
            logger.warning("Attempt to start already running game");
        }
    }

    private GameSaveData createSaveData() {
        GameSaveData saveData = new GameSaveData();

        Hunter hunter = gameMap.getHunter();
        saveData.setHunterX(hunter.getX());
        saveData.setHunterY(hunter.getY());
        saveData.setCurrentWeapon(hunter.getWeapon().getType());

        var weapons = hunter.getAllWeapons();
        saveData.setRifleAmmo(weapons.get(WeaponType.RIFLE).getCurrentAmmo());
        saveData.setRifleTotalAmmo(weapons.get(WeaponType.RIFLE).getTotalAmmo());
        saveData.setShotgunAmmo(weapons.get(WeaponType.SHOTGUN).getCurrentAmmo());
        saveData.setShotgunTotalAmmo(weapons.get(WeaponType.SHOTGUN).getTotalAmmo());
        saveData.setPistolAmmo(weapons.get(WeaponType.PISTOL).getCurrentAmmo());
        saveData.setPistolTotalAmmo(weapons.get(WeaponType.PISTOL).getTotalAmmo());

        List<AnimalSaveData> animalData = new ArrayList<>();
        for (Animal animal : gameMap.getAnimals()) {
            AnimalSaveData animalSave = new AnimalSaveData();
            animalSave.setX(animal.getX());
            animalSave.setY(animal.getY());
            animalSave.setType(animal.getType());
            animalSave.setHealth(animal.getHealth());
            animalSave.setActive(animal.isActive());
            animalSave.setCreateTime(animal.getCreateTime());
            animalData.add(animalSave);
        }
        saveData.setAnimals(animalData);

        List<BonusSaveData> bonusData = new ArrayList<>();
        for (Bonus bonus : gameMap.getBonuses()) {
            BonusSaveData bonusSave = new BonusSaveData();
            bonusSave.setX(bonus.getX());
            bonusSave.setY(bonus.getY());
            bonusSave.setType(bonus.getType());
            bonusData.add(bonusSave);
        }
        saveData.setBonuses(bonusData);

        saveData.setScore(statistics.getScore());
        saveData.setHealth(statistics.getHealth());
        saveData.setGameTime(statistics.getGameTime());
        saveData.setShotsFired(statistics.getShotsFired());
        saveData.setShotsHit(statistics.getShotsHit());
        saveData.setReloadsCount(statistics.getReloadsCount());

        return saveData;
    }

    private void restoreFromSaveData(GameSaveData saveData) {
        Hunter hunter = gameMap.getHunter();
        hunter.move(saveData.getHunterX(), saveData.getHunterY());
        hunter.switchWeapon(saveData.getCurrentWeapon());

        var weapons = hunter.getAllWeapons();
        weapons.get(WeaponType.RIFLE).setAmmo(saveData.getRifleAmmo(), saveData.getRifleTotalAmmo());
        weapons.get(WeaponType.SHOTGUN).setAmmo(saveData.getShotgunAmmo(), saveData.getShotgunTotalAmmo());
        weapons.get(WeaponType.PISTOL).setAmmo(saveData.getPistolAmmo(), saveData.getPistolTotalAmmo());

        gameMap.getAnimals().clear();
        for (AnimalSaveData animalSave : saveData.getAnimals()) {
            Animal animal = new Animal(animalSave.getX(), animalSave.getY(), animalSave.getType());
            animal.setHealth(animalSave.getHealth());
            if (!animalSave.isActive()) {
                animal.deactivate();
            }
            animal.setCreateTime(animalSave.getCreateTime());
            gameMap.getAnimals().add(animal);
        }

        gameMap.getBonuses().clear();
        for (BonusSaveData bonusSave : saveData.getBonuses()) {
            Bonus bonus = new Bonus(bonusSave.getX(), bonusSave.getY(), bonusSave.getType());
            gameMap.getBonuses().add(bonus);
        }

        statistics.setScore(saveData.getScore());
        statistics.setHealth(saveData.getHealth());
        statistics.setGameTime(saveData.getGameTime());
        statistics.setShotsFired(saveData.getShotsFired());
        statistics.setShotsHit(saveData.getShotsHit());
        statistics.setReloadsCount(saveData.getReloadsCount());

        logger.info("Game state restored from save data");

        restartAnimalSimulation();
    }
    private void restartAnimalSimulation() {
        logger.config("Restarting animal simulation after load");

        shutdownExecutors();

        this.gameExecutor = Executors.newCachedThreadPool();
        this.resourceGenerationExecutor = Executors.newScheduledThreadPool(1);

        startAnimalSimulation();
        startResourceGeneration();
        startEventProcessing();
        startObjectInfoCollection();

        logger.config("Animal simulation restarted successfully");
    }

    private double calculateAccuracy(double distance, WeaponType weaponType) {
        double baseAccuracy = weaponType.getAccuracy();

        if (distance <= 100) {
            return baseAccuracy;
        } else if (distance <= 300) {
            return baseAccuracy * (1.0 - (distance - 100) / 400.0);
        } else {
            return baseAccuracy * 0.3;
        }
    }

    private Animal findAnimalWithAccuracy(int targetX, int targetY, double accuracy, WeaponType weaponType) {
        if (Math.random() > accuracy) {
            return null;
        }

        int maxSpread;
        switch (weaponType) {
            case RIFLE: maxSpread = 15; break;
            case SHOTGUN: maxSpread = 40; break;
            case PISTOL: maxSpread = 25; break;
            default: maxSpread = 20;
        }

        double distance = Math.sqrt(Math.pow(targetX - gameMap.getHunter().getX(), 2) +
                Math.pow(targetY - gameMap.getHunter().getY(), 2));
        double distanceFactor = Math.min(2.0, 1.0 + distance / 300.0);

        int spread = (int)(maxSpread * distanceFactor * (1.0 - accuracy));
        int actualX = targetX + (int)((Math.random() - 0.5) * 2 * spread);
        int actualY = targetY + (int)((Math.random() - 0.5) * 2 * spread);

        return gameMap.getAnimalAt(actualX, actualY);
    }

    private int calculateDamage(WeaponType weaponType, double distance, AnimalType animalType) {
        int baseDamage = weaponType.getDamage();

        double distanceModifier;
        if (distance <= 50) {
            distanceModifier = 1.0;
        } else if (distance <= 200) {
            distanceModifier = 0.8 + (0.2 * (1.0 - (distance - 50) / 150.0));
        } else {
            distanceModifier = 0.5;
        }

        double armorModifier = getArmorModifier(animalType);

        return (int)(baseDamage * distanceModifier * armorModifier);
    }

    private double getArmorModifier(AnimalType animalType) {
        switch (animalType) {
            case RABBIT: return 1.2;
            case DEER: return 1.0;
            case WOLF: return 0.9;
            case BEAR: return 0.7;
            default: return 1.0;
        }
    }

    private void handleAnimalAttack(int damage) {
        if (!isPaused.get() && isRunning.get()) {
            statistics.reduceHealth(damage);
            int currentHealth = statistics.getHealth();

            logger.warning(String.format("Player attacked! Lost %d health. Current health: %d",
                    damage, currentHealth));

            if (currentHealth <= 0) {
                String deathReason = "You were killed by " + (damage == 15 ? "wolf" : "bear") + "!";
                logger.severe("Player died from animal attack! Game Over!");

                if (onGameOver != null) {
                    onGameOver.accept(deathReason);
                }
                eventQueue.offer(new GameEvent(EventType.GAME_OVER));
            }
        }
        GameEvent attackEvent = new GameEvent(EventType.ANIMAL_ATTACK_DETAIL, -1, -1, damage);
        notifyEventListeners(attackEvent);
    }

    private void handleAnimalEscaped(int x, int y) {
        statistics.reduceScore(5);
        logger.info("The animal is gone. -5 points");
    }

    private void handleGameOver() {
        logger.info("The Game Over event has been received");
        stopGame();

        if (onGameOver != null) {
            onGameOver.accept("You dead!");
        }
    }

    public void addEventListener(GameEventListener listener) {
        if (listener != null && !eventListeners.contains(listener)) {
            eventListeners.add(listener);
            logger.fine("Event listener added: " + listener.getClass().getSimpleName());
        }
    }

    private void notifyEventListeners(GameEvent event) {
        if (event == null || eventListeners.isEmpty()) {
            return;
        }

        for (GameEventListener listener : eventListeners) {
            try {
                listener.onGameEvent(event);
            } catch (Exception e) {
                logger.warning("Error notifying event listener: " + e.getMessage());
            }
        }
    }

    private void startObjectInfoCollection() {
        logger.config("Starting object information collection thread");

        Thread infoCollectionThread = new Thread(() -> {
            logger.config("Object information collection thread started");

            while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    if (!isPaused.get()) {
                        GameEvent stateEvent = new GameEvent(
                                EventType.GAME_STATE_UPDATE,
                                statistics.getScore(),
                                statistics.getHealth()
                        );
                        notifyEventListeners(stateEvent);
                    }
                    Thread.sleep(500); // Каждые 500 мс
                } catch (InterruptedException e) {
                    logger.fine("Object information collection thread interrupted");
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            logger.config("Object information collection thread stopped");
        });

        infoCollectionThread.setName("ObjectInfoCollector");
        infoCollectionThread.setDaemon(true);
        infoCollectionThread.start();
    }

    private void shutdownExecutors() {
        logger.config("Shutting down executors");

        if (gameExecutor != null && !gameExecutor.isShutdown()) {
            gameExecutor.shutdownNow();
            try {
                if (!gameExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    logger.warning("gameExecutor did not finish on time");
                }
            } catch (InterruptedException e) {
                logger.severe("Interruption during gameExecutor shutdown");
                Thread.currentThread().interrupt();
            }
        }

        if (resourceGenerationExecutor != null && !resourceGenerationExecutor.isShutdown()) {
            resourceGenerationExecutor.shutdownNow();
            try {
                if (!resourceGenerationExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                    logger.warning("resourceGenerationExecutor did not finish on time");
                }
            } catch (InterruptedException e) {
                logger.severe("Interruption during resourceGenerationExecutor shutdown");
                Thread.currentThread().interrupt();
            }
        }

        logger.config("Executors shutdown completed");
    }
}