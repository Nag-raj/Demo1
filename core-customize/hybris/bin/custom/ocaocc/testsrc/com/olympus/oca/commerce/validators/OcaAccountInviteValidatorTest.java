package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.constants.OcaoccConstants;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaAccountInviteValidatorTest {
    public static final String TEST_EMAIL = "testEmail";
    @Mock
    private OcaB2BCustomerFacade ocaB2BCustomerFacade;
    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Configuration configuration;

    @Mock
    private Date date;

    @Mock
    private Errors errors;

    @Mock
    private CRMCustomerAccountInviteModel customerAccountInviteModel;

    @Mock
    private B2BCustomerModel customerModel;
    @Spy
    @InjectMocks
    private OcaAccountInviteValidator ocaAccountInviteValidator;


    @Before
    public void setUp(){
        when(ocaB2BCustomerFacade.getCustomerInvite(anyString())).thenReturn(customerAccountInviteModel);
        when(ocaB2BCustomerFacade.findExistingCustomer(anyString())).thenReturn(customerModel);
        when(customerAccountInviteModel.getLastInviteTimeStamp()).thenReturn(date);
        when(customerAccountInviteModel.getEmail()).thenReturn(TEST_EMAIL);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getInt(OcaoccConstants.INVITATION_VALIDITY_DAYS)).thenReturn(8);
        when(date.getTime()).thenReturn(89l);
    }
    @Test
    public void testValidateErrorsLinkExpired()
    {
        doReturn(Boolean.FALSE).when(ocaAccountInviteValidator).isUserRegistered(TEST_EMAIL,customerAccountInviteModel);
        ocaAccountInviteValidator.validate(TEST_EMAIL,errors);
        verify(errors).reject("ERROR_LINK_EXPIRED");
    }

    @Test
    public void testValidateErrorsUserRegistered()
    {
        ocaAccountInviteValidator.validate(TEST_EMAIL, errors);
        verify(errors).reject("ERROR_USER_REGISTERED");
        verify(ocaAccountInviteValidator).isUserRegistered(TEST_EMAIL,customerAccountInviteModel);
        verify(ocaAccountInviteValidator,never()).isAccountInviteExpired(date);
    }

}
