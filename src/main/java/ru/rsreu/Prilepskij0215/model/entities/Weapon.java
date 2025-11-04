package ru.rsreu.Prilepskij0215.model.entities;

import java.util.concurrent.atomic.AtomicInteger;

public class Weapon {
    private final WeaponType type;
    private final AtomicInteger currentAmmo;
    private final AtomicInteger totalAmmo;

    public Weapon(WeaponType type) {
        this.type = type;
        this.currentAmmo = new AtomicInteger(type.getMaxAmmo());
        this.totalAmmo = new AtomicInteger(type.getMaxAmmo() * 2);
    }

    public boolean shoot() {
        if (currentAmmo.get() > 0) {
            currentAmmo.decrementAndGet();
            return true;
        }
        return false;
    }

    public boolean reload() {
        if (currentAmmo.get() == type.getMaxAmmo()) {
            return false;
        }

        int needed = type.getMaxAmmo() - currentAmmo.get();
        if (totalAmmo.get() >= needed) {
            totalAmmo.addAndGet(-needed);
            currentAmmo.set(type.getMaxAmmo());
            return true;
        } else if (totalAmmo.get() > 0) {
            currentAmmo.addAndGet(totalAmmo.get());
            totalAmmo.set(0);
            return true;
        }
        return false;
    }

    public void setAmmo(int current, int total) {
        this.currentAmmo.set(current);
        this.totalAmmo.set(total);
    }
    public void addAmmo(int amount) {
        totalAmmo.addAndGet(amount);
    }

    public WeaponType getType() { return type; }
    public int getCurrentAmmo() { return currentAmmo.get(); }
    public int getTotalAmmo() { return totalAmmo.get(); }

}