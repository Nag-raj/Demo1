<?xml version='1.0' ?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="http://xml.apache.org/xalan/java" exclude-result-prefixes="java fn">

	<xsl:import href="Hybris.Library.xsl"/>
	<xsl:import href="XiPay.Routing.Rules.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<!-- ########################################################################################
		# Save the data elements used by the XiPay Routing Rules
		############################################################################################# -->
		<xsl:variable name="varCountry" select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderCountry"/>
		<xsl:variable name="varCurrency" select="SubscriptionAuthorizationRequest/currency"/>
		<xsl:variable name="varCardType" select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardType"/>
		<xsl:variable name="varMTC" select="SubscriptionAuthorizationRequest/merchantTransactionCode"/>
		<xsl:variable name="varBusiness">
			<xsl:choose>
				<xsl:when test="contains($varMTC, '-AUTHORIZATION-') = false()">
					<xsl:value-of select="$varMTC"/>
				</xsl:when>
				<xsl:otherwise><xsl:value-of select="'*'"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="varMID">
			<xsl:call-template name="XiPayMID">
				<xsl:with-param name="cardType" select="$varCardType"/>
				<xsl:with-param name="currency" select="$varCurrency"/>
				<xsl:with-param name="country" select="$varCountry"/>
				<xsl:with-param name="business" select="$varBusiness"/>
				<xsl:with-param name="nodes" select="SubscriptionAuthorizationRequest"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- ########################################################################################
		# Save the successful AVS codes to be used in the response
		############################################################################################# -->
		<xsl:variable name="varAVSCodes">
			<xsl:call-template name="SuccessfulAVSCodes">
				<xsl:with-param name="xipayMID" select="$varMID"/>
				<xsl:with-param name="cardType" select="$varCardType"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- ########################################################################################
		# Save the successful CVV codes to be used in the response
		############################################################################################# -->
		<xsl:variable name="varCVVCodes">
			<xsl:call-template name="SuccessfulCVVCodes">
				<xsl:with-param name="xipayMID" select="$varMID"/>
				<xsl:with-param name="cardType" select="$varCardType"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- ########################################################################################
		# Save the Cronjob indicator
		############################################################################################# -->
		<xsl:variable name="varCronJob" select="SubscriptionAuthorizationRequest/cronJob"/>

		<ITransactionHeader>
			<additionalInfo>
				<xsl:value-of select="SubscriptionAuthorizationRequest/subscriptionID"/>
			</additionalInfo>
			<amount>
				<xsl:value-of select="SubscriptionAuthorizationRequest/totalAmount"/>
			</amount>
			<xsl:if test="string-length(SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardCVV) > 0">
				<cardCVV2>
					<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardCVV"/>
				</cardCVV2>
			</xsl:if>
			<cardExpirationDate>
				<xsl:call-template name="ExpiryDate">
					<xsl:with-param name="xipayMID" select="$varMID"/>
					<xsl:with-param name="expMonth" select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardExpirationMonth"/>
					<xsl:with-param name="expYear" select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardExpirationYear"/>
				</xsl:call-template>
			</cardExpirationDate>
			<cardHolderAddress1>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderAddress1"/>
			</cardHolderAddress1>
			<cardHolderAddress2>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderAddress2"/>
			</cardHolderAddress2>
			<cardHolderCity>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderCity"/>
			</cardHolderCity>
			<cardHolderCountry>
				<xsl:value-of select="$varCountry"/>
			</cardHolderCountry>
			<cardHolderName1>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderName1"/>
			</cardHolderName1>
			<cardHolderName2>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderName2"/>
			</cardHolderName2>
			<cardHolderName>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardOwner"/>
			</cardHolderName>
			<cardHolderState>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderState"/>
			</cardHolderState>
			<cardHolderZip>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/billingInfo/cardHolderZip"/>
			</cardHolderZip>
			<cardNumber>
				<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardNumber"/>
			</cardNumber>
			<cardPresent>
				<xsl:value-of select="'0'"/>
			</cardPresent>
			<cardType>
				<xsl:value-of select="$varCardType"/>
			</cardType>
			<currencyKey>
				<xsl:value-of select="$varCurrency"/>
			</currencyKey>
			<customerNumber>
				<xsl:value-of select="SubscriptionAuthorizationRequest/customerNumber"/>
			</customerNumber>
			<merchantID>
				<xsl:value-of select="$varMID"/>
			</merchantID>
			<merchantTransactionID>
				<xsl:value-of select="$varMTC"/>
			</merchantTransactionID>
			<xsl:if test="$varCardType != 'PP' and $varCardType != 'AL' and $varCardType != 'EC'">
				<taxLevel1>
					<xsl:value-of select="SubscriptionAuthorizationRequest/totalTax"/>
				</taxLevel1>
			</xsl:if>

			<!-- ########################################################################################
			# This template adds/sets any processor-specific header data
			############################################################################################# -->
			<xsl:call-template name="CustomHeader">
				<xsl:with-param name="xipayMID" select="$varMID"/>
				<xsl:with-param name="cardType" select="$varCardType"/>
				<xsl:with-param name="op"  select="'2'"/>
				<xsl:with-param name="nodes" select="SubscriptionAuthorizationRequest"/>
			</xsl:call-template>


			<arrayOfInfoItems>
				<infoItems>
					<InfoItem>
						<key>POS</key>
						<value>
							<xsl:value-of select="'Hybris [y]: '"/>
							<xsl:value-of select="SubscriptionAuthorizationRequest/baseStore"/>
						</value>
					</InfoItem>
					<xsl:if test="$varCardType = 'PP'">
						<InfoItem><key>TR_TRANS_REFID</key><value><xsl:value-of select="SubscriptionAuthorizationRequest/subscriptionID"/></value></InfoItem>
					</xsl:if>
					<InfoItem><key>PM_CRONJOB</key><value><xsl:value-of select="$varCronJob"/></value></InfoItem>
					<InfoItem><key>PM_FRAUD_ENABLED</key><value><xsl:value-of select="SubscriptionAuthorizationRequest/fraudEnabled"/></value></InfoItem>
					<InfoItem><key>PM_FRAUD_RESULT</key><value><xsl:value-of select="SubscriptionAuthorizationRequest/fraudResult"/></value></InfoItem>
					<InfoItem><key>PM_AVS_MUST_PASS</key><value><xsl:value-of select="SubscriptionAuthorizationRequest/avsMustPass"/></value></InfoItem>
					<InfoItem><key>PM_AVS_CODES</key><value><xsl:value-of select="$varAVSCodes"/></value></InfoItem>
					<InfoItem><key>PM_CVV_MUST_PASS</key><value><xsl:value-of select="SubscriptionAuthorizationRequest/cvvMustPass"/></value></InfoItem>
					<InfoItem><key>PM_CVV_CODES</key><value><xsl:value-of select="$varCVVCodes"/></value></InfoItem>
					<InfoItem>
						<key>PM_CVV_INFOITEM</key>
						<value>
							<xsl:call-template name="cvvResponseInfoItem">
								<xsl:with-param name="xipayMID" select="$varMID"/>
							</xsl:call-template>
						</value>
					</InfoItem>
	
					<!-- ########################################################################################
					# Set the Network ID during a full-authorization to XiPay
					############################################################################################# -->
					<xsl:variable name="varMsgType">
						<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/TR_TRANS_MSGTYPE"/>
					</xsl:variable>
					<xsl:if test="string-length($varMsgType) > 0">
						<InfoItem>
							<key>TR_TRANS_MSGTYPE</key>
							<value><xsl:value-of select="$varMsgType"/></value>
						</InfoItem>
					</xsl:if>
					<xsl:variable name="varNID">
						<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/IN_TRANS_VISATRANSID"/>
					</xsl:variable>
					<xsl:if test="string-length($varNID) > 0 and starts-with($varMsgType,'M')">
						<InfoItem>
							<key>IN_TRANS_VISATRANSID</key>
							<value><xsl:value-of select="$varNID"/></value>
						</InfoItem>
					</xsl:if>
	
					<xsl:variable name="varNID2">
						<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/IN_TRANS_VISATRANSID2"/>
					</xsl:variable>
					<xsl:if test="string-length($varNID2) > 0">
						<InfoItem>
							<key>IN_TRANS_VISATRANSID2</key>
							<value><xsl:value-of select="$varNID2"/></value>
						</InfoItem>
					</xsl:if>
	
					<xsl:variable name="varBankNetDate">
						<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/IN_TRANS_BANKNETDATE"/>
					</xsl:variable>
					<xsl:if test="string-length($varBankNetDate) > 0">
						<InfoItem>
							<key>IN_TRANS_BANKNETDATE</key>
							<value><xsl:value-of select="$varBankNetDate"/></value>
						</InfoItem>
					</xsl:if>
					
					<xsl:if test="$varCardType = 'EC'">
						<InfoItem>	
							<key>TR_ECP_ACCTTYPE</key>
							<value>
								<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/EC_ACCOUNT_TYPE"/>
							</value>
						</InfoItem>
						
						<InfoItem>
							<key>TR_ECP_BANKTRANSITNUM</key>
							<value>
								<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/EC_ROUTING_NUMBER"/>
							</value>
						</InfoItem>
						
						<InfoItem>
							<key>HD_BUYER_COMPANY</key>
							<value>
								<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/EC_COMPANY_NAME"/>
							</value>
						</InfoItem>	
					
					</xsl:if>
					
					
					<xsl:if test="$varCardType = 'PC'">
						<InfoItem>	
							<key>TR_CARD_CURRENCY</key>
							<value>
								<xsl:value-of select="$varCurrency"/>
							</value>
						</InfoItem>
						<InfoItem>
							<key>TR_CARD_PINNUM</key>
							<value>
								<xsl:value-of select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardCVV"/>
							</value>
						</InfoItem>
						<InfoItem>
							<key>TR_CARD_EXPDATE</key>
							<value>
								<xsl:call-template name="ExpiryDate">
									<xsl:with-param name="xipayMID" select="$varMID"/>
									<xsl:with-param name="expMonth" select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardExpirationMonth"/>
									<xsl:with-param name="expYear" select="SubscriptionAuthorizationRequest/paymentInfo/creditCard/cardExpirationYear"/>
								</xsl:call-template>
							</value>
						</InfoItem>	
					</xsl:if>
					
	
					<!-- ########################################################################################
					# This template adds/sets any processor-specific info-item data
					############################################################################################# -->
					<xsl:call-template name="CustomInfoItems">
						<xsl:with-param name="xipayMID" select="$varMID"/>
						<xsl:with-param name="cardType" select="$varCardType"/>
						<xsl:with-param name="op"  select="'2'"/>
						<xsl:with-param name="nodes" select="SubscriptionAuthorizationRequest"/>
					</xsl:call-template>
	
				</infoItems>
			</arrayOfInfoItems>

			<xsl:if test="SubscriptionAuthorizationRequest/lineItems">
				<arrayOfLineItems>
					<lineItems>
						<xsl:for-each select="SubscriptionAuthorizationRequest/lineItems/lineItem">
							<LineItem>
								<UPC><xsl:value-of select="UPC"/></UPC>
								<materialNumber><xsl:value-of select="materialNumber"/></materialNumber>
								<description><xsl:value-of select="description"/></description>
								<salesDocItemNumber><xsl:value-of select="salesDocItemNumber"/></salesDocItemNumber>
								<actualInvoicedQuantity><xsl:value-of select="actualInvoicedQuantity"/></actualInvoicedQuantity>
								<salesUnit><xsl:value-of select="salesUnit"/></salesUnit>
								<taxAmount><xsl:value-of select="taxAmount"/></taxAmount>
								<netValue><xsl:value-of select="netValue"/></netValue>
	
								<arrayOfInfoItems>
									<infoItems>
										<!-- ########################################################################################
										# This template adds/sets any processor-specific Line Item info-items data
										############################################################################################# -->
										<xsl:call-template name="CustomLineInfoItems">
											<xsl:with-param name="xipayMID" select="$varMID"/>
											<xsl:with-param name="cardType" select="$varCardType"/>
											<xsl:with-param name="op"  select="'2'"/>
											<xsl:with-param name="nodes" select="."/>
										</xsl:call-template>
									</infoItems>
								</arrayOfInfoItems>
	
							</LineItem>
						</xsl:for-each>
					</lineItems>
				</arrayOfLineItems>
			</xsl:if>
		</ITransactionHeader>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Full Authorization To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\2.1-FullAuthorization-SubscriptionAuthorizationRequest.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes"
		          profilemode="2" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""
		          validateoutput="no" validator="internal" customvalidator="">
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
			<advancedProp name="schemaCache" value="||"/>
			<advancedProp name="iWhitespace" value="0"/>
			<advancedProp name="bWarnings" value="true"/>
			<advancedProp name="bXml11" value="false"/>
			<advancedProp name="bUseDTD" value="false"/>
			<advancedProp name="bXsltOneIsOkay" value="true"/>
			<advancedProp name="bTinyTree" value="true"/>
			<advancedProp name="bGenerateByteCode" value="true"/>
			<advancedProp name="bExtensions" value="true"/>
			<advancedProp name="iValidation" value="0"/>
			<advancedProp name="iErrorHandling" value="fatal"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="sInitialMode" value=""/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="..\XSD\XiPay30WS.xsd" destSchemaRoot="ITransactionHeader" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\XML\2.1-FullAuthorization-SubscriptionAuthorizationRequest.xml" srcSchemaRoot="SubscriptionAuthorizationRequest" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="ITransactionHeader/xsl:if" x="244" y="396"/>
				<block path="ITransactionHeader/xsl:if/cardCVV2/xsl:value-of" x="204" y="396"/>
				<block path="ITransactionHeader/cardDataSource/xsl:value-of" x="366" y="414"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[1]" x="406" y="432"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[2]" x="244" y="219"/>
				<block path="ITransactionHeader/cardHolderCountry/xsl:value-of" x="164" y="219"/>
				<block path="ITransactionHeader/cardHolderName/xsl:value-of[1]" x="366" y="236"/>
				<block path="ITransactionHeader/cardPresent/xsl:value-of" x="406" y="236"/>
				<block path="ITransactionHeader/cardType/xsl:value-of" x="44" y="219"/>
				<block path="ITransactionHeader/currencyKey/xsl:value-of" x="204" y="179"/>
				<block path="ITransactionHeader/merchantID/xsl:value-of" x="244" y="179"/>
				<block path="ITransactionHeader/infoItems/xsl:if/=[0]" x="118" y="177"/>
				<block path="ITransactionHeader/infoItems/xsl:if" x="164" y="179"/>
				<block path="ITransactionHeader/infoItems/InfoItem[4]/value/xsl:value-of" x="124" y="179"/>
				<block path="ITransactionHeader/infoItems/InfoItem[6]/value/xsl:value-of" x="84" y="179"/>
				<block path="ITransactionHeader/infoItems/InfoItem[7]/value/xsl:value-of" x="44" y="179"/>
				<block path="ITransactionHeader/xsl:if[1]" x="33" y="119"/>
				<block path="ITransactionHeader/xsl:if[1]/lineItems/xsl:for-each" x="473" y="79"/>
			</template>
			<template match="/SubscriptionAuthorizationRequest">
				<block path="ITransactionHeader/additionalInfo/xsl:value-of" x="366" y="126"/>
				<block path="ITransactionHeader/amount/xsl:value-of" x="406" y="144"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of" x="366" y="432"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->