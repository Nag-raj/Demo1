package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.core.enums.AccessType;
import com.olympus.oca.commerce.dto.user.AccountPreferencesWsDTO;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaAccessTypeValidatorTest {
    @Mock
    private CustomerFacade customerFacade;

    @Mock
    private Errors errors;

    @Mock
    private AccountPreferencesWsDTO accountPreferences;

    @InjectMocks
    private OcaAccessTypeValidator ocaAccessTypeValidator;

    @Test(expected = IllegalArgumentException.class)
    public void testValidateErrorsInvalidAccessType(){
        when(accountPreferences.getAccessType()).thenReturn("TestAccessType");
        ocaAccessTypeValidator.validate(accountPreferences, errors);
        verify(accountPreferences.getAccessType()).equals(AccessType.PLACE_ORDER_AND_CHECK_ORDERSTATUS.toString());
        verify(accountPreferences.getAccessType()).equals(AccessType.CHECK_ORDERSTATUS.toString());
        verify(accountPreferences,times(3)).getAccessType();
    }

    @Test
    public void testAccessTypeIsNull()
    {
        when(accountPreferences.getAccessType()).thenReturn(null);
        ocaAccessTypeValidator.validate(accountPreferences, errors);
        verify(accountPreferences, Mockito.times(1)).getAccessType();
    }

}
