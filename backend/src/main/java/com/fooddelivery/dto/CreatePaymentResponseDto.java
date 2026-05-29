package com.fooddelivery.dto;

public class CreatePaymentResponseDto {

    private String razorpayOrderId;
    private Double amount;
    private String currency;
    // Public key ID only — never the secret. Safe to send to frontend.
    private String keyId;

    public CreatePaymentResponseDto() {}

    public CreatePaymentResponseDto(String razorpayOrderId, Double amount, String currency, String keyId) {
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.currency = currency;
        this.keyId = keyId;
    }

    public String getRazorpayOrderId() { return razorpayOrderId; }
    public void setRazorpayOrderId(String razorpayOrderId) { this.razorpayOrderId = razorpayOrderId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getKeyId() { return keyId; }
    public void setKeyId(String keyId) { this.keyId = keyId; }
}
