package io.xeros.content.commands.admin;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.content.commands.all.Claim;
import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.sql.donation.model.DonationItem;

import java.util.Arrays;
import java.util.Optional;

public class Givedonation extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] data = input.split("-");
            Player recipient = PlayerHandler.getPlayerByDisplayName(data[0]);
            int scroll = Integer.parseInt(data[1]);
            Optional<ClaimDonatorScrollDialogue.DonationScroll> donationScroll = Arrays.stream(ClaimDonatorScrollDialogue.DonationScroll.values()).filter(it -> it.getDonationAmount() == scroll).findFirst();
            int amount = data.length == 3 ? Integer.parseInt(data[2]) : 1;

            if (recipient == null) {
                player.sendMessage("No player online with name: " + data[0]);
                return;
            }

            if (!donationScroll.isPresent()) {
                player.sendMessage("No donation scroll with price: $" + scroll);
                return;
            }

            ClaimDonatorScrollDialogue.DonationScroll donationScroll1 = donationScroll.get();

            if (!Claim.giveDonationItem(recipient, new DonationItem(donationScroll1.getItemId(), amount, donationScroll1.name(), null, null))) {
                player.sendMessage("Player did not have space in inventory to receive the donation.");
            } else {
                player.sendMessage("Successfully gave " + donationScroll1.name().toLowerCase() + " x " + amount + " to '" + data[0] + "'.");
            }
        } catch (Exception e) {
            if (Server.isDebug()) {
                e.printStackTrace(System.err);
            }
            player.sendMessage("Error occurred, usage: ::givedonation-name-10/25/50/100/250/500-amount");
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Give a donation to a player, functionally the same as a manual ::claim.");
    }

}
