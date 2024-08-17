package io.xeros.model.entity.healthbar;

import lombok.Getter;

public class DynamicHealthBarUpdate extends HealthBarUpdate {
    @Getter
    private final int startHealth;
    @Getter
    private final int endHealth;
    @Getter
    private final int maxHealth;
    @Getter
    private final int decreaseSpeed;
    @Getter
    private final int delay;
    @Getter
    private final int startBarWidth;
    @Getter
    private final int endBarWidth;

    public DynamicHealthBarUpdate(int id, int startHealth, int endHealth, int maxHealth, int decreaseSpeed, int delay) {
        super(id);
        this.startHealth = startHealth;
        this.endHealth = endHealth;
        this.maxHealth = maxHealth;
        this.decreaseSpeed = decreaseSpeed;
        this.delay = delay;
        this.startBarWidth = (int) (((double) startHealth / maxHealth) * this.template.getWidth());
        this.endBarWidth = (int) (((double) endHealth / maxHealth) * this.template.getWidth());
    }

}