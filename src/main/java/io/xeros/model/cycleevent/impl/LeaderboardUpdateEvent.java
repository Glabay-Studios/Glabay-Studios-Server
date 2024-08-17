package io.xeros.model.cycleevent.impl;

import com.google.common.base.Preconditions;
import io.xeros.Server;
import io.xeros.content.leaderboards.*;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.sql.leaderboard.LeaderboardGetAll;
import io.xeros.sql.leaderboard.LeaderboardRewardAdd;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.xeros.content.leaderboards.LeaderboardUtils.*;

public class LeaderboardUpdateEvent extends Event<Object> {

	private static final Logger logger = LoggerFactory.getLogger(LeaderboardUpdateEvent.class);
	private static final String LAST_DAY_ATTRIBUTE = "leaderboard_last_select";

	/**
	 * The amount of time in game cycles (600ms) that the event pulses at
	 */
	private static final int INTERVAL = Misc.toCycles(1, TimeUnit.MINUTES);

	private static final ZoneId zoneId = ZoneId.of("America/New_York");
	private static ZonedDateTime lastUsedStartOfDay = LocalDate.now(zoneId).atStartOfDay(zoneId);

	public LeaderboardUpdateEvent() {
		super("", new Object(), Server.isDebug() ? Misc.toCycles(10, TimeUnit.SECONDS) : INTERVAL);

		String time = Server.getServerAttributes().getString(LAST_DAY_ATTRIBUTE);
		if (time != null) {
			lastUsedStartOfDay = LocalDate.parse(time, DateTimeFormatter.ISO_DATE).atStartOfDay(zoneId);
			logger.debug("Loaded serialized start of day as {}", lastUsedStartOfDay);
		} else {
			updateLastUsedStartOfDay(lastUsedStartOfDay);
		}

		if (Server.isDebug())
			execute();
	}

	private static void updateLastUsedStartOfDay(ZonedDateTime time) {
		lastUsedStartOfDay = time;
		String formattedLocalDate = lastUsedStartOfDay.toLocalDate().format(DateTimeFormatter.ISO_DATE);
		Server.getServerAttributes().setString(LAST_DAY_ATTRIBUTE, formattedLocalDate);
		logger.debug("Updated last start of day to {}", formattedLocalDate);
	}

	private static LocalDate getMondayOfThisWeek() {
		return LocalDate.now(zoneId).with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
	}

	@Override
	public void execute() {
		runUpdate(false);
	}

	public static void runUpdate(boolean forceWinnerSelection) {
		leaderboards.clear();

		Server.getDatabaseManager().exec((context, connection) -> {
			for (LeaderboardType type : LeaderboardType.values()) {
				List<LeaderboardEntry> entries = new LeaderboardGetAll(type).execute(context, connection);
				leaderboards.put(type, entries);
			}

			LocalDate now = LocalDate.now();
			update(now, LeaderboardPeriodicity.TODAY);
			update(now, LeaderboardPeriodicity.WEEKLY);

			ZonedDateTime startOfToday = LocalDate.now(zoneId).atStartOfDay(zoneId);

			// If the start of the current day is after the last used start of day, it's been 24 hours and we
			// select the daily winners for each category
			if (startOfToday.isAfter(lastUsedStartOfDay) || forceWinnerSelection) {
				updateLastUsedStartOfDay(startOfToday);
				List<RewardEntry> dailyWinnerRewards = getRewards(LeaderboardPeriodicity.TODAY);
				List<RewardEntry> rewards = new ArrayList<>(dailyWinnerRewards);
				logger.debug("Selected daily winners: {}", dailyWinnerRewards);

				// If we just selected daily winners for sunday then we select the weekly winners.
				if (startOfToday.getDayOfWeek() == DayOfWeek.MONDAY || forceWinnerSelection) {
					List<RewardEntry> weeklyWinnerRewards = getRewards(LeaderboardPeriodicity.WEEKLY);
					rewards.addAll(weeklyWinnerRewards);
					logger.debug("Selected weekly winners: {}", weeklyWinnerRewards);

					// Delete everything before this monday to free up space (due to leaderboards being held in memory)
					PreparedStatement delete = connection.prepareStatement("delete from leaderboards where date < ?");
					delete.setDate(1, Date.valueOf(getMondayOfThisWeek()));
					delete.execute();
				}

				if (!rewards.isEmpty()) {
					logger.debug("Added rewards {}", rewards);
					new LeaderboardRewardAdd(rewards).execute(context, connection);
				}

				PlayerHandler.addQueuedAction(() -> PlayerHandler.getPlayers().stream().filter(Objects::nonNull).forEach(LeaderboardUtils::checkRewards));
			}
			return null;
		});
	}

	private static void update(LocalDate now, LeaderboardPeriodicity period) {
		Preconditions.checkArgument(period == LeaderboardPeriodicity.TODAY || period == LeaderboardPeriodicity.WEEKLY);
		boolean dailyUpdate = period == LeaderboardPeriodicity.TODAY;
		LocalDate mondayOfThisWeek = getMondayOfThisWeek();

		Map<LeaderboardType, Map<String, LeaderboardEntry>> entries = new HashMap<>();

		// Get all leaderboard entries that fall into the time frame, add up all entries for the same username and type
		for (LeaderboardType type : LeaderboardType.values()) {
			HashMap<String, LeaderboardEntry> map = new HashMap<>();
			entries.put(type, map);

			for (Map.Entry<LeaderboardType, List<LeaderboardEntry>> keySet : leaderboards.entrySet()) {
				if (keySet.getKey() == type) {
					for (LeaderboardEntry entry : keySet.getValue()) {
						String loginName = entry.getLoginName();
						LocalDateTime from = entry.getTimestamp();
						Period diff = Period.between(from.toLocalDate(), now);
						if (dailyUpdate && diff.getDays() == 0 || !dailyUpdate
								&& (entry.getLocalDate().isEqual(mondayOfThisWeek) || entry.getLocalDate().isAfter(mondayOfThisWeek))) {
							if (map.containsKey(loginName)) {
								long newAmount = map.get(loginName).getAmount() + entry.getAmount();
								map.put(loginName, new LeaderboardEntry(type, loginName, newAmount, entry.getTimestamp()));
								continue;
							}

							map.put(loginName, entry);
						}
					}
				}
			}
		}

		// Now update the global map
		Map<LeaderboardType, List<LeaderboardEntry>> sortedMap = dailyUpdate ? daily : weekly;
		sortedMap.clear();

		for (LeaderboardType type : LeaderboardType.values()) {
			// Get the top 10
			List<LeaderboardEntry> list = new ArrayList<>(entries.get(type).values());
			list.sort(Comparator.comparingLong(LeaderboardEntry::getAmount).reversed());
			if (list.size() > 10)
				list = list.subList(0, Math.min(10, entries.size()));

			// Add them to the map for this type
			sortedMap.put(type, list);
		}
	}

	private static List<RewardEntry> getRewards(LeaderboardPeriodicity period) {
		Preconditions.checkArgument(period == LeaderboardPeriodicity.TODAY || period == LeaderboardPeriodicity.WEEKLY);
		boolean dailyUpdate = period == LeaderboardPeriodicity.TODAY;
		Map<LeaderboardType, List<LeaderboardEntry>> sortedMap = dailyUpdate ? daily : weekly;
		List<RewardEntry> rewards = new ArrayList<>();

		for (LeaderboardType type : LeaderboardType.values()) {
			List<LeaderboardEntry> entries = sortedMap.get(type);
			for(int i = 0; i < entries.size(); i++) {
				int place = i + 1;
				rewards.add(new RewardEntry(period, entries.get(i), place));
			}
		}

		return rewards;
	}
}