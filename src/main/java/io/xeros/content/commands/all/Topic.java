package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Topic extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            int id = Integer.parseInt(input);
            player.getPA().openWebAddress(Configuration.FORUM_TOPIC_URL + id + "-1");
        } catch (Exception e) {
            player.sendMessage("Invalid format: ::topic 124");
            e.printStackTrace(System.err);
        }
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Open a forum topic by the id.");
    }
}
