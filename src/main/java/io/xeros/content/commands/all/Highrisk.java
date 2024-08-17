package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Highrisk extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
//        if (Server.getMultiplayerSessionListener().inAnySession(c)) {
//            return;
//        }
//        if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
//            c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
//            return;
//        }
//        if (c.getPosition().inWild()) {
//            c.sendMessage("You can't use this command in the wilderness.");
//            return;
//        }
//        c.getPA().spellTeleport(3093, 3495, 8, false);
//        c.sendMessage("@red@If you leave the bank then you are in the wilderness...");
//        c.sendMessage("@red@You can not use the protect item prayer or protect items here.");
//        c.sendMessage("@red@Type ::home if you want to go home or use the home teleport.");
        c.sendMessage("This area is currently unavailable.");
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleports you to a instanced edgeville");
    }

}
