package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;


public class Droptable extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        Server.getDropManager().openDefault(c);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Opens the drop table.");
    }

}
