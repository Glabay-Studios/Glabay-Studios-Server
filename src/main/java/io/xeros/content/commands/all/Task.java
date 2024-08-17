package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Task extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
    	if (!player.getSlayer().getTask().isPresent()) {
            player.sendMessage("You do not have a task, please talk with a slayer master!");
            return;
        }
    	if (player.getSlayer().getMaster() == 8623) {//konar slayer
        player.sendMessage("I currently have " + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getPrimaryName() + "'s to kill in "  + player.getKonarSlayerLocation() + ".");
        } else {
            player.sendMessage("I currently have " + player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getPrimaryName() + "'s to kill.");
        }

        player.getPA().closeAllWindows();
    }
    @Override
	public Optional<String> getDescription() {
		return Optional.of("Shows your current slayer task");
	}
}

