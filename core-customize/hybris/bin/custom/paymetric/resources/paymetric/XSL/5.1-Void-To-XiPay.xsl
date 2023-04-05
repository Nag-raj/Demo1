<?xml version='1.0' ?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="http://xml.apache.org/xalan/java" exclude-result-prefixes="java fn">

	<xsl:import href="Hybris.Library.xsl"/>
	<xsl:import href="XiPay.Routing.Rules.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/">
		<ITransactionHeader>
			<cardType>
				<xsl:value-of select="substring-before(substring-after(VoidRequest/requestToken,'&quot;CCINS&quot;:&quot;'),'&quot;')"/>
			</cardType>
			<currencyKey>
				<xsl:value-of select="substring-before(substring-after(VoidRequest/requestToken,'&quot;CCWAE&quot;:&quot;'),'&quot;')"/>
			</currencyKey>
			<merchantID>
				<xsl:value-of select="substring-before(substring-after(VoidRequest/requestToken,'&quot;MERCH&quot;:&quot;'),'&quot;')"/>
			</merchantID>
			<transactionID>
				<xsl:value-of select="substring-before(substring-after(VoidRequest/requestToken,'&quot;AUTRA&quot;:&quot;'),'&quot;')"/>
			</transactionID>
		</ITransactionHeader>
	</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="To XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\5.1-Void-VoidRequest.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth="" profilelength=""
		          urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext="" validateoutput="no" validator="internal"
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
			<SourceSchema srcSchemaPath="..\XML\5.1-Void-VoidRequest.xml" srcSchemaRoot="VoidRequest" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/"></template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->