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
		# Save the data elements used more than once in this XSLT to optimize performance
		############################################################################################# -->
		<xsl:variable name="varTransactionID">
			<xsl:value-of select="ITransactionHeader/transactionID"/>
		</xsl:variable>
		<xsl:variable name="varAmount">
			<xsl:value-of select="ITransactionHeader/amount"/>
		</xsl:variable>
		<xsl:variable name="varAuthCode">
			<xsl:value-of select="ITransactionHeader/authorizationCode"/>
		</xsl:variable>
		<xsl:variable name="varAvsCode">
			<xsl:value-of select="ITransactionHeader/AVSCode"/>
		</xsl:variable>
		<xsl:variable name="varCvvCode">
			<xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_CARD_CIDRESPCODE']/value"/>
		</xsl:variable>
		<xsl:variable name="varStatusCode">
			<xsl:value-of select="ITransactionHeader/statusCode"/>
		</xsl:variable>
		<xsl:variable name="varCardType">
			<xsl:value-of select="ITransactionHeader/cardType"/>
		</xsl:variable>
		<xsl:variable name="varSAP">
			<xsl:call-template name="XiPayAuthorizationData">
				<xsl:with-param name="AUTRA" select="$varTransactionID"/>
				<xsl:with-param name="AUTWR" select="$varAmount"/>
				<xsl:with-param name="AUNUM" select="$varAuthCode"/>
				<xsl:with-param name="REACT" select="$varStatusCode"/>
				<xsl:with-param name="RCAVR" select="$varAvsCode"/>
				<xsl:with-param name="RCAVA" select="ITransactionHeader/AVSAddress"/>
				<xsl:with-param name="RCAVZ" select="ITransactionHeader/AVSZipCode"/>
				<xsl:with-param name="RCRSP" select="ITransactionHeader/responseCode"/>
				<xsl:with-param name="RCCVV" select="$varCvvCode"/>
				<xsl:with-param name="RTEXT" select="normalize-space(ITransactionHeader/message)"/>
				<xsl:with-param name="MERCH" select="ITransactionHeader/merchantID"/>
				<xsl:with-param name="CCINS" select="$varCardType"/>
				<xsl:with-param name="CCWAE" select="ITransactionHeader/currencyKey"/>
				<xsl:with-param name="DSIND" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_IND']/value"/>
				<xsl:with-param name="DSPSTATUS" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_PARESSTATUS']/value"/>
				<xsl:with-param name="DSVBVCAVV" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_VBVCAVV']/value"/>
				<xsl:with-param name="DSVBVXID" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_VBVXID']/value"/>
			</xsl:call-template>
		</xsl:variable>
		<VoidResult>
			<merchantTransactionCode>
				<xsl:value-of select="$varAuthCode"/>
			</merchantTransactionCode>
			<requestId>
				<xsl:value-of select="$varTransactionID"/>
			</requestId>
<!-- 			<requestToken> -->
<!-- 				<xsl:value-of select="$varSAP"/> -->
<!-- 			</requestToken> -->
			<reconciliationId>
				<xsl:value-of select="$varTransactionID"/>
			</reconciliationId>
			<xsl:call-template name="setTransactionStatus">
				<xsl:with-param name="statusCode" select="$varStatusCode"/>
			</xsl:call-template>
			<currency>
				<xsl:value-of select="ITransactionHeader/currencyKey"/>
			</currency>
			<amount>
				<xsl:value-of select="ITransactionHeader/amount"/>
			</amount>
			<requestTime>
				<xsl:value-of select="ITransactionHeader/authorizationDate"/>
			</requestTime>
		</VoidResult>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="From XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\5.3-Void-ITransactionHeaderRS.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth=""
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
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="..\XML\5.4-Void-VoidResult.xml" destSchemaRoot="VoidResult" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\XML\5.3-Void-ITransactionHeaderRS.xml" srcSchemaRoot="ITransactionHeader" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/"></template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->