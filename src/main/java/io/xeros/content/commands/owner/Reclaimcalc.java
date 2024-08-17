package io.xeros.content.commands.owner;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.sql.donation.model.DonationItem;
import io.xeros.sql.donation.model.DonationItemList;
import io.xeros.sql.donation.query.GetDonationsQuery;
import io.xeros.sql.donation.reclaim.ReclaimQuery;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Reclaimcalc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] names = input.split("-");

		if (names.length < 2) {
			c.sendMessage("Two names required, ::reclaimcalc-player claiming-account they are claiming");
			return;
		}

		String name = names[0].toLowerCase();
		String oldUsername = names[1].toLowerCase();

		Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), (context, connection) -> {
			try {
				boolean usedOldReclaim = Server.getDatabaseManager().executeImmediate(Server.getEmbeddedDatabase(), (context2, connection2) -> {
					PreparedStatement select = connection2.prepareStatement("SELECT * FROM reclaimed_donations WHERE username = ?");
					select.setString(1, name.toLowerCase());
					ResultSet rs = select.executeQuery();
					return rs.next();
				});

				DonationItemList donations = context.executeImmediate(Server.getConfiguration().getStoreDatabase(), new GetDonationsQuery(oldUsername));
				List<DonationItem> v1Donations = donations.stream().filter(item -> item.isV1Donation() && item.isClaimed()).collect(Collectors.toList());
				int dollars = v1Donations.stream().mapToInt(DonationItem::getItemCost).sum();

				int newDollars = ReclaimQuery.getV1DonationDollars(connection, oldUsername);
				c.addQueuedAction(plr -> c.sendMessage(String.format("'%s', usedOld=%b, oldDollars=%d, newDollars=%d, difference=%s",
						name, usedOldReclaim, dollars, newDollars, newDollars - dollars)));
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace(System.err);
				c.addQueuedAction(plr -> plr.sendMessage("There was an error calculating old reclaim."));
			}

			return null;
		});
	}

}
