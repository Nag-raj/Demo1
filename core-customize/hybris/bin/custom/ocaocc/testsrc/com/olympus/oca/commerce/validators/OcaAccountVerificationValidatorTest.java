package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaAccountVerificationValidatorTest {

    @Mock
    private Errors errors;

    @Mock
    private OcaB2BCustomerFacade ocaB2BCustomerFacade;

    @Mock
    private B2BCustomerModel b2BCustomerModel;

    @InjectMocks
    private OcaAccountVerificationValidator ocaAccountVerificationValidator;

    @Test
    public void testValidateErrorsIsNull() {
        when(ocaB2BCustomerFacade.findExistingCustomer(anyString())).thenReturn(null);
        ocaAccountVerificationValidator.validate(anyString(),errors);
        verify(errors).reject("ERROR_USER_DOESNT_EXIST");
    }

    @Test
    public void testValidateErrorsIsUserRegistered() {
        when(ocaB2BCustomerFacade.findExistingCustomer(anyString())).thenReturn(b2BCustomerModel);
        when(b2BCustomerModel.isActivationStatus()).thenReturn(Boolean.TRUE);
        ocaAccountVerificationValidator.validate(anyString(),errors);
        verify(errors).reject("ERROR_USER_REGISTERED");
    }
}
