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
		<xsl:variable name="varCountry" select="StandaloneRefundRequest/billTo/country"/>
		<xsl:variable name="varCurrency" select="StandaloneRefundRequest/currency"/>
		<xsl:variable name="varCardType" select="StandaloneRefundRequest/card/cardType"/>
		<xsl:variable name="varMTC" select="StandaloneRefundRequest/merchantTransactionCode"/>
		<xsl:variable name="varBusiness">
			<xsl:choose>
				<xsl:when test="contains($varMTC, '-REFUND-') = false()">
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
				<xsl:with-param name="nodes" select="StandaloneRefundRequest"/>
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

		<xsl:variable name="varCVV">
			<xsl:value-of select="StandaloneRefundRequest/card/cv2Number"/>
		</xsl:variable>
		<xsl:variable name="varAmount">
			<xsl:choose>
				<xsl:when test="number(StandaloneRefundRequest/totalAmount) > 0">
					<xsl:value-of select="-StandaloneRefundRequest/totalAmount"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="StandaloneRefundRequest/totalAmount"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="varCardHolderName">
			<xsl:choose>
				<xsl:when test="StandaloneRefundRequest/billTo/firstName and StandaloneRefundRequest/billTo/lastName">
					<xsl:value-of select="StandaloneRefundRequest/billTo/firstName"/>
					<xsl:value-of select="' '"/>
					<xsl:value-of select="StandaloneRefundRequest/billTo/lastName"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="StandaloneRefundRequest/card/cardHolderFullName"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<ITransactionHeader>
			<amount>
				<xsl:value-of select="$varAmount"/>
			</amount>
			<xsl:if test="$varCVV != ''">
				<cardCVV2>
					<xsl:value-of select="$varCVV"/>
				</cardCVV2>
			</xsl:if>
			<cardExpirationDate>
				<xsl:call-template name="ExpiryDate">
					<xsl:with-param name="xipayMID" select="$varMID"/>
					<xsl:with-param name="expMonth" select="StandaloneRefundRequest/card/expirationMonth"/>
					<xsl:with-param name="expYear" select="StandaloneRefundRequest/card/expirationYear"/>
				</xsl:call-template>
			</cardExpirationDate>
			<cardHolderAddress1>
				<xsl:value-of select="StandaloneRefundRequest/billTo/street1"/>
			</cardHolderAddress1>
			<cardHolderAddress2>
				<xsl:value-of select="StandaloneRefundRequest/billTo/street2"/>
			</cardHolderAddress2>
			<cardHolderCity>
				<xsl:value-of select="StandaloneRefundRequest/billTo/city"/>
			</cardHolderCity>
			<cardHolderCountry>
				<xsl:value-of select="$varCountry"/>
			</cardHolderCountry>
			<cardHolderName1>
				<xsl:value-of select="StandaloneRefundRequest/billTo/firstName"/>
			</cardHolderName1>
			<cardHolderName2>
				<xsl:value-of select="StandaloneRefundRequest/billTo/lastName"/>
			</cardHolderName2>
			<cardHolderName>
				<xsl:value-of select="$varCardHolderName"/>
			</cardHolderName>
			<cardHolderState>
				<xsl:value-of select="StandaloneRefundRequest/billTo/state"/>
			</cardHolderState>
			<cardHolderZip>
				<xsl:value-of select="StandaloneRefundRequest/billTo/postalCode"/>
			</cardHolderZip>
			<cardNumber>
				<xsl:value-of select="StandaloneRefundRequest/card/cardNumber"/>
			</cardNumber>
			<cardPresent>0</cardPresent>
			<cardType>
				<xsl:value-of select="$varCardType"/>
			</cardType>
			<currencyKey>
				<xsl:value-of select="$varCurrency"/>
			</currencyKey>
			<merchantID>
				<xsl:value-of select="$varMID"/>
			</merchantID>


			<!-- ########################################################################################
			# This template adds/sets any processor-specific header data
			############################################################################################# -->
			<xsl:call-template name="CustomHeader">
				<xsl:with-param name="xipayMID" select="$varMID"/>
				<xsl:with-param name="cardType" select="$varCardType"/>
				<xsl:with-param name="op"  select="'4'"/>
				<xsl:with-param name="nodes" select="StandaloneRefundRequest"/>
			</xsl:call-template>

			<arrayOfInfoItems>
				<infoItems>
					<InfoItem>
						<key>PM_AVS_CODES</key>
						<value><xsl:value-of select="$varAVSCodes"/></value>
					</InfoItem>
					<InfoItem>
						<key>PM_CVV_CODES</key>
						<value><xsl:value-of select="$varCVVCodes"/></value>
					</InfoItem>
					<InfoItem>
						<key>PM_CVV_INFOITEM</key>
						<value>
							<xsl:value-of select="'TR_CARD_CIDRESPCODE'"/>
						</value>
					</InfoItem>
	
					<!-- ########################################################################################
					# This template adds/sets any processor-specific info-item data
					############################################################################################# -->
					<xsl:call-template name="CustomInfoItems">
						<xsl:with-param name="xipayMID" select="$varMID"/>
						<xsl:with-param name="cardType" select="$varCardType"/>
						<xsl:with-param name="op"  select="'4'"/>
						<xsl:with-param name="nodes" select="StandaloneRefundRequest"/>
					</xsl:call-template>
				</infoItems>
			</arrayOfInfoItems>

		</ITransactionHeader>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\4.1-Refund-StandaloneRefundRequest.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth=""
		          profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
		          customvalidator="">
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
			<SourceSchema srcSchemaPath="..\XML\4.1-Refund-StandaloneRefundRequest.xml" srcSchemaRoot="StandaloneRefundRequest" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="ITransactionHeader/xsl:if/!=[0]" x="349" y="130"/>
				<block path="ITransactionHeader/xsl:if" x="395" y="132"/>
				<block path="ITransactionHeader/xsl:if/cardCVV2/xsl:value-of" x="355" y="132"/>
				<block path="ITransactionHeader/cardDataSource/xsl:value-of" x="315" y="132"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[1]" x="275" y="132"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[2]" x="235" y="132"/>
				<block path="ITransactionHeader/cardHolderCountry/xsl:value-of" x="195" y="132"/>
				<block path="ITransactionHeader/cardHolderName/xsl:value-of[1]" x="195" y="132"/>
				<block path="ITransactionHeader/cardType/xsl:value-of" x="115" y="132"/>
				<block path="ITransactionHeader/currencyKey/xsl:value-of" x="75" y="132"/>
				<block path="ITransactionHeader/merchantID/xsl:value-of" x="35" y="132"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->