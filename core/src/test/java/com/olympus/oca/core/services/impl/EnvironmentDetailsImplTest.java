package com.olympus.oca.core.services.impl;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

/*
    Junit for EnvironmentDetailsImpl
 */

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EnvironmentDetailsImplTest {

    @InjectMocks
    EnvironmentDetailsImpl environmentDetails;

    @Mock
    private EnvironmentDetailsImpl.Configuration config;

    @Test
    void getEnvironmentRunMode() {
        lenient().when(environmentDetails.getEnvironmentRunMode()).thenReturn("author");
        assertNotNull(environmentDetails.getEnvironmentRunMode());
        Assertions.assertEquals("author", environmentDetails.getEnvironmentRunMode());
    }

    @Test
    void getEnvironmentType() {
        lenient().when(environmentDetails.getEnvironmentType()).thenReturn("DEV");
        assertNotNull(environmentDetails.getEnvironmentType());
        Assertions.assertEquals("DEV", environmentDetails.getEnvironmentType());
    }
}