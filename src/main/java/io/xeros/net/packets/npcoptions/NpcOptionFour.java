package io.xeros.net.packets.npcoptions;

import io.xeros.Server;
import io.xeros.content.skills.agility.AgilityHandler;
import io.xeros.content.skills.slayer.SlayerRewardsInterface;
import io.xeros.content.skills.slayer.SlayerRewardsInterfaceData;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;

/*
 * @author Matt
 * Handles all 4th options on non playable characters.
 */

public class NpcOptionFour {

	public static void handleOption(Player player, int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		if (PetHandler.isPet(npcType)) {
			if (PetHandler.getOptionForNpcId(npcType) == "fourth") {
				if (PetHandler.pickupPet(player, npcType, true))
					return;
			}
		}
		player.clickNpcType = 0;
		player.clickedNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;
		switch (npcType) {
		case 17: //Rug merchant - Sophanem
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3285, 2815, 0, "You step on the carpet and take off...", "at last you end up in sophanem.", 3);
			break;

		case 2580:
			player.getPA().startTeleport(3039, 4788, 0, "modern", false);
			player.teleAction = -1;
			break;

		case 402:
		case 401:
		case 405:
		case 6797:
		case 7663:
		case 8761:
		case 5870:
			SlayerRewardsInterface.open(player, SlayerRewardsInterfaceData.Tab.TASK);
			//player.getSlayer().handleInterface("buy");
			break;
			
		case 1501:
			player.getShops().openShop(23);
			break;

		case 308:
			player.getDH().sendDialogues(545, npcType);
			break;
		}
	}

}
