package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Outlast extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage("@red@[OUTLAST]@blu@ Bank your items and enter the portal to join the tournament! Good Luck!");
    	c.getPA().spellTeleport(3080, 3510, 0, false);
}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Quick teleport to outlast teleport.");
	}

}
