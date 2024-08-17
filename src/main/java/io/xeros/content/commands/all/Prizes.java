package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.item.lootable.LootableInterface;
import io.xeros.model.entity.player.Player;

/**
 * Open the mbox in the default web browser.
 * 
 * @author Noah
 */
public class Prizes extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LootableInterface.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the loot table interface.");
	}

}
