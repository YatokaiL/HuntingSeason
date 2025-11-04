package ru.rsreu.Prilepskij0215.model.core;

import ru.rsreu.Prilepskij0215.model.entities.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int hunterX;
    private int hunterY;
    private WeaponType currentWeapon;

    private int rifleAmmo;
    private int rifleTotalAmmo;
    private int shotgunAmmo;
    private int shotgunTotalAmmo;
    private int pistolAmmo;
    private int pistolTotalAmmo;

    private List<AnimalSaveData> animals = new ArrayList<>();

    private List<BonusSaveData> bonuses = new ArrayList<>();

    private int score;
    private int health;
    private long gameTime;
    private int shotsFired;
    private int shotsHit;
    private int reloadsCount;

    public GameSaveData() {}

    public int getHunterX() { return hunterX; }
    public void setHunterX(int hunterX) { this.hunterX = hunterX; }

    public int getHunterY() { return hunterY; }
    public void setHunterY(int hunterY) { this.hunterY = hunterY; }

    public WeaponType getCurrentWeapon() { return currentWeapon; }
    public void setCurrentWeapon(WeaponType currentWeapon) { this.currentWeapon = currentWeapon; }

    public int getRifleAmmo() { return rifleAmmo; }
    public void setRifleAmmo(int rifleAmmo) { this.rifleAmmo = rifleAmmo; }

    public int getRifleTotalAmmo() { return rifleTotalAmmo; }
    public void setRifleTotalAmmo(int rifleTotalAmmo) { this.rifleTotalAmmo = rifleTotalAmmo; }

    public int getShotgunAmmo() { return shotgunAmmo; }
    public void setShotgunAmmo(int shotgunAmmo) { this.shotgunAmmo = shotgunAmmo; }

    public int getShotgunTotalAmmo() { return shotgunTotalAmmo; }
    public void setShotgunTotalAmmo(int shotgunTotalAmmo) { this.shotgunTotalAmmo = shotgunTotalAmmo; }

    public int getPistolAmmo() { return pistolAmmo; }
    public void setPistolAmmo(int pistolAmmo) { this.pistolAmmo = pistolAmmo; }

    public int getPistolTotalAmmo() { return pistolTotalAmmo; }
    public void setPistolTotalAmmo(int pistolTotalAmmo) { this.pistolTotalAmmo = pistolTotalAmmo; }

    public List<AnimalSaveData> getAnimals() { return animals; }
    public void setAnimals(List<AnimalSaveData> animals) { this.animals = animals; }

    public List<BonusSaveData> getBonuses() { return bonuses; }
    public void setBonuses(List<BonusSaveData> bonuses) { this.bonuses = bonuses; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public long getGameTime() { return gameTime; }
    public void setGameTime(long gameTime) { this.gameTime = gameTime; }

    public int getShotsFired() { return shotsFired; }
    public void setShotsFired(int shotsFired) { this.shotsFired = shotsFired; }

    public int getShotsHit() { return shotsHit; }
    public void setShotsHit(int shotsHit) { this.shotsHit = shotsHit; }

    public int getReloadsCount() { return reloadsCount; }
    public void setReloadsCount(int reloadsCount) { this.reloadsCount = reloadsCount; }
}