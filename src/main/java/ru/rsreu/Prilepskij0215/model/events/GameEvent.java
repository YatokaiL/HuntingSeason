package ru.rsreu.Prilepskij0215.model.events;

public class GameEvent {
    private final EventType type;
    private final int x;
    private final int y;
    private final int damage;

    public GameEvent(EventType type) {
        this(type, -1, -1, 0);
    }

    public GameEvent(EventType type, int x, int y) {
        this(type, x, y, 0);
    }

    public GameEvent(EventType type, int x, int y, int damage) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.damage = damage;
    }

    public EventType getType() { return type; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getDamage() { return damage; }
}
