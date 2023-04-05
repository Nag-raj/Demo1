<?xml version='1.0' ?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="http://xml.apache.org/xalan/java" exclude-result-prefixes="java fn">

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<xsl:variable name="varBatchID" select="SettleBatchRequest/strBatchID"/>

		<ITransactionHeader-array>
		<xsl:for-each select="SettleBatchRequest/xiTransactions/ITransactionHeader">
			<ITransactionHeader>
				<batchID>
					<xsl:value-of select="$varBatchID"/>
				</batchID>
				<cardType>
					<xsl:value-of select="cardType"/>
				</cardType>
				<currencyKey>
					<xsl:value-of select="currencyKey"/>
				</currencyKey>
				<merchantID>
					<xsl:value-of select="merchantID"/>
				</merchantID>
				<settlementAmount>
					<xsl:value-of select="settlementAmount"/>
				</settlementAmount>
				<transactionID>
					<xsl:value-of select="transactionID"/>
				</transactionID>
			</ITransactionHeader>
		</xsl:for-each>
		</ITransactionHeader-array>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\6.1-SettleBatch-SettleBatchRequest.xml" htmlbaseurl="" outputurl="" processortype="jaxp" useresolver="yes" profilemode="0" profiledepth=""
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
			<SourceSchema srcSchemaPath="..\XML\6.1-SettleBatch-SettleBatchRequest.xml" srcSchemaRoot="ScheduleBatchRequest" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/"></template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->