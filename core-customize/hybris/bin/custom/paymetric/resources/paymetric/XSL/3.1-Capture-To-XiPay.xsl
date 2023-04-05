<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="http://xml.apache.org/xalan/java" exclude-result-prefixes="java fn">

	<xsl:import href="Hybris.Library.xsl"/>
	<xsl:import href="XiPay.Routing.Rules.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">

		<xsl:variable name="varTransactionID">
			<xsl:value-of select="substring-before(substring-after(CaptureRequest/requestToken,'&quot;AUTRA&quot;:&quot;'),'&quot;')"/>
		</xsl:variable>
		<xsl:variable name="varCardType">
			<xsl:value-of select="substring-before(substring-after(CaptureRequest/requestToken,'&quot;CCINS&quot;:&quot;'),'&quot;')"/>
		</xsl:variable>
		<xsl:variable name="varCurrency">
			<xsl:value-of select="substring-before(substring-after(CaptureRequest/requestToken,'&quot;CCWAE&quot;:&quot;'),'&quot;')"/>
		</xsl:variable>
		<xsl:variable name="varMID">
			<xsl:value-of select="substring-before(substring-after(CaptureRequest/requestToken,'&quot;MERCH&quot;:&quot;'),'&quot;')"/>
		</xsl:variable>
		<xsl:variable name="varBatchID">
			<xsl:choose>
				<xsl:when test="CaptureRequest/scheduleEnabled = 'true'">
					<xsl:value-of select="'[y]'"/>
					<xsl:value-of select="$varTransactionID"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="CaptureRequest/currentDateHour" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<ITransactionHeader>
			<amount>
				<xsl:value-of select="CaptureRequest/totalAmount"/>
			</amount>
			<batchID>
				<xsl:value-of select="$varBatchID"/>
			</batchID>
			<cardType>
				<xsl:value-of select="$varCardType"/>
			</cardType>
			<currencyKey>
				<xsl:value-of select="$varCurrency"/>
			</currencyKey>
			<merchantID>
				<xsl:value-of select="$varMID"/>
			</merchantID>
			<settlementAmount>
				<xsl:value-of select="CaptureRequest/totalAmount"/>
			</settlementAmount>
			<statusCode>0</statusCode>
			<transactionID>
				<xsl:value-of select="$varTransactionID"/>
			</transactionID>
		</ITransactionHeader>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="Capture To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\3.1-Capture-CaptureRequest.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth=""
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
			<SourceSchema srcSchemaPath="..\XML\3.1-Capture-CaptureRequest.xml" srcSchemaRoot="CaptureRequest" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="ITransactionHeader/transactionID/xsl:value-of" x="160" y="149"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->