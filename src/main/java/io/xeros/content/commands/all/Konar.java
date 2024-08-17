package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Konar extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		c.getPA().startTeleport(1311, 3795, 0, "modern", false);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Takes you to konar slayer master.");
	}
}
