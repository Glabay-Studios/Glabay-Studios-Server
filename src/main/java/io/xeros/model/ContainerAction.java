package io.xeros.model;

public class ContainerAction {

    private final ContainerActionType type;
    private final int interfaceId;
    private final int itemId;
    private final int slotId;
    private final int itemAmount;

    public ContainerAction(ContainerActionType type, int interfaceId, int itemId, int slotId) {
        this(type, interfaceId, itemId, slotId, 0);
    }

    public ContainerAction(ContainerActionType type, int interfaceId, int itemId, int slotId, int itemAmount) {
        this.type = type;
        this.interfaceId = interfaceId;
        this.itemId = itemId;
        this.slotId = slotId;
        this.itemAmount = itemAmount;
    }

    public ContainerActionType getType() {
        return type;
    }

    public int getInterfaceId() {
        return interfaceId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getSlotId() {
        return slotId;
    }

    public int getItemAmount() {
        return itemAmount;
    }
}
