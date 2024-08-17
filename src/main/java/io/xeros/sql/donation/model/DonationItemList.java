package io.xeros.sql.donation.model;

import io.xeros.sql.donation.query.GetDonationsQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class DonationItemList extends ArrayList<DonationItem> {

    public DonationItemList(Collection<DonationItem> donationList) {
        this.addAll(donationList);
    }

    /**
     * Gets a list of {@link DonationItem} that have already been previously claimed.
     */
    public DonationItemList oldDonations() {
        return new DonationItemList(stream().filter(it -> it.isClaimed()).collect(Collectors.toList()));
    }

    /**
     * Gets a list of {@link DonationItem} that have not been claimed.
     */
    public DonationItemList newDonations() {
        return new DonationItemList(stream().filter(it -> !it.isClaimed()).collect(Collectors.toList()));
    }

}
