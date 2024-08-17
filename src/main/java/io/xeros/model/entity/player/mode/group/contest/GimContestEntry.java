package io.xeros.model.entity.player.mode.group.contest;

public class GimContestEntry {

    private final String groupName;
    private final ContestType type;
    private final long value;
    private final int rank;

    public GimContestEntry(String groupName, ContestType type, long value, int rank) {
        this.groupName = groupName;
        this.type = type;
        this.value = value;
        this.rank = rank;
    }

    public String getGroupName() {
        return groupName;
    }

    public ContestType getType() {
        return type;
    }

    public long getValue() {
        return value;
    }

    public int getRank() {
        return rank;
    }
}
