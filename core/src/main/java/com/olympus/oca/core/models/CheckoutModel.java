package com.olympus.oca.core.models;

public interface CheckoutModel {
	public String getDisclaimerText();
	public String getTermsofuseText();
	public String getTermsofusePath();
	public String getCheckoutPagePath();
	public String getShopPagePath();
	String getPayViaInvoice();
	String getPayWithCreditCard();
	String getPaymentMethodDesc();
	String getPaymentMethodText();
}


