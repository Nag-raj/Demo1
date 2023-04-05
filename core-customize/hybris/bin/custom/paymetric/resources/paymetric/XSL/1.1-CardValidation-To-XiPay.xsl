<?xml version="1.0"?>
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
		<xsl:variable name="varCountry" select="/map/entry[string[1]='billTo_country']/string[2]"/>
		<xsl:variable name="varCurrency" select="/map/entry[string[1]='currency']/string[2]"/>
		<xsl:variable name="varCardType">
			<xsl:call-template name="Hybris2XiPayCardType">
				<xsl:with-param name="cardType" select="/map/entry[string[1]='card_cardType']/string[2]"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="varBusiness">
			<xsl:choose>
				<xsl:when test="/map/entry[string[1]='business_unit']/string[2]">
					<xsl:value-of select="/map/entry[string[1]='business_unit']/string[2]"/>
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
				<xsl:with-param name="nodes" select="/map"/>
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
		# Optional Name on Card field value. If client using standard or custom xml template with name 
		# on card field enabled, it will have precedence over billing first name and billing last name.
		############################################################################################# -->
		<xsl:variable name="varFirstName">
			<xsl:choose>
				<xsl:when test="string-length(map/entry[string[1]='card_nameOnCard']/string[2]) > 0"><xsl:value-of select="map/entry[string[1]='card_nameOnCard']/string[2]"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="map/entry[string[1]='billTo_firstName']/string[2]"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="varLastName">
			<xsl:choose>
				<xsl:when test="string-length(map/entry[string[1]='card_nameOnCard']/string[2]) > 0"><xsl:value-of select="''"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="map/entry[string[1]='billTo_lastName']/string[2]"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="varEmailid">
			<xsl:choose>
				<xsl:when test="string-length(map/entry[string[1]='billTo_email']/string[2]) > 0"><xsl:value-of select="map/entry[string[1]='billTo_email']/string[2]"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="map/entry[string[1]='billTo_email']/string[2]"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="varCardNumber">
			<xsl:choose>
				<xsl:when test="string-length(map/entry[string[1]='card_accountNumber']/string[2]) > 0"><xsl:value-of select="map/entry[string[1]='card_accountNumber']/string[2]"/></xsl:when>				
			</xsl:choose>
		</xsl:variable>

		<ITransactionHeader>
			<additionalInfo>
				<xsl:value-of select="normalize-space(concat($varEmailid,'^',$varCardNumber))"/>
			</additionalInfo>
			<amount>
				<xsl:call-template name="PreAuthAmount">
					<xsl:with-param name="xipayMID" select="$varMID"/>
					<xsl:with-param name="cardType" select="$varCardType"/>
				</xsl:call-template>
			</amount>
						
			<xsl:if test="string-length(map/entry[string[1]='card_cvNumber']/string[2]) > 0">
				<cardCVV2>
					<xsl:value-of select="map/entry[string[1]='card_cvNumber']/string[2]"/>
				</cardCVV2>
			</xsl:if>
			<cardExpirationDate>
				<xsl:call-template name="ExpiryDate">
					<xsl:with-param name="xipayMID" select="$varMID"/>
					<xsl:with-param name="expMonth" select="map/entry[string[1]='card_expirationMonth']/string[2]"/>
					<xsl:with-param name="expYear" select="map/entry[string[1]='card_expirationYear']/string[2]"/>
				</xsl:call-template>
			</cardExpirationDate>
			<cardHolderAddress1>
				<xsl:value-of select="map/entry[string[1]='billTo_street1']/string[2]"/>
			</cardHolderAddress1>
			<cardHolderAddress2>
				<xsl:value-of select="map/entry[string[1]='billTo_street2']/string[2]"/>
			</cardHolderAddress2>
			<cardHolderCity>
				<xsl:value-of select="map/entry[string[1]='billTo_city']/string[2]"/>
			</cardHolderCity>
			<cardHolderCountry>
				<xsl:value-of select="$varCountry"/>
			</cardHolderCountry>
			<cardHolderName1>
				<xsl:value-of select="map/entry[string[1]='billTo_firstName']/string[2]"/>
			</cardHolderName1>
			<cardHolderName2>
				<xsl:value-of select="map/entry[string[1]='billTo_lastName']/string[2]"/>
			</cardHolderName2>
			<cardHolderName>
				<xsl:value-of select="normalize-space(concat($varFirstName,' ',$varLastName))"/>
			</cardHolderName>
			<cardHolderState>
				<xsl:value-of select="map/entry[string[1]='billTo_state']/string[2]"/>
			</cardHolderState>
			<cardHolderZip>
				<xsl:value-of select="map/entry[string[1]='billTo_postalCode']/string[2]"/>
			</cardHolderZip>
			<cardNumber>
				<xsl:value-of select="map/entry[string[1]='card_accountNumber']/string[2]"/>
			</cardNumber>
			<cardType>
				<xsl:value-of select="$varCardType"/>
			</cardType>
			<currencyKey>
				<xsl:value-of select="$varCurrency"/>
			</currencyKey>
			<customerNumber>
				<xsl:value-of select="map/entry[string[1]='billTo_email']/string[2]"/>
			</customerNumber>
			<merchantID>
				<xsl:value-of select="$varMID"/>
			</merchantID>
			<modifiedStatus>0</modifiedStatus>
			<orderID>
				<xsl:value-of select="map/entry[string[1]='paySubscriptionCreateReply_subscriptionID']/string[2]"/>
			</orderID>


			<xsl:choose>
				<xsl:when test="$varCardType = 'PP'">
					<transactionID>
						<xsl:value-of select="map/entry[string[1]='paypal_order_id']/string[2]"/>
					</transactionID>
				</xsl:when>
				<xsl:when test="$varCardType = 'AL'">
					<transactionID>
						<xsl:value-of select="map/entry[string[1]='alipay_order_id']/string[2]"/>
					</transactionID>
				</xsl:when>
				<xsl:when test="$varCardType = 'EC'">
					<transactionID>
						<xsl:value-of select="'ACH CHECK'"/>
					</transactionID>
				</xsl:when>
			</xsl:choose>


			<!-- ########################################################################################
			# This template adds/sets any processor-specific header data
			############################################################################################# -->
			<xsl:call-template name="CustomHeader">
				<xsl:with-param name="xipayMID" select="$varMID"/>
				<xsl:with-param name="cardType" select="$varCardType"/>
				<xsl:with-param name="op"  select="'1'"/>
				<xsl:with-param name="nodes" select="/map"/>
			</xsl:call-template>

			<arrayOfInfoItems>
				<infoItems>
					<InfoItem>
						<key>TR_TRANS_MSGTYPE</key>
						<value>
							<xsl:value-of select="map/entry[string[1]='TR_TRANS_MSGTYPE']/string[2]"/>
						</value>
					</InfoItem>
	
					<InfoItem>
						<key>PM_AVS_MUST_PASS</key>
						<value>
							<xsl:value-of select="map/entry[string[1]='PM_AVS_MUST_PASS']/string[2]"/>
						</value>
					</InfoItem>
					<InfoItem>
						<key>PM_AVS_CODES</key>
						<value>
							<xsl:value-of select="$varAVSCodes"/>
						</value>
					</InfoItem>
	
					<InfoItem>
						<key>PM_CVV_MUST_PASS</key>
						<value>
							<xsl:value-of select="map/entry[string[1]='PM_CVV_MUST_PASS']/string[2]"/>
						</value>
					</InfoItem>
					<InfoItem>
						<key>PM_CVV_CODES</key>
						<value>
							<xsl:value-of select="$varCVVCodes"/>
						</value>
					</InfoItem>
					<InfoItem>
						<key>PM_CVV_INFOITEM</key>
						<value>
							<xsl:call-template name="cvvResponseInfoItem">
								<xsl:with-param name="xipayMID" select="$varMID"/>
							</xsl:call-template>
						</value>
					</InfoItem>
					<InfoItem>
						<key>POS</key>
						<value>
							<xsl:value-of select="'Hybris [y]: '"/>
							<xsl:value-of select="map/entry[string[1]='ybaseStore']/string[2]"/>
						</value>
					</InfoItem>
					<InfoItem>
						<key>HYBRIS_BASESTORE</key>
						<value>
							<xsl:value-of select="map/entry[string[1]='ybaseStore']/string[2]"/>
						</value>
					</InfoItem>
				
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
								<xsl:value-of select="map/entry[string[1]='card_cvNumber']/string[2]"/>
							</value>
						</InfoItem>
						<InfoItem>
							<key>TR_CARD_EXPDATE</key>
							<value>
								<xsl:call-template name="ExpiryDate">
									<xsl:with-param name="xipayMID" select="$varMID"/>
									<xsl:with-param name="expMonth" select="map/entry[string[1]='card_expirationMonth']/string[2]"/>
									<xsl:with-param name="expYear" select="map/entry[string[1]='card_expirationYear']/string[2]"/>
								</xsl:call-template>
							</value>
						</InfoItem>	
					</xsl:if>

					<xsl:if test="map/entry[string[1]='includeSopData']/string[2] = 'true'">
						<xsl:for-each select="map/entry">
							<InfoItem>
								<key>
									<xsl:value-of select="string[1]"/>
								</key>
								<value>
									<xsl:choose>
										<xsl:when test="string[1] = 'card_cvNumber'">
											<xsl:value-of select="'**PRESENT** with '"/>
											<xsl:value-of select="string-length(string[2])"/>
											<xsl:value-of select="' digits'"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="string[2]"/>
										</xsl:otherwise>
									</xsl:choose>
								</value>
							</InfoItem>
						</xsl:for-each>
					</xsl:if>

					<!-- ########################################################################################
					# This template adds/sets any processor-specific info-item data
					############################################################################################# -->
					<xsl:call-template name="CustomInfoItems">
						<xsl:with-param name="xipayMID" select="$varMID"/>
						<xsl:with-param name="cardType" select="$varCardType"/>
						<xsl:with-param name="op"  select="'1'"/>
						<xsl:with-param name="nodes" select="/map"/>
					</xsl:call-template>

				</infoItems>
			</arrayOfInfoItems>

		</ITransactionHeader>
	</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\1.1-CardValidation-HashMap.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth="" profilelength=""
		          urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
		          customvalidator="">
			<advancedProp name="bSchemaAware" value="true"/>
			<advancedProp name="xsltVersion" value="2.0"/>
			<advancedProp name="xsdVersion" value="1.0"/>
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
			<advancedProp name="iErrorHandling" value="0"/>
			<advancedProp name="sInitialTemplate" value=""/>
			<advancedProp name="sInitialMode" value=""/>
		</scenario>
	</scenarios>
	<MapperMetaTag>
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="..\XSD\XiPay30WS.xsd" destSchemaRoot="ITransactionHeader" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\XML\1.1-CardValidation-HashMap.xml" srcSchemaRoot="map" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[1]" x="366" y="219"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[2]" x="406" y="219"/>
				<block path="ITransactionHeader/cardHolderCountry/xsl:value-of" x="401" y="194"/>
				<block path="ITransactionHeader/cardType/xsl:value-of" x="189" y="228"/>
				<block path="ITransactionHeader/currencyKey/xsl:value-of" x="361" y="194"/>
				<block path="ITransactionHeader/xsl:if/=[0]" x="320" y="166"/>
				<block path="ITransactionHeader/xsl:if" x="366" y="168"/>
				<block path="ITransactionHeader/xsl:if/infoItems/xsl:for-each" x="166" y="219"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->