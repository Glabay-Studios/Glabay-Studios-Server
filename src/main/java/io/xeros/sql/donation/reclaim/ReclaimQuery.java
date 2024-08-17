package io.xeros.sql.donation.reclaim;

import com.google.common.base.Preconditions;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.save.PlayerSaveOffline;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.io.File;
import java.sql.*;

/**
 * Checks username and password and
 */
public class ReclaimQuery implements SqlQuery<ReclaimDonationResponse> {

    public static final File OLD_FILES_LOCATION = new File(Configuration.SAVE_DIRECTORY + "old_chars/");

    private final Player player;
    private final String oldAccountUsername;
    private final String password;

    public ReclaimQuery(Player player, String oldAccountUsername, String password) {
        this.player = player;
        this.oldAccountUsername = oldAccountUsername;
        this.password = password;
    }

    public static int getV1DonationDollars(Connection connection, String oldAccountUsername) throws SQLException {
        PreparedStatement select = connection.prepareStatement("SELECT total_payment_amount FROM orders WHERE player_name = ? " +
                "AND status = ? AND created_at < ? AND created_at > ?");
        select.setString(1, oldAccountUsername.toLowerCase());
        select.setString(2, "Approved");
        select.setTimestamp(3, Timestamp.valueOf(Configuration.XEROS_V2_LAUNCH_DATE.atStartOfDay()));
        select.setTimestamp(4, Timestamp.valueOf(Configuration.XEROS_V1_LAUNCH_DATE.atStartOfDay()));

        int dollars = 0;
        ResultSet rs = select.executeQuery();
        while(rs.next()) {
            int totalPaymentAmount = rs.getInt("total_payment_amount");
            dollars += totalPaymentAmount;
        }

        return dollars;
    }

    @Override
    public ReclaimDonationResponse execute(DatabaseManager context, Connection connection) throws SQLException {
        try {
            boolean skipAuth = Server.isDebug() && player.getRights().isOrInherits(Right.ADMINISTRATOR);

            if (!OLD_FILES_LOCATION.exists()) {
                throw new IllegalStateException("Old character files folder (" + OLD_FILES_LOCATION + ") does not exist.");
            }

            if (!skipAuth) {
                File characterFile = PlayerSaveOffline.getCharacterFile(OLD_FILES_LOCATION, oldAccountUsername);
                if (characterFile == null) {
                    return new ReclaimDonationResponse(ReclaimDonationResponse.Response.NO_CHARACTER_FILE_WITH_NAME);
                }

                String actualPassword = PlayerSaveOffline.getPassword(characterFile);
                if (!PlayerSaveOffline.passwordMatches(password, actualPassword)) {
                    return new ReclaimDonationResponse(ReclaimDonationResponse.Response.INVALID_PASSWORD);
                }
            } else player.addQueuedAction(plr -> plr.sendMessage("Skipping password check because dev."));

            boolean alreadyClaimed = context.executeImmediate(new ReclaimCheckQuery(oldAccountUsername));
            if (alreadyClaimed) {
                if (skipAuth) {
                    player.addQueuedAction(plr -> plr.sendMessage("Already claimed but skipping response because dev."));
                } else {
                    return new ReclaimDonationResponse(ReclaimDonationResponse.Response.ALREADY_CLAIMED);
                }
            }

            int dollars = getV1DonationDollars(connection, oldAccountUsername);
            if (dollars == 0) {
                return new ReclaimDonationResponse(ReclaimDonationResponse.Response.NO_RESULTS);
            }

            boolean success = context.executeImmediate(new ReclaimSuccessQuery(oldAccountUsername, player.getLoginName(), dollars));
            Preconditions.checkState(success, "Could not set reclaimed donations for user " + player);

            return new ReclaimDonationResponse(ReclaimDonationResponse.Response.SUCCESS, dollars, dollars);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
