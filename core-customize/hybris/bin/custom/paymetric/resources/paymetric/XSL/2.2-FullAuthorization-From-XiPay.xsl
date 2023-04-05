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
		<xsl:variable name="varCvvResponseInfoitem">
			<xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_INFOITEM']/value"/>
		</xsl:variable>
		<xsl:variable name="varCvvCode">
			<xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key=$varCvvResponseInfoitem]/value"/>
		</xsl:variable>
		<xsl:variable name="varStatusCode">
			<xsl:value-of select="ITransactionHeader/statusCode"/>
		</xsl:variable>
		<xsl:variable name="varCardType">
			<xsl:value-of select="ITransactionHeader/cardType"/>
		</xsl:variable>
		<xsl:variable name="varFraudEnabled">
			<xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_FRAUD_ENABLED']/value"/>
		</xsl:variable>
		<xsl:variable name="varResponseCode">
			<xsl:call-template name="AuthorizationResponseCode">
				<xsl:with-param name="packet" select="ITransactionHeader"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="varAuthDate">
			<xsl:value-of select="substring-before(ITransactionHeader/authorizationDate,'T')"/>
		</xsl:variable>
		
		<xsl:variable name="varAuthTime">
			<xsl:value-of select="ITransactionHeader/authorizationTime" />
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
				<xsl:with-param name="RCRSP" select="$varResponseCode"/>
				<xsl:with-param name="RCCVV" select="$varCvvCode"/>
				<xsl:with-param name="RTEXT" select="normalize-space(ITransactionHeader/message)"/>
				<xsl:with-param name="MERCH" select="ITransactionHeader/merchantID"/>
				<xsl:with-param name="CCINS" select="$varCardType"/>
				<xsl:with-param name="CCWAE" select="ITransactionHeader/currencyKey"/>
				<xsl:with-param name="AUTHDATE" select="$varAuthDate"/>
				<xsl:with-param name="AUTHTIME" select="$varAuthTime"/>
				<xsl:with-param name="DSIND" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_IND']/value"/>
				<xsl:with-param name="DSPSTATUS" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_PARESSTATUS']/value"/>
				<xsl:with-param name="DSVBVCAVV" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_VBVCAVV']/value"/>
				<xsl:with-param name="DSVBVXID" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_VBVXID']/value"/>
				<xsl:with-param name="DSXID" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_XID']/value"/>
				<xsl:with-param name="DSVERSION" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_ECOMM_3DSVERSION']/value"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="varFraudCode">
			<xsl:call-template name="passedFraud">
				<xsl:with-param name="fraudEnabled" select="$varFraudEnabled"/>
				<xsl:with-param name="fraudCodes" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_FRAUD_RESULT']/value"/>
				<xsl:with-param name="fraudCode" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_FRAUD_RESULTSTATUS']/value"/>
				<xsl:with-param name="statusCode" select="$varStatusCode"/>
			</xsl:call-template>
		</xsl:variable>

		<AuthorizationResult>
			<accountBalance>
				<xsl:choose>
					<xsl:when test="$varCardType = 'PC'"><xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_CARD_BALANCEAMT']/value"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="0"/></xsl:otherwise>
				</xsl:choose>
			</accountBalance>
			<authorizationCode>
				<xsl:value-of select="$varAuthCode"/>
			</authorizationCode>
			<authorizationTime>
				<xsl:value-of select="ITransactionHeader/authorizationDate"/>
			</authorizationTime>
			<avsStatus>
				<xsl:call-template name="matchedCheck">
					<xsl:with-param name="mustPass" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_AVS_MUST_PASS']/value"/>
					<xsl:with-param name="codes" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_AVS_CODES']/value"/>
					<xsl:with-param name="code" select="$varAvsCode"/>
				</xsl:call-template>
			</avsStatus>
			<currency>
				<xsl:value-of select="ITransactionHeader/currencyKey"/>
			</currency>
			<cvnStatus>
				<xsl:call-template name="matchedCheck">
					<xsl:with-param name="mustPass" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_MUST_PASS']/value"/>
					<xsl:with-param name="codes" select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_CODES']/value"/>
					<xsl:with-param name="code" select="$varCvvCode"/>
				</xsl:call-template>
			</cvnStatus>
			<paymentProvider>
				<xsl:value-of select="'paymetric'"/>
			</paymentProvider>
			<reconciliationId>
				<xsl:value-of select="$varTransactionID"/>
			</reconciliationId>
			<requestId>
				<xsl:choose>
					<xsl:when test="$varCardType = 'PP'"><xsl:value-of select="ITransactionHeader/arrayOfInfoItems/infoItems/InfoItem[key='TR_TRANS_REFID']/value"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="ITransactionHeader/additionalInfo"/></xsl:otherwise>
				</xsl:choose>
			</requestId>
			<merchantTransactionCode>
				<xsl:value-of select="$varAuthCode"/>
			</merchantTransactionCode>
			<requestToken>
				<xsl:value-of select="$varSAP"/>
			</requestToken>
			<totalAmount>
				<xsl:value-of select="$varAmount"/>
			</totalAmount>

			<xsl:call-template name="setTransactionStatus">
				<xsl:with-param name="statusCode" select="$varFraudCode"/>
			</xsl:call-template>

		</AuthorizationResult>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Full Authorization From XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\2.3-FullAuthorization-ITransactionHeaderRS.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes"
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
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="..\XML\2.4-FullAuthorization-AuthorizationResult.xml" destSchemaRoot="AuthorizationResult" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\XML\2.3-FullAuthorization-ITransactionHeaderRS.xml" srcSchemaRoot="ITransactionHeader" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="AuthorizationResult/accountBalance/xsl:value-of" x="325" y="216"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose" x="375" y="150"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/=[0]" x="329" y="144"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/xsl:when/xsl:choose" x="384" y="150"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/xsl:when/xsl:choose/contains[0]" x="338" y="144"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/xsl:when/xsl:choose/xsl:when/xsl:value-of" x="334" y="180"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/xsl:when/xsl:choose/xsl:otherwise/xsl:value-of" x="294" y="180"/>
				<block path="AuthorizationResult/avsStatus/xsl:choose/xsl:otherwise/xsl:value-of" x="285" y="180"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose" x="215" y="168"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/=[0]" x="169" y="162"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/xsl:when/xsl:choose" x="184" y="168"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/xsl:when/xsl:choose/contains[0]" x="138" y="162"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/xsl:when/xsl:choose/xsl:when/xsl:value-of" x="374" y="198"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/xsl:when/xsl:choose/xsl:otherwise/xsl:value-of" x="134" y="198"/>
				<block path="AuthorizationResult/cvnStatus/xsl:choose/xsl:otherwise/xsl:value-of" x="165" y="198"/>
				<block path="AuthorizationResult/paymentProvider/xsl:value-of" x="334" y="270"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->