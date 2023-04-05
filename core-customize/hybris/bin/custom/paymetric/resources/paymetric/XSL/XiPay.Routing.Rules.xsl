<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- ########################################################################################
	#  XiPay.Routing.Rules.xsl for Evernorth (Express Scripts) - Vantiv and AMEX CAPN Cartridges
	############################################################################################# -->


	<!-- ########################################################################################
	# This template sets the processor-specific PreAuthorization Amount
	############################################################################################# -->
	<xsl:template name="PreAuthAmount">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
		<xsl:choose>
			<xsl:when test="contains('AX DI', $cardType)">
				<xsl:value-of select="'1.00'"/>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="'0.00'"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template sets the processor-specific Expiration Date
	############################################################################################# -->
	<xsl:template name="ExpiryDate">
	    <xsl:param name="xipayMID"/>
		<xsl:param name="expMonth"/>
		<xsl:param name="expYear"/>
		
		<xsl:choose>
			<xsl:when test="string-length($expMonth) = 1"><xsl:value-of select="concat('0', $expMonth)"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="$expMonth"/></xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="string-length($expYear) = 4"><xsl:value-of select="number($expYear) - 2000"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="$expYear"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template sets the processor-specific CVV Response Info-Item Name
	############################################################################################# -->
	<xsl:template name="cvvResponseInfoItem">
	 	<xsl:param name="xipayMID"/>
		<xsl:value-of select="'TR_CARD_CIDRESPCODE'"/>
	</xsl:template>

	<!-- ########################################################################################
	# This template adds/sets any processor-specific header data
	############################################################################################# -->
	<xsl:template name="CustomHeader">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
	    <xsl:param name="op"/>
	    <xsl:param name="nodes"/>
		
		<xsl:choose>
			<xsl:when test="$op = '1'"> <!-- Card Validation -->
				<xsl:if test="$nodes/entry[string[1]='merchantRefenceNumber']">
					<PONumber>
						<xsl:value-of select="$nodes/entry[string[1]='merchantRefenceNumber']/string[2]"/>
					</PONumber>
					<salesDocNumber>
						<xsl:value-of select="$nodes/entry[string[1]='merchantRefenceNumber']/string[2]"/>
					</salesDocNumber>
				</xsl:if>

				<xsl:variable name="orderSource">
					<xsl:value-of select="$nodes/entry[string[1]='orderSource']/string[2]" />
				</xsl:variable>
				<cardDataSource>
					<xsl:choose>
						<xsl:when test="$orderSource = 'telephone'"><xsl:value-of select="'M'" /></xsl:when>
						<xsl:otherwise><xsl:value-of select="'E'" /></xsl:otherwise>
					</xsl:choose>
				</cardDataSource>
			</xsl:when>

			<xsl:when test="$op = '2'"> <!-- Full Authorization -->
				<xsl:if test="$nodes/merchantRefenceNumber">
					<PONumber>
						<xsl:value-of select="$nodes/merchantRefenceNumber"/>
					</PONumber>
					<salesDocNumber>
						<xsl:value-of select="$nodes/merchantRefenceNumber"/>
					</salesDocNumber>
				</xsl:if>
				<xsl:variable name="orderSource">
					<xsl:value-of select="$nodes/customFields/orderSource" />
				</xsl:variable>
				<cardDataSource>
					<xsl:choose>
						<xsl:when test="$orderSource = 'telephone'"><xsl:value-of select="'M'" /></xsl:when>
						<xsl:otherwise><xsl:value-of select="'E'" /></xsl:otherwise>
					</xsl:choose>
				</cardDataSource>
			</xsl:when>

			<xsl:when test="$op = '3'"> <!-- Capture -->
			</xsl:when>

			<xsl:when test="$op = '4'"> <!-- Refunds -->
			</xsl:when>

			<xsl:when test="$op = '5'"> <!-- Voids -->
			</xsl:when>

			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ########################################################################################
	# This template adds/sets any processor-specific info-item data
	############################################################################################# -->
	<xsl:template name="CustomInfoItems">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
	    <xsl:param name="op"/>
	    <xsl:param name="nodes"/>

		<xsl:choose>
			<xsl:when test="$op = '1'"> <!-- Card Validation -->
				<InfoItem><key>HD_BUYER_CNTCTPHONE</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_phoneNumber']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_CNTCTEMAIL</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_email']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_FNAME</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_firstName']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_LNAME</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_lastName']/string[2]"/></value></InfoItem>

				<InfoItem><key>HD_BILLTO_CNTCTPHONE</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_phoneNumber']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_ADDR</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_street1']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_CITY</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_city']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_COUNTRY</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_country']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_STATE</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_state']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_ZIP</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_postalCode']/string[2]"/></value></InfoItem>

				<InfoItem><key>HD_SHIPTO_CNTCTPHONE</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_phoneNumber']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ADDR</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_street1']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ADDR2</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_street2']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_CITY</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_city']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_CNTRY</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_country']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_STATE</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_state']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ZIP</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_postalCode']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_NAME</key><value><xsl:value-of select="concat($nodes/entry[string[1]='billTo_lastName']/string[2], ' ', $nodes/entry[string[1]='billTo_firstName']/string[2])"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_FNAME</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_firstName']/string[2]"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_LNAME</key><value><xsl:value-of select="$nodes/entry[string[1]='billTo_lastName']/string[2]"/></value></InfoItem>
			</xsl:when>

			<xsl:when test="$op = '2'"> <!-- Full Authorization -->
				<InfoItem><key>HD_BUYER_CNTCTPHONE</key><value><xsl:value-of select="$nodes/shippingInfo/phoneNumber"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_CNTCTEMAIL</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderEmail"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_IPADDRESS</key><value><xsl:value-of select="$nodes/shippingInfo/ipAddress"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_FNAME</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderName1"/></value></InfoItem>
				<InfoItem><key>HD_BUYER_LNAME</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderName2"/></value></InfoItem>

				<InfoItem><key>HD_BILLTO_CNTCTPHONE</key><value><xsl:value-of select="$nodes/shippingInfo/phoneNumber"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_ADDR</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderAddress1"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_CITY</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderCity"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_COUNTRY</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderCountry"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_STATE</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderState"/></value></InfoItem>
				<InfoItem><key>HD_BILLTO_ZIP</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/cardHolderZip"/></value></InfoItem>

				<InfoItem><key>HD_SHIPTO_CNTCTPHONE</key><value><xsl:value-of select="$nodes/shippingInfo/phoneNumber"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ADDR</key><value><xsl:value-of select="$nodes/shippingInfo/street1"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ADDR2</key><value><xsl:value-of select="$nodes/shippingInfo/street2"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_CITY</key><value><xsl:value-of select="$nodes/shippingInfo/city"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_CNTRY</key><value><xsl:value-of select="$nodes/shippingInfo/country"/></value></InfoItem>					
				<InfoItem><key>HD_SHIPTO_STATE</key><value><xsl:value-of select="$nodes/shippingInfo/state"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_ZIP</key><value><xsl:value-of select="$nodes/shippingInfo/postalCode"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_NAME</key><value><xsl:value-of select="concat($nodes/shippingInfo/lastName, ' ', $nodes/shippingInfo/firstName)"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_FNAME</key><value><xsl:value-of select="$nodes/paymentInfo/shippingInfo/firstName"/></value></InfoItem>
				<InfoItem><key>HD_SHIPTO_LNAME</key><value><xsl:value-of select="$nodes/paymentInfo/billingInfo/lastName"/></value></InfoItem>
				<InfoItem><key>HD_SHIPPING_METHOD</key><value><xsl:value-of select="$nodes/shippingInfo/shippingMethod"/></value></InfoItem>
				<InfoItem><key>HD_SHIPPING_COST</key><value><xsl:value-of select="$nodes/shippingInfo/shippingCost"/></value></InfoItem>
			</xsl:when>

			<xsl:when test="$op = '3'"> <!-- Capture -->
			</xsl:when>

			<xsl:when test="$op = '4'"> <!-- Refunds -->
			</xsl:when>

			<xsl:when test="$op = '5'"> <!-- Voids -->
			</xsl:when>

			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ########################################################################################
	# This template adds/sets any processor-specific Line Item info-items data
	############################################################################################# -->
	<xsl:template name="CustomLineInfoItems">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
	    <xsl:param name="op"/>
	    <xsl:param name="nodes"/>

		<xsl:choose>
			<xsl:when test="$op = '1'"> <!-- Card Validation -->
			</xsl:when>

			<xsl:when test="$op = '2'"> <!-- Full Authorization -->
			</xsl:when>

			<xsl:when test="$op = '3'"> <!-- Capture -->
			</xsl:when>

			<xsl:when test="$op = '4'"> <!-- Refunds -->
			</xsl:when>

			<xsl:when test="$op = '5'"> <!-- Voids -->
			</xsl:when>

			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ########################################################################################
	# This template determines the XiPay Routing MID
	############################################################################################# -->
	<xsl:template name="XiPayMID">
	    <xsl:param name="cardType"/>
	    <xsl:param name="currency"/>
	    <xsl:param name="country"/>
	    <xsl:param name="business"/>
	    <xsl:param name="nodes"/>
		<xsl:choose>
			<xsl:when test="contains('usd-USD-*', $currency) and contains('VI-MC-AX-DI-*', $cardType)"><xsl:value-of select="'OAIFISCYBER'"/></xsl:when>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template determines the successful AVS response code by card type
	############################################################################################# -->
	<xsl:template name="SuccessfulAVSCodes">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
		<xsl:choose>
		<xsl:when test="contains('VI-*', $cardType)and contains($xipayMID,'OAIFISCYBER')"><xsl:value-of select="'A;B;D;F;M;P;Y;Z'"/></xsl:when>
		<xsl:when test="contains('MC-*', $cardType)and contains($xipayMID,'OAIFISCYBER')"><xsl:value-of select="'X;Y;A;W;Z'"/></xsl:when>
		<xsl:when test="contains('AX-*', $cardType)and contains($xipayMID,'OAIFISCYBER')"><xsl:value-of select="'Y;A;Z;L;M;O;K;D;E;F'"/></xsl:when>
		<xsl:when test="contains('DI-*', $cardType)and contains($xipayMID,'OAIFISCYBER')"><xsl:value-of select="'X;Y;A;W;Z'"/></xsl:when>
		<xsl:otherwise><xsl:value-of select="'*'"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ########################################################################################
	# This template determines the successful CVV response code by card type
	############################################################################################# -->
	<xsl:template name="SuccessfulCVVCodes">
	    <xsl:param name="xipayMID"/>
	    <xsl:param name="cardType"/>
		<xsl:choose>
			<xsl:when test="contains('VI-MC-AX-DI-*', $cardType) and contains($xipayMID,'OAIFISCYBER')"><xsl:value-of select="'M;P;S;U;X'"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="'*'"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template determines the authorization response code produced by the processor
	############################################################################################# -->
	<xsl:template name="AuthorizationResponseCode">
	    <xsl:param name="packet"/>
		<xsl:choose>
			<xsl:when test="$packet/merchantID = 'OAIFISCYBER'"><xsl:value-of select="$packet/infoItems/InfoItem[key='TR_CARD_RESPCODE']/value"/></xsl:when>
		</xsl:choose>
	</xsl:template> 

</xsl:stylesheet>
