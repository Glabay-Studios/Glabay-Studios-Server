package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Olddice extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		if (c.getPosition().inWild()) {
			return;
		}
		c.getPA().spellTeleport(2440, 3089, 0, false);
		c.sendMessage("@red@[WARNING] Recording can help if a scam occurs, use ::grules for rules.");

	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to gambling area");
	}

}
