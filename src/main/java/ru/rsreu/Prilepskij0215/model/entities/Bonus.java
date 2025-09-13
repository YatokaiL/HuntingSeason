package ru.rsreu.Prilepskij0215.model.entities;

public class Bonus {
    private int x;
    private int y;
    private final BonusType type;

    public Bonus(int x, int y, BonusType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public BonusType getType() { return type; }
}
