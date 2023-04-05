package com.olympus.oca.commerce.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.List;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaB2BUnitValidatorTest {

    public static final String TEST_UID = "test_uid";
    @Mock
    private Errors errors;
    
    @Mock
    private CustomerFacade customerFacade;
    
    @Mock
    private CustomerData customerData;

    @Mock
    private B2BUnitData b2bUnitData;
    @InjectMocks
    private OcaB2bUnitValidator ocaB2bUnitValidator;
    
    @Test
    public void validateErrorsUnitIdBlank() {
        when(customerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(customerData.getUnits()).thenReturn(List.of(b2bUnitData));
        ocaB2bUnitValidator.validate(StringUtils.EMPTY, errors);
        verify(errors).reject("user.b2bUnitIdInvalid");
        verifyNoInteractions(b2bUnitData);
    }

    @Test
    public void validateErrorsUnitListBlank() {
        when(customerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(customerData.getUnits()).thenReturn(List.of());
        ocaB2bUnitValidator.validate(TEST_UID, errors);
        verify(errors).reject("user.b2bUnitIdInvalid");
        verifyNoInteractions(b2bUnitData);
    }

    @Test
    public void validateErrorsEmpty() {
        when(customerFacade.getCurrentCustomer()).thenReturn(customerData);
        when(customerData.getUnits()).thenReturn(List.of(b2bUnitData));
        when(b2bUnitData.getUid()).thenReturn(TEST_UID);
        ocaB2bUnitValidator.validate(TEST_UID, errors);
        Assert.assertTrue(errors.getAllErrors().isEmpty());
    }

}
