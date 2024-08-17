package io.xeros.content.commands.all;

import java.util.Objects;
import java.util.Optional;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Close extends Command {

	@Override
		public void execute(Player c, String commandName, String input) {
			if (c.getInterfaceEvent().isActive()) {
				c.sendMessage("You cannot close the random event..");
				return;
			}
			if (c.isDead || c.getHealth().getCurrentHealth() <= 0) {
				c.sendMessage("You are dead, you cannot do this.");
				return;
			}
			if (!Objects.equals(c.rottenPotatoOption, "")) {
				c.rottenPotatoOption = "";
			}
			if (c.viewingPresets) {
				c.sendMessage("You cannot do this right now.");
				return;
			}
			if (c.getCurrentCombination().isPresent()) {
				c.setCurrentCombination(Optional.empty());
			}
			if (c.getPA().viewingOtherBank) {
				c.getPA().resetOtherBank();
			}
			if (c.isStuck) {
				c.isStuck = false;
				c.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.getBankPin().open(2);
				return;
			}
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("You must end your current session to do this.");
				return;
			}
			if (c.canChangeAppearance) {
				c.canChangeAppearance = false;
			}

			if (System.currentTimeMillis() - c.lastCloseOfInterface < 4000) {
				//c.sendMessage("Wait a couple of seconds before attempting to do this again.");
				return;
			}
			c.lastCloseOfInterface = System.currentTimeMillis();
			c.getPA().closeAllWindows();
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Closes your current interface");
	}

}
