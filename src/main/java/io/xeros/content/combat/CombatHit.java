package io.xeros.content.combat;

public class CombatHit {

    public static CombatHit miss() {
        return new CombatHit(false, 0);
    }

    private final boolean success;
    private final int damage;

    public CombatHit(boolean success, int damage) {
        this.success = success;
        this.damage = damage;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean missed() {
        return !success;
    }

    public int getDamage() {
        return damage;
    }
}