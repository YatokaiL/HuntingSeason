package ru.rsreu.Prilepskij0215.model.entities;

import java.util.concurrent.atomic.AtomicBoolean;

public class Animal {
    private int x;
    private int y;
    private final AnimalType type;
    private final AtomicBoolean isActive;
    private long createTime;
    private int health;

    public Animal(int x, int y, AnimalType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isActive = new AtomicBoolean(true);
        this.createTime = System.currentTimeMillis();
        this.health = type.getMaxHealth();
    }

    public void takeDamage(int damage) {
        this.health -= damage;
        if (this.health <= 0) {
            deactivate();
        }
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void deactivate() {
        isActive.set(false);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public AnimalType getType() { return type; }
    public boolean isActive() { return isActive.get(); }
    public long getCreateTime() { return createTime; }
    public long getLifeTime() { return System.currentTimeMillis() - createTime; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return type.getMaxHealth(); }
}