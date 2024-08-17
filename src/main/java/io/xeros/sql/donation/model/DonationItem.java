package io.xeros.sql.donation.model;

import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

public class DonationItem {

    /**
     * A date used to distinguish version 1 donations from version 2. Any donations
     * before this cutoff is considered version 1 for the purpose of using {@link io.xeros.content.commands.all.Reclaim}.
     */
    private static final LocalDate VERSION_1_CUTOFF = LocalDate.of(2021, 5, 1);

    private final int itemId;
    private final int itemAmount;
    private final String itemName;
    private final Date claimedDate;
    private final DonationTransaction transaction;

    public DonationItem(int itemId, int itemAmount, String itemName, Date claimedDate, DonationTransaction transaction) {
        this.itemId = itemId;
        this.itemAmount = itemAmount;
        this.itemName = itemName;
        this.claimedDate = claimedDate;
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "DonationItem{" +
                "itemId=" + itemId +
                ", itemAmount=" + itemAmount +
                ", itemName='" + itemName + '\'' +
                ", claimedDate=" + claimedDate +
                ", transaction=" + transaction +
                '}';
    }

    public boolean isClaimed() {
        return claimedDate != null;
    }

    /**
     * @return true if this donation is from the first version of the server.
     */
    public boolean isV1Donation() {
        return claimedDate.before(Date.valueOf(VERSION_1_CUTOFF));
    }

    public int getItemCost() throws IllegalStateException {
        return Arrays.stream(ClaimDonatorScrollDialogue.DonationScroll.values())
                .filter(it -> it.getItemId() == getItemId())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No DonationScroll enum for " + getItemId()))
                .getDonationAmount();
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public String getItemName() {
        return itemName;
    }

    public Date getClaimedDate() {
        return claimedDate;
    }

    public DonationTransaction getTransaction() {
        return transaction;
    }
}
