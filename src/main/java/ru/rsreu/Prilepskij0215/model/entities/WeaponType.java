package ru.rsreu.Prilepskij0215.model.entities;

public enum WeaponType {
    RIFLE(30, 35, 0.9),
    SHOTGUN(8, 60, 0.6),
    PISTOL(50, 20, 0.85);

    private final int maxAmmo;
    private final int damage;
    private final double accuracy;

    WeaponType(int maxAmmo, int damage, double accuracy) {
        this.maxAmmo = maxAmmo;
        this.damage = damage;
        this.accuracy = accuracy;
    }

    public int getMaxAmmo() { return maxAmmo; }
    public int getDamage() { return damage; }
    public double getAccuracy() { return accuracy; }
}
