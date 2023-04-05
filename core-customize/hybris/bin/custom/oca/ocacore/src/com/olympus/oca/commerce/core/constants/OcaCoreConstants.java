/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.core.constants;


import java.util.HashMap;
import java.util.Map;

/**
 * Global class for all OcaCore constants. You can add global constants for your extension into this class.
 */
public final class OcaCoreConstants extends GeneratedOcaCoreConstants
{
	public static final String EXTENSIONNAME = "ocacore";
    public static final String ADDRESS_FILTER_TYPE = "address";
    public static final String ORDER_HISTORY_FILTER_STATUSES = "order.history.filter.statuses";


    private OcaCoreConstants()
	{
		//empty
	}

	// implement here constants used by this extension
	public static final String QUOTE_BUYER_PROCESS = "quote-buyer-process";
	public static final String QUOTE_SALES_REP_PROCESS = "quote-salesrep-process";
	public static final String QUOTE_USER_TYPE = "QUOTE_USER_TYPE";
	public static final String QUOTE_SELLER_APPROVER_PROCESS = "quote-seller-approval-process";
	public static final String QUOTE_TO_EXPIRE_SOON_EMAIL_PROCESS = "quote-to-expire-soon-email-process";
	public static final String QUOTE_EXPIRED_EMAIL_PROCESS = "quote-expired-email-process";
	public static final String QUOTE_POST_CANCELLATION_PROCESS = "quote-post-cancellation-process";
	public static final String SHIP_BY_OLYMPUS_CODE = "ship.by.olympus.code";
	public static final String RECENTLY_ORDERED_PRODUCT_COUNT = "recently.ordered.product.count";

	public static final String GHOST_PRODUCT_2_MG_4 = "ghost.products.materialgroup4";
	public static final String GHOST_PRODUCT_1_DCS = "ghost.products.distributionchainstatus";

	public static final String TEMP_NON_PURCHASABLE_DCS = "temp.nonpurchasable.distributionchainstatus";

	public static final String PURCHASABLE_DCS = "purchasable.distributionchainstatus";

	public static final String PURCHASABLE_MG4 = "purchasable.materialgroup4";

	public static final String CAPITAL_MG4 = "capital.materialgroup4";

	public static final String CAPITAL_MGRP = "capital.materialgroup";

	public static final String SHIP_BY_GROUND_LOADING_GROUP = "ship.by.ground.loadinggroup";

	public static final String SHIP_BY_GROUND_MATERIAL_GROUP = "ship.by.ground.materialgroup";

	public static final String MINICART_MAX_DISPLAY_VALUE = "minicart.max.display.value";

	public static final String PLUS_SYMBOL = "+";

	public static final String USD_PATTERN = "###,##0.00";

	public static final String TIME_TO_LIVE_HOURS = "occ.timeToLive.hours";
	public static final String RELEASE_FOLDER_NAME = "occ.release.impex.folderName";

	public static final String CUSTOMER_INVITE_LINK = "oca.customer.invite.link";
	public static final String CUSTOMER_VERIFICATION_LINK = "oca.customer.verification.link";
	public static final String TO_EMAIL = "oca.to.email.account";
	public static final String INVITE_INTERVAL = "crmcustomerinvitemail.daysbetweentwoinvites";
	public final static String FEDEX_URL = "oca.order.external.carrier.fedex.url";
	public final static String UPS_URL = "oca.order.external.carrier.ups.url";
	public static final String USER_GROUP_MAPPING = "sso.skip.usergroup.mapping";


	public final static String B2BUNIT_THIRDPARTYSHIPPING = "oca.b2bUnit.thirdParty.shippingCarrier.";

	public static class ErrorConstants
	{
		public static final String PRODUCT_NOT_SELLABLE = "This Product is non sellable";
		public static final String B2B_UNIT_NOT_FOUND = "The account number is not associated with current user";
	}



}
