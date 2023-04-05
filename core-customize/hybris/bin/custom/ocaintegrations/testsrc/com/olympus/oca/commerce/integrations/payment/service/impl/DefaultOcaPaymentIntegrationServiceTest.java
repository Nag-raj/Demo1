package com.olympus.oca.commerce.integrations.payment.service.impl;

import com.olympus.oca.commerce.integrations.constants.OcaIntegrationConstants;
import com.olympus.oca.commerce.integrations.payment.exception.PaymetricSignatureException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.payment.data.AccessTokenData;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaPaymentIntegrationServiceTest {
    private static final String EXPECTED_POST_DATA_BYTE_ARRAY_AS_STRING
            = "[107, 101, 121, 49, 61, 118, 97, 108, 49, 38, 107, 101, 121, 50, 61, 118, 97, 108, 50]";

    private static final String TOKEN_RESPONSE_PAYLOAD = "tokenResponsePayload";
    private static final String SIGNATURE = "testSignature";
    private static final String MERCHANT_GUID = "testMerchantGuid";
    private static final String PACKET_XML = "packetXml";
    private static final String SHARED_KEY = "sharedKey";
    private static final String EXPECTED_SIGNATURE = "5YZS7UwaawpAjBQkBIWFICEk/7EASanSHQ+2a7c2HqM=";

    @Spy
    @InjectMocks
    private DefaultOcaPaymentIntegrationService classUnderTest;

    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private AccessTokenData accessTokenData;

    @Before
    public void setUp() throws Exception {
        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString(OcaIntegrationConstants.PAYMETRIC_GUID)).thenReturn(MERCHANT_GUID);
        Mockito.when(configuration.getString(OcaIntegrationConstants.PACKET_XML)).thenReturn(PACKET_XML);
        Mockito.when(configuration.getString(OcaIntegrationConstants.SHARED_KEY)).thenReturn(SHARED_KEY);
    }

    @Test
    public void testGetAccessToken() {
        Mockito.doReturn(Optional.of(TOKEN_RESPONSE_PAYLOAD)).when(classUnderTest).getTokenResponsePayload();
        Mockito.doReturn(Optional.of(accessTokenData)).when(classUnderTest).parseTokenPayload(TOKEN_RESPONSE_PAYLOAD);
        Optional<AccessTokenData> accessTokenOptional = classUnderTest.getAccessToken();
        Assert.assertFalse(accessTokenOptional.isEmpty());
        Mockito.verify(classUnderTest).getTokenResponsePayload();
        Mockito.verify(classUnderTest).parseTokenPayload(TOKEN_RESPONSE_PAYLOAD);
        Mockito.verifyNoInteractions(configurationService, accessTokenData);
    }

    @Test
    public void testGetAccessToken_emptyAccessToken() {
        Mockito.doReturn(Optional.of(TOKEN_RESPONSE_PAYLOAD)).when(classUnderTest).getTokenResponsePayload();
        Mockito.doReturn(Optional.empty()).when(classUnderTest).parseTokenPayload(TOKEN_RESPONSE_PAYLOAD);
        Optional<AccessTokenData> accessTokenOptional = classUnderTest.getAccessToken();
        Assert.assertTrue(accessTokenOptional.isEmpty());
        Mockito.verify(classUnderTest).getTokenResponsePayload();
        Mockito.verify(classUnderTest).parseTokenPayload(TOKEN_RESPONSE_PAYLOAD);
        Mockito.verifyNoInteractions(configurationService);
    }

    @Test
    public void testGetAccessToken_emptyTokenPayload() {
        Mockito.doReturn(Optional.empty()).when(classUnderTest).getTokenResponsePayload();
        Optional<AccessTokenData> accessTokenOptional = classUnderTest.getAccessToken();
        Assert.assertTrue(accessTokenOptional.isEmpty());
        Mockito.verify(classUnderTest, Mockito.never()).parseTokenPayload(anyString());
    }

    @Test
    public void testGetSignatureForRequest() {
        Optional<String> signatureForRequestOptional = classUnderTest.getSignatureForRequest();
        Assert.assertFalse(signatureForRequestOptional.isEmpty());
        Assert.assertEquals(EXPECTED_SIGNATURE, signatureForRequestOptional.get());
    }

    @Test
    public void testGetRequestParameters() throws PaymetricSignatureException {
        Mockito.doReturn(Optional.of(SIGNATURE)).when(classUnderTest).getSignatureForRequest();
        Map<String, String> requestParameters = classUnderTest.getRequestParameters();
        Assert.assertEquals(5, requestParameters.size());
        Assert.assertEquals(requestParameters.get(OcaIntegrationConstants.MERCHANT_GUID), MERCHANT_GUID);
        Assert.assertEquals(requestParameters.get(OcaIntegrationConstants.SESSION_REQUEST_TYPE), "1");
        Assert.assertEquals(requestParameters.get(OcaIntegrationConstants.SIGNATURE), SIGNATURE);
        Assert.assertEquals(requestParameters.get(OcaIntegrationConstants.MERCHANT_DEVELOPMENT_ENVIRONMENT), OcaIntegrationConstants.JAVA);
        Assert.assertEquals(requestParameters.get(OcaIntegrationConstants.PACKET), PACKET_XML);
    }

    @Test(expected = PaymetricSignatureException.class)
    public void testGetRequestParameters_noSignature() throws PaymetricSignatureException {
        Mockito.doReturn(Optional.empty()).when(classUnderTest).getSignatureForRequest();
        classUnderTest.getRequestParameters();
    }

    @Test
    public void testCreatePostData() throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        params.put("key1", "val1");
        params.put("key2", "val2");
        byte[] postData = classUnderTest.createPostData(params);
        Assert.assertEquals(EXPECTED_POST_DATA_BYTE_ARRAY_AS_STRING, Arrays.toString(postData));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePostData_nullMap() throws UnsupportedEncodingException {
        classUnderTest.createPostData(null);
    }
}