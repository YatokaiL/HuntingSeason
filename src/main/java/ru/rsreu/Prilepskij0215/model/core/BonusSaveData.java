package ru.rsreu.Prilepskij0215.model.core;

import ru.rsreu.Prilepskij0215.model.entities.BonusType;

import java.io.Serializable;

public class BonusSaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private BonusType type;

    public BonusSaveData() {}

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public BonusType getType() { return type; }
    public void setType(BonusType type) { this.type = type; }
}