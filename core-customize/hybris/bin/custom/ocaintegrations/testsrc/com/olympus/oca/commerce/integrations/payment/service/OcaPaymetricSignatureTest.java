package com.olympus.oca.commerce.integrations.payment.service;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.*;

import java.util.Optional;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaPaymetricSignatureTest {
    private static final String PACKET_XML = "packetXml";
    private static final String SHARED_KEY = "sharedKey";
    private static final String EXPECTED_SIGNATURE = "5YZS7UwaawpAjBQkBIWFICEk/7EASanSHQ+2a7c2HqM=";

    @Test
    public void testGetSignature() {
        Optional<String> signature = OcaPaymetricSignature.getSignature(PACKET_XML, SHARED_KEY);
        Assert.assertFalse(signature.isEmpty());
        Assert.assertEquals(EXPECTED_SIGNATURE, signature.get());
    }
}