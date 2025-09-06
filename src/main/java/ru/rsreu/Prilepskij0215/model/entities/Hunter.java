package ru.rsreu.Prilepskij0215.model.entities;

import java.util.EnumMap;
import java.util.Map;

public class Hunter {
    private int x;
    private int y;
    private Weapon currentWeapon;
    private final Map<WeaponType, Weapon> weapons;

    public Hunter(int x, int y) {
        this.x = x;
        this.y = y;
        this.weapons = new EnumMap<>(WeaponType.class);

        for (WeaponType weaponType : WeaponType.values()) {
            weapons.put(weaponType, new Weapon(weaponType));
        }

        this.currentWeapon = weapons.get(WeaponType.RIFLE);
    }

    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void switchWeapon(WeaponType weaponType) {
        Weapon weapon = weapons.get(weaponType);
        if (weapon != null) {
            this.currentWeapon = weapon;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Weapon getWeapon() { return currentWeapon; }

    public Map<WeaponType, Weapon> getAllWeapons() {
        return new EnumMap<>(weapons);
    }
}