package com.olympus.oca.commerce.integrations.outbound.service.impl;

import com.olympus.oca.commerce.integrations.model.BTPOutboundContractPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.BTPOutboundFreightPriceRequestModel;
import com.olympus.oca.commerce.integrations.model.VertexRequestModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOcaOutboundServiceTest {
    private static final String TEST_PROPERTY = "testProperty";
    private static final String TEST_REQUEST_NAME = "testRequestName";

    @Spy
    @InjectMocks
    private DefaultOcaOutboundService classUnderTest;

    @Mock
    private OutboundServiceFacade outboundServiceFacade;
    @Mock
    private BTPOutboundContractPriceRequestModel contractPriceModel;
    @Mock
    private BTPOutboundFreightPriceRequestModel freightPriceModel;
    @Mock
    private VertexRequestModel vertexModel;
    @Mock
    private ResponseEntity<Map> responseEntityMap;

    @Test
    public void testGetContractPrice() {
        classUnderTest.getContractPrice(contractPriceModel);
        Mockito.verify(outboundServiceFacade)
               .send(contractPriceModel, DefaultOcaOutboundService.OUTBOUND_CONTRACT_PRICE_OBJECT,
                     DefaultOcaOutboundService.OUTBOUND_CONTRACT_PRICE_DESTINATION);
        Mockito.verifyNoInteractions(contractPriceModel);
        Mockito.verifyNoMoreInteractions(outboundServiceFacade);
    }

    @Test
    public void testGetFreightCost() {
        classUnderTest.getFreightCost(freightPriceModel);
        Mockito.verify(outboundServiceFacade)
               .send(freightPriceModel, DefaultOcaOutboundService.OUTBOUND_FREIGHT_PRICE_OBJECT,
                     DefaultOcaOutboundService.OUTBOUND_FREIGHT_PRICE_DESTINATION);
        Mockito.verifyNoInteractions(freightPriceModel);
        Mockito.verifyNoMoreInteractions(outboundServiceFacade);
    }

    @Test
    public void testGetVertexSalesTax() {
        classUnderTest.getVertexSalesTax(vertexModel);
        Mockito.verify(outboundServiceFacade)
               .send(vertexModel, DefaultOcaOutboundService.VERTEX_SALES_TAX_OBJECT, DefaultOcaOutboundService.VERTEX_SALES_TAX_DESTINATION);
        Mockito.verifyNoInteractions(vertexModel);
        Mockito.verifyNoMoreInteractions(outboundServiceFacade);
    }

    @Test
    public void testIsSentSuccessfully_notOk() {
        Mockito.when(responseEntityMap.getStatusCode()).thenReturn(HttpStatus.NO_CONTENT);
        boolean sentSuccessfully = classUnderTest.isSentSuccessfully(responseEntityMap);
        Assert.assertFalse(sentSuccessfully);
        Mockito.verify(responseEntityMap).getStatusCode();
        Mockito.verifyNoMoreInteractions(responseEntityMap);
    }

    @Test
    public void testIsSentSuccessfully_ok() {
        Mockito.when(responseEntityMap.getStatusCode()).thenReturn(HttpStatus.OK);
        boolean sentSuccessfully = classUnderTest.isSentSuccessfully(responseEntityMap);
        Assert.assertTrue(sentSuccessfully);
        Mockito.verify(responseEntityMap).getStatusCode();
        Mockito.verifyNoMoreInteractions(responseEntityMap);
    }

    @Test
    public void testGetPropertyValue_nullBody() {
        Mockito.when(responseEntityMap.getBody()).thenReturn(null);
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyValue_nullKey() {
        Mockito.when(responseEntityMap.getBody()).thenReturn(Collections.singletonMap(null, "some value"));
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyValue_emptyKey() {
        Mockito.when(responseEntityMap.getBody()).thenReturn(Collections.singletonMap("", "some value"));
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

    @Test(expected = ClassCastException.class)
    public void testGetPropertyValue_noMap() {
        Mockito.when(responseEntityMap.getBody()).thenReturn(Collections.singletonMap("testKey", "someString"));
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPropertyValue_nullMapValue() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(TEST_PROPERTY, null);
        Mockito.when(responseEntityMap.getBody()).thenReturn(Collections.singletonMap(TEST_PROPERTY, expectedMap));
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

    @Test
    public void testGetPropertyValue() {
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put(TEST_PROPERTY, "someValue");
        Mockito.when(responseEntityMap.getBody()).thenReturn(Collections.singletonMap("testKey", expectedMap));
        classUnderTest.getPropertyValue(responseEntityMap, TEST_PROPERTY, TEST_REQUEST_NAME);
    }

}