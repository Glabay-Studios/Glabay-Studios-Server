package io.xeros.model.entity.player;

/**
 * @author Chris | 8/8/21
 */
public enum DestroyType {
    DESTROY("destroy"),
    DROP("drop"),
    LOW_ALCH("Low Alchemy"),
    HIGH_ALCH("High Alchemy")
    ;

    private final String text;

    DestroyType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
