package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Chestrewards extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		//c.getPA().sendFrame126("https://www.sovark.com/topic/144-chest-rewards/", 12000);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Lets you know the chests rewards.");
	}
}
