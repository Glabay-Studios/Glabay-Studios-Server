package io.xeros.model;

import io.xeros.model.entity.Entity;

/**
 * Represents a square segment of the map.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class SquareArea implements Area {

    private final int lowX;
    private final int lowY;
    private final int highX;
    private final int highY;

    public SquareArea(int lowX, int highY, int highX, int lowY) {
        this.lowX = lowX;
        this.lowY = lowY;
        this.highX = highX;
        this.highY = highY;
    }

    public boolean inside(Entity entity) {
        return entity.getX() >= lowX && entity.getY() >= lowY && entity.getX() <= highX && entity.getY() <= highY;
    }

    public int getLowX() {
        return lowX;
    }

    public int getLowY() {
        return lowY;
    }

    public int getHighX() {
        return highX;
    }

    public int getHighY() {
        return highY;
    }
}
