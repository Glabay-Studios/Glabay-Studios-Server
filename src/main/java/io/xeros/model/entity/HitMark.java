package io.xeros.model.entity;

import io.xeros.content.combat.Hitmark;

public class HitMark {

    private Hitmark type;
    private int damage;
    private int delay;
    private int damageType;

    // Constructor with all parameters
    public HitMark(Hitmark type, int damage, int delay, int damageType) {
        this.type = type;
        this.damage = damage;
        this.delay = delay;
        this.damageType = damageType;
    }

    // Overloaded constructor for default 'delay'
    public HitMark(Hitmark type, int damage, int damageType) {
        this(type, damage, 0, damageType); // Calling the main constructor with 'delay' as 0
    }

    // Getters and Setters
    public Hitmark getType() {
        return type;
    }

    public void setType(Hitmark type) {
        this.type = type;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDamageType() {
        return damageType;
    }

    public void setDamageType(int damageType) {
        this.damageType = damageType;
    }

    // Optionally, toString method for easy printing
    @Override
    public String toString() {
        return "HitMark{" +
                "type=" + type +
                ", damage=" + damage +
                ", delay=" + delay +
                ", damageType=" + damageType +
                '}';
    }
}