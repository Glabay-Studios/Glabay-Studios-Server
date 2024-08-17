package io.xeros.sql.donation.model;

public class DonationTransaction {

    private final int orderId;
    private final int productId;
    private final int orderQuantity;

    public DonationTransaction(int orderId, int productId, int orderQuantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.orderQuantity = orderQuantity;
    }

    @Override
    public String toString() {
        return "DonationTransaction{" +
                "orderId=" + orderId +
                ", productId=" + productId +
                ", orderQuantity=" + orderQuantity +
                '}';
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
}
