package com.olympus.oca.commerce.integrations.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class OcaPaymetricSignature {
    private static final Logger LOG = LoggerFactory.getLogger(OcaPaymetricSignature.class);

    private OcaPaymetricSignature() {
        throw new UnsupportedOperationException("Cannot instantiate a static class.");
    }

    public static Optional<String> getSignature(final String packetXml, final String sharedKey) {
        try {
            final Mac hmacAlgorithm = Mac.getInstance("HmacSHA256");
            final SecretKeySpec sharedSecretKey = new SecretKeySpec(sharedKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacAlgorithm.init(sharedSecretKey);
            return Optional.of(Base64.getEncoder().encodeToString(hmacAlgorithm.doFinal(packetXml.getBytes(StandardCharsets.UTF_8))));
        } catch (final NoSuchAlgorithmException | InvalidKeyException exception) {
            LOG.error("Exception occurred while generating the signature", exception);
        }
        return Optional.empty();
    }
}

