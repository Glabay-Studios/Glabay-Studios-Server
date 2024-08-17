package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Unjails the player.
 * 
 * @author Emiel
 */
public class Unjail extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {

//		if (c.getPosition().isInJail()) {
//			if (c.jailEnd <= System.currentTimeMillis()) {
//				c.setTeleportToX(3093);
//				c.setTeleportToY(3493);
//				c.jailEnd = 0;
//				c.sendMessage("You've been unjailed. Don't get jailed again!");
//			} else {
//				long duration = (long) Math.ceil((double) (c.jailEnd - System.currentTimeMillis()) / 1000 / 60);
//				c.sendMessage("You need to wait " + duration + " more minutes before you can ::unjail yourself.");
//			}
//		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you out of the jail if you did your time");
	}

}
