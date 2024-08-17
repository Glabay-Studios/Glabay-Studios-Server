package io.xeros.sql.donation.reclaim;

import io.xeros.sql.DatabaseManager;
import io.xeros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReclaimSuccessQuery implements SqlQuery<Boolean> {

    private final String claimedUsername;
    private final String claimedByUsername;
    private final int claimedAmount;

    public ReclaimSuccessQuery(String claimedUsername, String claimedByUsername, int claimedAmount) {
        this.claimedUsername = claimedUsername;
        this.claimedByUsername = claimedByUsername;
        this.claimedAmount = claimedAmount;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO reclaimed_donations VALUES(?, ?, ?, ?)");
        statement.setString(1, claimedUsername.toLowerCase());
        statement.setString(2, claimedByUsername.toLowerCase());;
        statement.setInt(3, claimedAmount);
        statement.setDate(4, Date.valueOf(LocalDate.now()));
        return statement.executeUpdate() == 1;
    }

}
