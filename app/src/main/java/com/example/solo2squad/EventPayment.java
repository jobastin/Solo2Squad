package com.example.solo2squad;

public class EventPayment {

    private String customerIdStripe;
    private String paymentIntentId;
    private double amountPaid;
    private long paymentTimestamp;
    private int status;


    public EventPayment() {

    }
    public EventPayment(String customerIdStripe, String paymentIntentId, double amountPaid, long paymentTimestamp, int status) {
        this.customerIdStripe = customerIdStripe;
        this.paymentIntentId = paymentIntentId;
        this.amountPaid = amountPaid;
        //this.paymentTimestamp = paymentTimestamp;
        setPaymentTimestamp(System.currentTimeMillis());
        this.status = status;
    }

    public String getCustomerIdStripe() {
        return customerIdStripe;
    }

    public void setCustomerIdStripe(String customerIdStripe) {
        this.customerIdStripe = customerIdStripe;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public long getPaymentTimestamp() {
        return paymentTimestamp;
    }

    public void setPaymentTimestamp(long paymentTimestamp) {
        this.paymentTimestamp = paymentTimestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
