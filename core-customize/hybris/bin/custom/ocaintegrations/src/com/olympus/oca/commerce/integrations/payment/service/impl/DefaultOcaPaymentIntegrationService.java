package com.olympus.oca.commerce.integrations.payment.service.impl;

import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import com.olympus.oca.commerce.integrations.payment.exception.PaymetricSignatureException;
import com.olympus.oca.commerce.integrations.payment.service.OcaPaymentIntegrationService;
import com.olympus.oca.commerce.integrations.payment.service.OcaPaymetricSignature;
import de.hybris.platform.commercefacades.payment.data.AccessTokenData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import javax.servlet.http.HttpServletRequest;
import com.paymetric.sdk.AccessTokenUtility;
import com.paymetric.sdk.XIConfig;

public class DefaultOcaPaymentIntegrationService implements OcaPaymentIntegrationService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultOcaPaymentIntegrationService.class);

    private final ConfigurationService configurationService;

    public DefaultOcaPaymentIntegrationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public Optional<AccessTokenData> getAccessToken() {
        Optional<String> tokenResponsePayloadOptional = getTokenResponsePayload();
        if (tokenResponsePayloadOptional.isEmpty()) {
            return Optional.empty();
        }

        return parseTokenPayload(tokenResponsePayloadOptional.get());
    }

    protected Optional<AccessTokenData> parseTokenPayload(String payload) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document doc = builder.parse(new InputSource(new StringReader(payload)));
            doc.getDocumentElement().normalize();

            final AccessTokenData accessTokenData = new AccessTokenData();
            accessTokenData.setMerchantGuid(doc.getElementsByTagName(OcaIntegrationConstants.MERCHANT_GUID).item(0).getTextContent());
            accessTokenData.setAccessToken(doc.getElementsByTagName(OcaIntegrationConstants.ACCESS_TOKEN).item(0).getTextContent());
            accessTokenData.setSignature(doc.getElementsByTagName(OcaIntegrationConstants.SIGNATURE).item(0).getTextContent());
            return Optional.of(accessTokenData);
        } catch (final ParserConfigurationException | SAXException | IOException ioException) {
            LOG.error("Exception " + ioException.getMessage());
        }
        return Optional.empty();
    }

    protected Optional<String> getTokenResponsePayload() {
        HttpURLConnection httpURLConnection = null;

        try {
            final byte[] postDataBytes = createPostData(getRequestParameters());
            final URL url = new URL(configurationService.getConfiguration().getString(OcaIntegrationConstants.PAYMETRIC_XI_URL));

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(OcaIntegrationConstants.POST_METHOD);
            httpURLConnection.setRequestProperty(OcaIntegrationConstants.CONTENT_TYPE, OcaIntegrationConstants.FORM_URL_ENCODED);
            httpURLConnection.setRequestProperty(OcaIntegrationConstants.CONTENT_LENGTH, String.valueOf(postDataBytes.length));
            httpURLConnection.setDoOutput(true);
            httpURLConnection.getOutputStream().write(postDataBytes);

            final int status = httpURLConnection.getResponseCode();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Tokenization request sent : Response code=" + status);
            }
            if (status != HttpURLConnection.HTTP_OK) {
                LOG.error("Failed : HTTP error code : " + status);
                return Optional.empty();
            }

            final ByteArrayOutputStream result = new ByteArrayOutputStream();
            final int contentLength = Integer.parseInt(httpURLConnection.getHeaderField("Content-Length"));
            final byte[] buffer = new byte[contentLength];
            int length;
            while ((length = httpURLConnection.getInputStream().read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return Optional.of(result.toString(OcaIntegrationConstants.ENCODING));
        } catch (IOException | PaymetricSignatureException e) {
            LOG.error("Unable to request the paymetric token", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return Optional.empty();
    }

    protected Map<String, String> getRequestParameters() throws PaymetricSignatureException {
        final Map<String, String> params = new HashMap<>();
        params.put(OcaIntegrationConstants.MERCHANT_GUID, configurationService.getConfiguration().getString(OcaIntegrationConstants.PAYMETRIC_GUID));
        params.put(OcaIntegrationConstants.SESSION_REQUEST_TYPE, "1");
        Optional<String> signatureOptional = getSignatureForRequest();
        if (signatureOptional.isEmpty()) {
            throw new PaymetricSignatureException("Unable to prepare the paymetric signature. Check PACKET_XML and SHARED_KEY configuration");
        }
        params.put(OcaIntegrationConstants.SIGNATURE, signatureOptional.get());
        params.put(OcaIntegrationConstants.MERCHANT_DEVELOPMENT_ENVIRONMENT, OcaIntegrationConstants.JAVA);
        params.put(OcaIntegrationConstants.PACKET, configurationService.getConfiguration().getString(OcaIntegrationConstants.PACKET_XML));
        return params;
    }

    protected Optional<String> getSignatureForRequest() {
        return OcaPaymetricSignature.getSignature(configurationService.getConfiguration().getString(OcaIntegrationConstants.PACKET_XML),
                                                  configurationService.getConfiguration().getString(OcaIntegrationConstants.SHARED_KEY));
    }

    protected byte[] createPostData(final Map<String, String> params) throws UnsupportedEncodingException {
        Assert.notNull(params, "request parameters must be provided");
        final StringBuilder result = new StringBuilder();
        for (final Map.Entry<String, String> param : params.entrySet()) {
            if (result.length() != 0) {
                result.append('&');
            }
            result.append(URLEncoder.encode(param.getKey(), OcaIntegrationConstants.ENCODING));
            result.append('=');
            result.append(URLEncoder.encode(param.getValue(), OcaIntegrationConstants.ENCODING));
        }
        return result.toString().getBytes(OcaIntegrationConstants.ENCODING);
    }

	@Override
	public String getCardDetails(final String accessToken)
	{
        if(RequestContextHolder.getRequestAttributes() instanceof final ServletRequestAttributes servReq) {
            final HttpServletRequest request = servReq.getRequest();
            request.setAttribute("accessToken", accessToken);
            return AccessTokenUtility.GetResponsePacket(XIConfig.CC_GUID.get(), XIConfig.CC_PSK.get(),
                                                        XIConfig.URL.get(), accessToken);
        }
		return "";
	}
}
