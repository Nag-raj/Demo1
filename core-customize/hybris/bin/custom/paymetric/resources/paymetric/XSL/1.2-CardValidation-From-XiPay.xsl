<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:java="http://xml.apache.org/xalan/java" exclude-result-prefixes="java fn">

	<xsl:import href="Hybris.Library.xsl"/>
	<xsl:import href="XiPay.Routing.Rules.xsl"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>

	<xsl:template match="/ITransactionHeader">
		<xsl:variable name="varMonth">
			<xsl:choose>
				<xsl:when test="contains(cardExpirationDate, '/')">
					<xsl:value-of select="number(substring-before(cardExpirationDate,'/'))"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="number(substring(cardExpirationDate,1,2))"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="varYear">
			<xsl:choose>
				<xsl:when test="contains(cardExpirationDate, '/')">
					<xsl:value-of select="substring-after(cardExpirationDate,'/')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="substring(cardExpirationDate,3)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:variable name="varCvvResponseInfoitem">
			<xsl:value-of select="arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_INFOITEM']/value"/>
		</xsl:variable>
		
		<xsl:variable name="varCvvResponse">
			<xsl:value-of select="arrayOfInfoItems/infoItems/InfoItem[key=$varCvvResponseInfoitem]/value"/>
		</xsl:variable>
		
		<xsl:variable name="varCardNumberToken">
			<xsl:value-of select="substring-after(additionalInfo,'^')"/>
		</xsl:variable>
		
		
		
		<CreateSubscriptionResult>
			<requestId>
				<xsl:value-of select="transactionID"/>
			</requestId>
			<reasonCode>
				<xsl:value-of select="statusCode"/>
			</reasonCode>
			<decision>
				<xsl:choose>
					<xsl:when test="statusCode = 100">
						<xsl:value-of select="'ACCEPT'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'REJECT'"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:value-of select="'-'"/>
				<xsl:value-of select="arrayOfInfoItems/infoItems/InfoItem[key='IN_TRANS_VISATRANSID']/value"/>
				<xsl:value-of select="'-'"/>
				<xsl:value-of select="arrayOfInfoItems/infoItems/InfoItem[key='IN_TRANS_VISATRANSID2']/value"/>
				<xsl:value-of select="'-'"/>
				<xsl:value-of select="arrayOfInfoItems/infoItems/InfoItem[key='IN_TRANS_BANKNETDATE']/value"/>
				<xsl:value-of select="'-'"/>
			</decision>
			<authReplyData>
				<ccAuthReplyReasonCode>
					<xsl:value-of select="statusCode"/>
				</ccAuthReplyReasonCode>
				<ccAuthReplyAuthorizationCode>
					<xsl:value-of select="authorizationCode"/>
				</ccAuthReplyAuthorizationCode>
				<ccAuthReplyCvCode>
					<xsl:value-of select="$varCvvResponse"/>
				</ccAuthReplyCvCode>
				<cvnDecision>
					<xsl:call-template name="passedCheck">
						<xsl:with-param name="mustPass" select="arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_MUST_PASS']/value"/>
						<xsl:with-param name="codes" select="arrayOfInfoItems/infoItems/InfoItem[key='PM_CVV_CODES']/value"/>
						<xsl:with-param name="code" select="$varCvvResponse"/>
					</xsl:call-template>
				</cvnDecision>
				<ccAuthReplyAvsCodeRaw>
					<xsl:value-of select="AVSCode"/>
				</ccAuthReplyAvsCodeRaw>
				<ccAuthReplyAvsCode>
					<xsl:value-of select="AVSCode"/>
				</ccAuthReplyAvsCode>
				<ccAuthReplyAmount>
					<xsl:value-of select="amount"/>
				</ccAuthReplyAmount>
				<ccAuthReplyProcessorResponse>
					<xsl:value-of select="message"/>
				</ccAuthReplyProcessorResponse>
				<ccAuthReplyAuthorizedDateTime>
					<xsl:value-of select="authorizationDate"/>
				</ccAuthReplyAuthorizedDateTime>
			</authReplyData>
			<customerInfoData>
				<billToCity>
					<xsl:value-of select="cardHolderCity"/>
				</billToCity>
				<billToCountry>
					<xsl:value-of select="cardHolderCountry"/>
				</billToCountry>
				<billToEmail>
					<xsl:value-of select="substring-before(additionalInfo,'^')"/>
				</billToEmail>
				<billToFirstName>
					<xsl:value-of select="cardHolderName1"/>
				</billToFirstName>
				<billToLastName>
					<xsl:value-of select="cardHolderName2"/>
				</billToLastName>
				<billToPhoneNumber>
					<xsl:value-of select="PONumber"/>
				</billToPhoneNumber>
				<billToPostalCode>
					<xsl:value-of select="cardHolderZip"/>
				</billToPostalCode>
				<billToState>
					<xsl:value-of select="cardHolderState"/>
				</billToState>
				<billToStreet1>
					<xsl:value-of select="cardHolderAddress1"/>
				</billToStreet1>
				<billToStreet2>
					<xsl:value-of select="cardHolderAddress2"/>
				</billToStreet2>
			</customerInfoData>
			<paymentInfoData>
				<cardAccountNumber>
					<xsl:value-of select="$varCardNumberToken"/>
				</cardAccountNumber>
				<cardCardType>
					<xsl:call-template name="XiPay2HybrisCardType">
						<xsl:with-param name="cardType" select="cardType"/>
					</xsl:call-template>
				</cardCardType>
				<cardCvNumber>
					<xsl:value-of select="cardCVV2"/>
				</cardCvNumber>
				<cardExpirationMonth>
					<xsl:value-of select="$varMonth"/>
				</cardExpirationMonth>
				<cardExpirationYear>
					<xsl:choose>
						<xsl:when test="string-length($varYear) = 2">
							<xsl:value-of select="2000 + number($varYear)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$varYear"/>
						</xsl:otherwise>
					</xsl:choose>
				</cardExpirationYear>
				<cardAccountHolderName>
					<xsl:value-of select="cardHolderName"/>
				</cardAccountHolderName>
			</paymentInfoData>
			<orderInfoData>
				<comments>
					<xsl:value-of select="message"/>
				</comments>
				<orderNumber>
					<xsl:value-of select="orderID"/>
				</orderNumber>
				<orderPageRequestToken>
					<xsl:value-of select="cardNumber"/>
				</orderPageRequestToken>
				<orderPageTransactionType>subscription</orderPageTransactionType>
				<subscriptionTitle>
					<xsl:value-of select="client"/>
				</subscriptionTitle>
			</orderInfoData>
			<signatureData>
				<amount>
					<xsl:value-of select="amount"/>
				</amount>
				<currency>
					<xsl:value-of select="currencyKey"/>
				</currency>
				<merchantID>
					<xsl:value-of select="merchantID"/>
				</merchantID>
				<transactionSignature>
					<xsl:value-of select="transactionID"/>
				</transactionSignature>
			</signatureData>
			<subscriptionInfoData>
				<subscriptionID>
					<xsl:value-of select="transactionID"/>
				</subscriptionID>
				<subscriptionSignedValue>
					<xsl:value-of select="transactionID"/>
				</subscriptionSignedValue>
			</subscriptionInfoData>
		</CreateSubscriptionResult>
	</xsl:template>
</xsl:stylesheet>
<!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
	<scenarios>
		<scenario default="yes" name="From XiPay" userelativepaths="yes" externalpreview="no" url="..\XML\1.3-CardValidation-ITransactionHeaderRS.xml" htmlbaseurl="" outputurl="" processortype="jaxp" useresolver="yes" profilemode="2" profiledepth=""
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
		<MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="..\XML\1.2.4-CardValidation-CreateSubscriptionResult.xml" destSchemaRoot="CreateSubscriptionResult" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no">
			<SourceSchema srcSchemaPath="..\XML\1.2.3-CardValidation-ITransactionHeader.xml" srcSchemaRoot="ITransactionHeader" AssociatedInstance="" loaderFunction="document" loaderFunctionUsesURI="no"/>
		</MapperInfo>
		<MapperBlockPosition>
			<template match="/">
				<block path="CreateSubscriptionResult/decision/xsl:choose" x="414" y="42"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/=[0]" x="368" y="36"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/xsl:when/xsl:value-of" x="364" y="72"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/xsl:otherwise/xsl:value-of" x="324" y="72"/>
				<block path="CreateSubscriptionResult/authReplyData/cvnDecision/xsl:value-of" x="364" y="162"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardCardType/xsl:value-of" x="364" y="130"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardCardType/xsl:value-of/fromXiPay[0]" x="318" y="128"/>
			</template>
			<template match="/ITransactionHeader">
				<block path="CreateSubscriptionResult/requestId/xsl:value-of" x="365" y="36"/>
				<block path="CreateSubscriptionResult/reasonCode/xsl:value-of" x="405" y="54"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose" x="295" y="42"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/=[0]" x="249" y="36"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/xsl:when/xsl:value-of" x="365" y="72"/>
				<block path="CreateSubscriptionResult/decision/xsl:choose/xsl:otherwise/xsl:value-of" x="245" y="72"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyReasonCode/xsl:value-of" x="365" y="108"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyAuthorizationCode/xsl:value-of" x="405" y="126"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyCvCode/xsl:value-of" x="365" y="144"/>
				<block path="CreateSubscriptionResult/authReplyData/cvnDecision/xsl:value-of" x="61" y="217"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyAvsCodeRaw/xsl:value-of" x="365" y="180"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyAvsCode/xsl:value-of" x="405" y="198"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyAmount/xsl:value-of" x="365" y="216"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyProcessorResponse/xsl:value-of" x="405" y="234"/>
				<block path="CreateSubscriptionResult/authReplyData/ccAuthReplyAuthorizedDateTime/xsl:value-of" x="365" y="252"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToCity/xsl:value-of" x="365" y="288"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToCountry/xsl:value-of" x="205" y="157"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToEmail/xsl:value-of" x="165" y="157"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToFirstName/xsl:value-of" x="125" y="157"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToLastName/xsl:value-of" x="85" y="157"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToPhoneNumber/xsl:value-of" x="45" y="157"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToPostalCode/xsl:value-of" x="245" y="117"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToState/xsl:value-of" x="205" y="117"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToStreet1/xsl:value-of" x="165" y="117"/>
				<block path="CreateSubscriptionResult/customerInfoData/billToStreet2/xsl:value-of" x="125" y="117"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardAccountNumber/xsl:value-of" x="85" y="117"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardCardType/xsl:value-of" x="45" y="117"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardCvNumber/xsl:value-of" x="325" y="77"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardExpirationMonth/xsl:value-of" x="285" y="77"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardExpirationYear/xsl:value-of" x="205" y="77"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardExpirationYear/xsl:value-of[1]" x="126" y="145"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardStartMonth/xsl:value-of" x="165" y="77"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardStartYear/xsl:value-of" x="125" y="77"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardStartYear/xsl:value-of[1]" x="366" y="105"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardAccountHolderName/xsl:value-of" x="85" y="77"/>
				<block path="CreateSubscriptionResult/orderInfoData/comments/xsl:value-of" x="45" y="77"/>
				<block path="CreateSubscriptionResult/orderInfoData/orderNumber/xsl:value-of" x="245" y="37"/>
				<block path="CreateSubscriptionResult/orderInfoData/orderPageRequestToken/xsl:value-of" x="205" y="37"/>
				<block path="CreateSubscriptionResult/orderInfoData/subscriptionTitle/xsl:value-of" x="165" y="37"/>
				<block path="CreateSubscriptionResult/signatureData/amount/xsl:value-of" x="125" y="37"/>
				<block path="CreateSubscriptionResult/signatureData/currency/xsl:value-of" x="85" y="37"/>
				<block path="CreateSubscriptionResult/signatureData/merchantID/xsl:value-of" x="45" y="37"/>
				<block path="CreateSubscriptionResult/signatureData/transactionSignature/xsl:value-of" x="325" y="197"/>
				<block path="CreateSubscriptionResult/subscriptionInfoData/subscriptionID/xsl:value-of" x="285" y="197"/>
				<block path="CreateSubscriptionResult/subscriptionInfoData/subscriptionSignedValue/xsl:value-of" x="245" y="197"/>
				<block path="CreateSubscriptionResult/paymentInfoData/cardCardType/xsl:value-of[1]" x="76" y="257"/>
			</template>
		</MapperBlockPosition>
		<TemplateContext>
			<template match="/ITransactionHeader" mode="" srcContextPath="/ITransactionHeader/infoItems/InfoItem/value" srcContextFile="file:///d:/Hybris/v6.4/hybris/bin/custom/paymetric/resources/paymetric/XML/1.2.3-CardValidation-ITransactionHeader.xml"
			          targetContextPath="" targetContextFile=""/>
		</TemplateContext>
		<MapperFilter side="source"></MapperFilter>
	</MapperMetaTag>
</metaInformation>
-->