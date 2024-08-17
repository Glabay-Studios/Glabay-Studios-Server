package io.xeros.model.entity.player;

import org.jetbrains.annotations.NotNull;

/**
 * @author Chris | 8/8/21
 */
public class ItemToDestroy {
    private final int itemId;
    private final int itemSlot;
    private final DestroyType type;

    public ItemToDestroy(final int itemId, int itemSlot, @NotNull final DestroyType type) {
        this.itemId = itemId;
        this.itemSlot = itemSlot;
        this.type = type;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemSlot() {
        return itemSlot;
    }

    public DestroyType getType() {
        return type;
    }
}
