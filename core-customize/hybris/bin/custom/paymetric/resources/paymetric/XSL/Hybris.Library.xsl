<?xml version="1.0"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- ########################################################################################
	# Hybris.Library.xsl - Standard Hybris XSLT template library
	############################################################################################# -->

	<!-- ########################################################################################
	# This template translates Card Types from Hybris to XiPay
	############################################################################################# -->
	<xsl:template name="Hybris2XiPayCardType">
	    <xsl:param name="cardType"/>
		<xsl:choose>
			<xsl:when test="$cardType = '001'"><xsl:value-of select="'VI'"/></xsl:when>
			<xsl:when test="$cardType = '002'"><xsl:value-of select="'MC'"/></xsl:when>
			<xsl:when test="$cardType = '003'"><xsl:value-of select="'AX'"/></xsl:when>
			<xsl:when test="$cardType = '004'"><xsl:value-of select="'DI'"/></xsl:when>
			<xsl:when test="$cardType = '005'"><xsl:value-of select="'DC'"/></xsl:when>
			<xsl:when test="$cardType = '007'"><xsl:value-of select="'JC'"/></xsl:when>
			<xsl:when test="$cardType = '008'"><xsl:value-of select="'PP'"/></xsl:when>
			<xsl:when test="$cardType = '009'"><xsl:value-of select="'AL'"/></xsl:when>
			<xsl:when test="$cardType = '024'"><xsl:value-of select="'SW'"/></xsl:when>
			<xsl:when test="$cardType = '999'"><xsl:value-of select="'EC'"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="'PC'"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template translates Card Types from XiPay to Hybris
	############################################################################################# -->
	<xsl:template name="XiPay2HybrisCardType">
	    <xsl:param name="cardType"/>
		<xsl:choose>
			<xsl:when test="$cardType = 'VI'"><xsl:value-of select="'001'"/></xsl:when>
			<xsl:when test="$cardType = 'MC'"><xsl:value-of select="'002'"/></xsl:when>
			<xsl:when test="$cardType = 'AX'"><xsl:value-of select="'003'"/></xsl:when>
			<xsl:when test="$cardType = 'DI'"><xsl:value-of select="'004'"/></xsl:when>
			<xsl:when test="$cardType = 'DC'"><xsl:value-of select="'005'"/></xsl:when>
			<xsl:when test="$cardType = 'JC'"><xsl:value-of select="'007'"/></xsl:when>
			<xsl:when test="$cardType = 'PP'"><xsl:value-of select="'008'"/></xsl:when>
			<xsl:when test="$cardType = 'AL'"><xsl:value-of select="'009'"/></xsl:when>
			<xsl:when test="$cardType = 'SW'"><xsl:value-of select="'024'"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="'999'"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ########################################################################################
	# This template determines if a check was successful or not
	############################################################################################# -->
	<xsl:template name="matchedCheck">
	    <xsl:param name="mustPass"/>
	    <xsl:param name="codes"/>
	    <xsl:param name="code"/>
		<xsl:choose>
			<xsl:when test="$mustPass = 'true'">
				<xsl:choose>
					<xsl:when test="string-length($code) > 0 and contains($codes, $code)">
						<xsl:value-of select="'MATCHED'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'NOT_MATCHED'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'MATCHED'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ########################################################################################
	# This template determines if a check was successful or not
	############################################################################################# -->
	<xsl:template name="passedCheck">
	    <xsl:param name="mustPass"/>
	    <xsl:param name="codes"/>
	    <xsl:param name="code"/>
		<xsl:choose>
			<xsl:when test="$mustPass = 'true'">
				<xsl:choose>
					<xsl:when test="string-length($code) > 0 and contains($codes, $code)">
						<xsl:value-of select="'true'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'false'"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'true'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template determines if a FRAUD check was successful or not
	############################################################################################# -->
	<xsl:template name="passedFraud">
	    <xsl:param name="fraudEnabled"/>
	    <xsl:param name="fraudCodes"/>
	    <xsl:param name="fraudCode"/>
	    <xsl:param name="statusCode"/>
		<xsl:choose>
			<xsl:when test="$fraudEnabled = 'true'">
				<xsl:choose>
					<xsl:when test="$statusCode = '100' and $fraudCode = '0'">
						<xsl:value-of select="'100'"/>
					</xsl:when>
					<xsl:when test="$statusCode = '100' and $fraudCode = '1'">
						<xsl:value-of select="'-998'"/>
					</xsl:when>
					<xsl:when test="$statusCode = '100' and $fraudCode = '2'">
						<xsl:value-of select="'-999'"/>
					</xsl:when>
					<xsl:when test="$statusCode != '100'">
						<xsl:value-of select="'-999'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$statusCode"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$statusCode"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template sets the proper transaction status for Hybris
	############################################################################################# -->
	<xsl:template name="setTransactionStatus">
	    <xsl:param name="statusCode"/>
		<xsl:choose>
			<xsl:when test="$statusCode = '100' or $statusCode = '101' or $statusCode = '110' or 
				$statusCode = '200' or $statusCode = '210' or 
				$statusCode = '300' or $statusCode = '310' or $statusCode = '350' or 
				$statusCode = '400' or $statusCode = '500' or $statusCode = '600'">
			  <transactionStatus>ACCEPTED</transactionStatus>
			  <transactionStatusDetails>SUCCESFULL</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '401'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>TRANSACTION_ALREADY_SETTLED_OR_REVERSED</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-50'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>INVALID_ACCOUNT_NUMBER</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-100' or $statusCode = '-200' or $statusCode = '-300' or 
				$statusCode = '-400' or $statusCode = '-500' or $statusCode = '-600'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>BANK_DECLINE</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-101' or $statusCode = '-201' or $statusCode = '-301' or 
				$statusCode = '-401' or $statusCode = '-501' or $statusCode = '-601'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>PROCESSOR_DECLINE</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-111' or $statusCode = '-211' or $statusCode = '-311'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>DUPLICATE_TRANSACTION</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-302'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>AUTHORIZATION_ALREADY_SETTLED</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-998'">
			  <transactionStatus>REVIEW</transactionStatus>
			  <transactionStatusDetails>REVIEW_NEEDED</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-999'">
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>PROCESSOR_DECLINE</transactionStatusDetails>
			</xsl:when>
			<xsl:when test="$statusCode = '-1' or $statusCode = '-2'">
			  <transactionStatus>ERROR</transactionStatus>
			  <transactionStatusDetails>TIMEOUT</transactionStatusDetails>
			</xsl:when>
			<xsl:otherwise>
			  <transactionStatus>REJECTED</transactionStatus>
			  <transactionStatusDetails>GENERAL_SYSTEM_ERROR</transactionStatusDetails>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template> 

	<!-- ########################################################################################
	# This template packages the authorization data from XiPay to Hybris for Sales Order Replication
	############################################################################################# -->
	<xsl:template name="XiPayAuthorizationData">
		<xsl:param name="AUTRA"/>
		<xsl:param name="AUTWR"/>
		<xsl:param name="AUNUM"/>
		<xsl:param name="REACT"/>
		<xsl:param name="RCAVR"/>
		<xsl:param name="RCAVA"/>
		<xsl:param name="RCAVZ"/>
		<xsl:param name="RCRSP"/>
		<xsl:param name="RCCVV"/>
		<xsl:param name="RTEXT"/>
		<xsl:param name="MERCH"/>
		<xsl:param name="CCINS"/>
		<xsl:param name="CCWAE"/>
		<xsl:param name="AUTHDATE"/>
		<xsl:param name="AUTHTIME"/>
		<xsl:param name="DSIND"/>
		<xsl:param name="DSPSTATUS"/>
		<xsl:param name="DSVBVCAVV"/>
		<xsl:param name="DSVBVXID"/>
		<xsl:param name="DSXID"/>
		<xsl:param name="DSVERSION"/>

		<xsl:value-of select="'{'"/>
			<xsl:value-of select="concat('&quot;AUTRA&quot;:&quot;', $AUTRA, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;AUTWR&quot;:&quot;', $AUTWR, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;AUNUM&quot;:&quot;', $AUNUM, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;XIPAY&quot;:&quot;', $REACT, '&quot;,')"/>
			<xsl:choose>
				<xsl:when test="$REACT = '100' or $REACT = '500'">
					<xsl:value-of select="concat('&quot;REACT&quot;:&quot;', 'A', '&quot;,')"/>
					<xsl:value-of select="concat('&quot;RTEXT&quot;:&quot;', 'APPROVED', '&quot;,')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="concat('&quot;REACT&quot;:&quot;', 'C', '&quot;,')"/>
					<xsl:value-of select="concat('&quot;RTEXT&quot;:&quot;', 'DECLINED', '&quot;,')"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:value-of select="concat('&quot;RCAVR&quot;:&quot;', $RCAVR, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;RCAVA&quot;:&quot;', $RCAVA, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;RCAVZ&quot;:&quot;', $RCAVZ, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;RCRSP&quot;:&quot;', $RCRSP, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;RCCVV&quot;:&quot;', $RCCVV, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;MERCH&quot;:&quot;', $MERCH, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;CCINS&quot;:&quot;', $CCINS, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;CCWAE&quot;:&quot;', $CCWAE, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;AUTHDATE&quot;:&quot;', $AUTHDATE, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;AUTHTIME&quot;:&quot;', $AUTHTIME, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSIND&quot;:&quot;', $DSIND, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSPSTATUS&quot;:&quot;', $DSPSTATUS, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSVBVCAVV&quot;:&quot;', $DSVBVCAVV, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSVBVXID&quot;:&quot;', $DSVBVXID, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSXID&quot;:&quot;', $DSXID, '&quot;,')"/>
			<xsl:value-of select="concat('&quot;DSVERSION&quot;:&quot;', $DSVERSION, '&quot;')"/>
			<xsl:value-of select="'}'"/>
	</xsl:template> 

</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="ToXiPay" userelativepaths="yes" externalpreview="no" url="..\XML\1.1-CardValidation-HashMap.xml" htmlbaseurl="" outputurl="" processortype="saxon8" useresolver="yes" profilemode="2" profiledepth="" profilelength=""
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
			<SourceSchema srcSchemaPath="..\XML\1.1-CardValidation-HashMap.xml" srcSchemaRoot="map" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[1]" x="714" y="219"/>
				<block path="ITransactionHeader/cardExpirationDate/xsl:value-of[2]" x="754" y="219"/>
				<block path="ITransactionHeader/cardHolderCountry/xsl:value-of" x="38" y="112"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext></TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->