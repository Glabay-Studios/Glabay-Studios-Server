package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.util.discord.Discord;

import java.util.Optional;

public class Suggestion extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (input == null) {
			c.sendMessage("Please redo your message.");
			return;
		}
		Discord.writeSuggestionMessage(c.getDisplayName() + ": " + input);
		c.sendMessage("Your suggestion has been sent to the staff.");
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Suggestion that we add or change something");
	}
}
