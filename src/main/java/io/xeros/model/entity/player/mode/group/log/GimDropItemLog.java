package io.xeros.model.entity.player.mode.group.log;

import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;

public class GimDropItemLog {

    private final String displayName;
    private final GameItem gameItem;
    private final Position position;

    public GimDropItemLog(String displayName, GameItem gameItem, Position position) {
        this.displayName = displayName;
        this.gameItem = gameItem;
        this.position = position;
    }

    private GimDropItemLog() {
        displayName = null;
        gameItem = null;
        position = null;
    }

    @Override
    public String toString() {
        return getDisplayName() + " dropped " + getGameItem().getFormattedString() + " at " + getPosition().getFormattedString();
    }

    public String getDisplayName() {
        return displayName;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public Position getPosition() {
        return position;
    }
}
