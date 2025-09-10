package ru.rsreu.Prilepskij0215.model.entities;

public enum AnimalType {
    DEER(50, 700, 5, 60, false, 0),
    WOLF(30, 500, 10, 45, true, 15),
    RABBIT(10, 300, 15, 20, false, 0),
    BEAR(100, 1000, 3, 120, true, 25);

    private final int value;
    private final int baseSpeed;
    private final int moveRange;
    private final int maxHealth;
    private final boolean isAggressive;
    private final int attackDamage;

    AnimalType(int value, int baseSpeed, int moveRange, int maxHealth, boolean isAggressive, int attackDamage) {
        this.value = value;
        this.baseSpeed = baseSpeed;
        this.moveRange = moveRange;
        this.maxHealth = maxHealth;
        this.isAggressive = isAggressive;
        this.attackDamage = attackDamage;
    }

    public int getValue() { return value; }
    public int getBaseSpeed() { return baseSpeed; }
    public int getMoveRange() { return moveRange; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isAggressive() { return isAggressive; }
    public int getAttackDamage() { return attackDamage; }
}