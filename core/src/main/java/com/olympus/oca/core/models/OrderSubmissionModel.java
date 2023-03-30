package com.olympus.oca.core.models;

public interface OrderSubmissionModel {
	String getOrderConfirmationTitle();
	String getOrderConfirmationDescription();
	String getShippingAddress();
	String getPaymentMethod();
	String getDatePlaced();
	String getOrderNumber();
	String getPONumber();
	String getTotalPrice();
	String getOrderStatusLabel();
	String getModelNumber();
	String getProduct();
	String getDescription();
	String getQuantity();

}


