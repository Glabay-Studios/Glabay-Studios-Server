package io.xeros.sql.donation.query;

import com.google.common.base.Preconditions;
import io.xeros.model.entity.player.Player;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.donation.model.DonationItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

public class ClaimDonationsQuery implements SqlQuery<int[]> {

    private final Player player;
    private final Collection<DonationItem> donationItems;

    public ClaimDonationsQuery(Player player, Collection<DonationItem> donationItems) {
        this.player = player;
        this.donationItems = donationItems;
        Preconditions.checkState(donationItems.stream().noneMatch(DonationItem::isClaimed), "list contains donations that were already claimed.");
    }

    @Override
    public int[] execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement("UPDATE orders SET claimed_at = NOW(), claimed_ip = ?, claimed_mac = ? WHERE player_name = ? AND id = ?");
        for (DonationItem item: donationItems) {
            stmt.setString(1, player.getIpAddress());
            stmt.setString(2, player.getMacAddress());
            stmt.setString(3, player.getLoginName().toLowerCase());
            stmt.setInt(4, item.getTransaction().getOrderId());
            stmt.execute();
            stmt.addBatch();
        }

        return stmt.executeBatch();
    }

}
