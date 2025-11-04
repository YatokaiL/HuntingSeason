package ru.rsreu.Prilepskij0215.model.core;

import ru.rsreu.Prilepskij0215.model.entities.AnimalType;

import java.io.Serializable;

public class AnimalSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private AnimalType type;
    private int health;
    private boolean isActive;
    private long createTime;

    public AnimalSaveData() {}

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public AnimalType getType() { return type; }
    public void setType(AnimalType type) { this.type = type; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
}