package io.xeros.sql.donation.query;

import io.xeros.model.entity.player.Player;
import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;
import io.xeros.sql.donation.model.DonationItem;
import io.xeros.sql.donation.model.DonationItemList;
import io.xeros.sql.donation.model.DonationTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Grabs all the donated items that were ever purchased by this player.
 * This includes donations that have already been claimed.
 */
public class GetDonationsQuery implements SqlQuery<DonationItemList> {

    private final String username;

    public GetDonationsQuery(String username) {
        this.username = username;
    }

    @Override
    public DonationItemList execute(DatabaseManager context, Connection connection) throws SQLException {
        List<DonationItem> list = new ArrayList<>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders WHERE player_name = ? AND status = ?"); // AND claimed_at IS NULL
        statement.setString(1, username.toLowerCase());
        statement.setString(2, "Approved");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int orderId = resultSet.getInt("id");
            Date claimedDate = resultSet.getDate("claimed_at");
            PreparedStatement innerStatement = connection.prepareStatement("SELECT * FROM order_lines WHERE order_id = ?");
            innerStatement.setInt(1, orderId);

            ResultSet innerResultSet = innerStatement.executeQuery();

            while (innerResultSet.next()) {
                int productId = innerResultSet.getInt("product_id");
                int orderQuantity = innerResultSet.getInt("quantity");
                PreparedStatement innerStatement2 = connection.prepareStatement("SELECT * FROM products WHERE id = ?");
                innerStatement2.setInt(1, productId);
                ResultSet innerResultSet2 = innerStatement2.executeQuery();

                while (innerResultSet2.next()) {
                    final int itemId = innerResultSet2.getInt("item_id");
                    final int itemQuantity = innerResultSet2.getInt("amount") * orderQuantity;
                    String itemName = innerResultSet2.getString("name");

                    list.add(new DonationItem(itemId, itemQuantity, itemName, claimedDate, new DonationTransaction(orderId, productId, orderQuantity)));
                }
            }
        }

        return new DonationItemList(list);
    }


}
