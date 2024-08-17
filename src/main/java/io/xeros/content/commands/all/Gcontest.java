package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.group.contest.GroupIronmanContest;

import java.util.Optional;


public class Gcontest extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		GroupIronmanContest.openInterface(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens the GIM contest interface.");
	}

}
