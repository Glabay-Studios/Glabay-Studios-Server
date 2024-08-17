package io.xeros.sql.donation.reclaim;

public class ReclaimDonationResponse {

    public static enum Response {
        NO_CHARACTER_FILE_WITH_NAME("No character was found with that name!"),
        INVALID_PASSWORD("The password you supplied was invalid."),
        NO_RESULTS("There isn't any v1 purchases for that username."),
        ALREADY_CLAIMED("That account's donation were already claimed."),
        SUCCESS;

        private final String message;

        Response() {
            this(null);
        }

        Response(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private final Response response;
    private final int amountDonated;
    private final int points;

    public ReclaimDonationResponse(Response response, int amountDonated, int points) {
        this.response = response;
        this.amountDonated = amountDonated;
        this.points = points;
    }

    public ReclaimDonationResponse(Response response) {
        this.response = response;
        amountDonated = 0;
        points = 0;
    }

    public Response getResponse() {
        return response;
    }

    public int getAmountDonated() {
        return amountDonated;
    }

    public int getPoints() {
        return points;
    }
}
