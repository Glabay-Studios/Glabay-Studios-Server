package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.sql.donation.reclaim.ReclaimDonationResponse;
import io.xeros.sql.donation.reclaim.ReclaimQuery;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ReclaimDonationLog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Optional;

/**
 * Show the current position.
 * 
 * @author Noah
 *
 */
public class Reclaim extends Command {

	public static final LocalDate START = Configuration.RECLAIM_DONATIONS_START_DATE;
	public static final LocalDate END = START.plus(7, ChronoUnit.DAYS);
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale.US);
	private static final String RECLAIM_USERNAME_ATTRIBUTE = "reclaim_username";
	private static final String RECLAIM_PASSWORD_ATTRIBUTE = "reclaim_password";
	private static final String RECLAIM_WHITELIST_START = "reclaim_whitelist_start";
	private static final String RECLAIM_WHITELIST_TIME = "reclaim_whitelist_time";

	public static void addReclaimWhitelist(Player player, int seconds) {
		player.getAttributes().setLong(RECLAIM_WHITELIST_START, System.currentTimeMillis());
		player.getAttributes().setLong(RECLAIM_WHITELIST_TIME, seconds);
	}

	public static boolean canReclaim(Player player) {
		long start = player.getAttributes().getLong(RECLAIM_WHITELIST_START, -1);
		long seconds = player.getAttributes().getLong(RECLAIM_WHITELIST_TIME, 0);
		if (start == -1 || (System.currentTimeMillis() - start) / 1_000 > seconds) {
			player.sendMessage("@red@Contact support to reclaim old donations.");
			return false;
		}

		return true;
	}

	public static boolean isReclaimPeriod() {
		LocalDate now = LocalDate.now();
		boolean before = now.isBefore(START);
		boolean after = now.isAfter(END);
		return !before && !after;
	}

	private enum Stage {
		INTRO_AND_ENTER_USER, ENTER_PASSWORD, VERIFICATION,
	}

	@Override
	public void execute(Player player, String commandName, String input) {
		boolean skipAuth = player.getRights().contains(Right.OWNER);
		if (!canReclaim(player)) {
			if (skipAuth) {
				player.sendMessage("Not on reclaim whitelist but skipping..");
			} else
				return;
		}

		LocalDate now = LocalDate.now();
		boolean before = now.isBefore(START);
		boolean after = now.isAfter(END);


		if (skipAuth) {
			player.sendMessage("Dev skip dates: " + START.format(DATE_FORMAT) + " - " + END.format(DATE_FORMAT));
			start(player);
		} else {
			if (before) {
				player.sendMessage("You can't reclaim donations until " + START.format(DATE_FORMAT) + ".");
			} else if (after) {
				player.sendMessage("Donation reclaim ended on " + END.format(DATE_FORMAT) + ".");
			} else {
				start(player);
			}
		}
	}

	private static void start(Player player) {
		if (!Server.getConfiguration().getServerState().isSqlEnabled()) {
			player.start(new DialogueBuilder(player).npc(Npcs.DONATOR_SHOP, "Database connections aren't enabled at the moment!"));
			return;
		}

		if (player.hitDatabaseRateLimit(true))
			return;

		dialogue(player, Stage.INTRO_AND_ENTER_USER);
	}

	private static void dialogue(Player player, Stage stage) {
		if (stage == Stage.INTRO_AND_ENTER_USER) {
			player.start(new DialogueBuilder(player)
					.npc(Npcs.DONATOR_SHOP, "Please enter the username and password of the account",
								"you entered when purchasing your previous donations.")
					.npc(Npcs.DONATOR_SHOP, "Enter your username first.")
					.exit(exitPlr -> exitPlr.getPA().sendEnterString("Enter your username", (plr, str) -> entered(plr, str, true)))
			);
		} else if (stage == Stage.ENTER_PASSWORD) {
			player.start(new DialogueBuilder(player)
					.npc(Npcs.DONATOR_SHOP, "Now enter your password.")
					.exit(exitPlr -> exitPlr.getPA().sendEnterString("Enter your password", (plr, str) -> entered(plr, str, false)))
			);
		} else if (stage == Stage.VERIFICATION) {
			player.start(new DialogueBuilder(player)
				.npc(Npcs.DONATOR_SHOP, "Processing, please wait. *beep boop*", "@red@Do not logout.")
				.action(Reclaim::verify)
			);
		}
	}

	private static void entered(Player player, String string, boolean username) {
		if (username) {
			player.getAttributes().setString(RECLAIM_USERNAME_ATTRIBUTE, string);
			dialogue(player, Stage.ENTER_PASSWORD);
		} else {
			player.getAttributes().setString(RECLAIM_PASSWORD_ATTRIBUTE, string);
			dialogue(player, Stage.VERIFICATION);
		}
	}

	private static void verify(Player player) {
		String username = player.getAttributes().getString(RECLAIM_USERNAME_ATTRIBUTE).toLowerCase();
		String password = player.getAttributes().getString(RECLAIM_PASSWORD_ATTRIBUTE);

		if (username == null || password == null) {
			player.getPA().closeAllWindows();
			player.sendMessage("@red@Your username or password wasn't entered, please try again.");
			return;
		}

		Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), (context, connection) -> {
			try {
				ReclaimDonationResponse response = Server.getDatabaseManager().executeImmediate(Server.getConfiguration().getStoreDatabase(), new ReclaimQuery(player, username, password));

				if (response.getResponse() == ReclaimDonationResponse.Response.SUCCESS) {
					Server.getLogging().write(new ReclaimDonationLog(player, username, response.getAmountDonated()));
					player.addQueuedAction(plr -> {
						player.amDonated += response.getAmountDonated();
						player.donatorPoints += response.getPoints();
						plr.start(new DialogueBuilder(plr).npc(Npcs.DONATOR_SHOP,
								"You've reclaimed $" + Misc.insertCommas(response.getAmountDonated()) + " in donations and",
								Misc.insertCommas(response.getPoints()) + " donation points."
						));
						PlayerSave.saveGame(plr);
					});
				} else {
					player.addQueuedAction(plr -> {
						plr.start(new DialogueBuilder(plr)
								.npc(Npcs.DONATOR_SHOP, response.getResponse().getMessage())
						);
					});
				}
			} catch (Exception e) {
				e.printStackTrace(System.err);
				player.addQueuedAction(plr -> plr.sendMessage("There was an issue while reclaiming your donations, try again later."));
			}

			return null;
		});
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Reclaim donations from Xeros v1 between ");
	}
}
