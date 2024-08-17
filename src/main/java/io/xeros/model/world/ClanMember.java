package io.xeros.model.world;

import io.xeros.model.entity.player.Player;

public class ClanMember {

    private final String loginName;
    private final String displayName;

    public ClanMember(Player player) {
        this.loginName = player.getLoginName();
        this.displayName = player.getDisplayName();
    }

    public ClanMember(String loginName, String displayName) {
        this.loginName = loginName;
        this.displayName = displayName;
    }

    public boolean is(Player player) {
        return loginName.equalsIgnoreCase(player.getLoginName());
    }

    public boolean is(String name) {
        return loginName.equalsIgnoreCase(name) || displayName.equalsIgnoreCase(name);
    }

    public String getLoginName() {
        return loginName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
