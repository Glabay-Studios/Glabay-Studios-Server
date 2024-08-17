package io.xeros.model;

public enum ContainerActionType {
    ACTION_1(1),
    ACTION_2(2),
    ACTION_3(3),
    ACTION_4(4),
    ACTION_5(5),
    ACTION_6(6),
    ACTION_7(7),
    
    X(99),
    ;

    private final int intValue;

    ContainerActionType(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }
}
