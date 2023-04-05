package com.olympus.oca.commerce.validators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerAccountData;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.commercefacades.user.data.CustomerConsentData;
import de.hybris.platform.core.model.user.UserModel;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaCreateCustomerValidatorTest {

    public static final String TEST_GMAIL_COM = "test$@gmail.com";
    public static final String ACCOUNT_ID = "accountId";

    public static final String CUSTOMER_ID = "customerId";
    public static final String NAME = "name";
    public static final String JOB_CATEGORY = "jobCategory";
    public static final String PASSWORD = "password";
    public static final String CONTACT_ID = "contactID";

    public static final String CONSENT_INFO="consentInfo";
    public static final String ACCOUNTS_INFO = "accountsInfo";
    @Mock
    private Errors errors;

    private static final Logger LOG = LoggerFactory.getLogger(OcaCreateCustomerValidator.class);

    @Mock
    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

    @Mock
    private B2BUnitModel b2BUnitModel;

    @Mock
    private CustomerActivationData custActivationData;

    @Mock
    private CustomerAccountData account;

    @Spy
    @InjectMocks
    private OcaCreateCustomerValidator ocaCreateCustomerValidator;

    @Before
    public void setUp(){
        when(errors.getFieldValue(CUSTOMER_ID)).thenReturn(CUSTOMER_ID);
        when(errors.getFieldValue(NAME)).thenReturn(NAME);
        when(errors.getFieldValue(JOB_CATEGORY)).thenReturn(JOB_CATEGORY);
        when(errors.getFieldValue(PASSWORD)).thenReturn(PASSWORD);
        when(errors.getFieldValue(CONTACT_ID)).thenReturn(CONTACT_ID);
        when(errors.getFieldValue(CONSENT_INFO)).thenReturn(CONSENT_INFO);
        when(custActivationData.getAccountsInfo()).thenReturn(List.of(account));
        when(account.getAccountId()).thenReturn(ACCOUNT_ID);
        when(b2bUnitService.getUnitForUid(ACCOUNT_ID)).thenReturn(b2BUnitModel);
    }

    @Test
    public void testValidateErrorsInvalidUserEmail()
    {
        when(custActivationData.getEmailId()).thenReturn(TEST_GMAIL_COM);
        when(errors.getFieldValue(ACCOUNTS_INFO)).thenReturn(ACCOUNTS_INFO);
        ocaCreateCustomerValidator.validate(custActivationData, errors);
        verify(errors).rejectValue("emailId","ERROR_INVALID_USER_EMAIL");
        verifyMethodCalls(TEST_GMAIL_COM);
        verify(ocaCreateCustomerValidator).validateEmailAddress(TEST_GMAIL_COM);
        verify(errors).getFieldValue(ACCOUNTS_INFO);
    }

    @Test
    public void testValidateErrorsEmailBlank()
    {
        when(custActivationData.getEmailId()).thenReturn(StringUtils.EMPTY);
        when(errors.getFieldValue(ACCOUNTS_INFO)).thenReturn(ACCOUNTS_INFO);
        ocaCreateCustomerValidator.validate(custActivationData, errors);
        verify(errors).rejectValue("emailId","ERROR_INVALID_USER_EMAIL");
        verifyMethodCalls(StringUtils.EMPTY);
        verify(errors).getFieldValue(ACCOUNTS_INFO);
    }

    @Test
    public void testValidateErrorsEmailLengthInvalid()
    {
        String email = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        when(custActivationData.getEmailId()).thenReturn(email);
        ocaCreateCustomerValidator.validate(custActivationData, errors);
        verify(errors).rejectValue("emailId","ERROR_INVALID_USER_EMAIL");
        verifyMethodCalls(email);
    }

    @Test
    public void testValidateErrorsInvalidUserAccount() {
        String emailId = "test@mailsac.com";
        when(custActivationData.getEmailId()).thenReturn(emailId);
        when(b2bUnitService.getUnitForUid(ACCOUNT_ID)).thenReturn(null);
        ocaCreateCustomerValidator.validate(custActivationData, errors);
        verify(errors).reject("ERROR_INVALID_USER_ACCOUNTID");
        verifyMethodCalls(emailId);
        verify(ocaCreateCustomerValidator).validateEmailAddress(emailId);
    }

    @Test
    public void testValidateNoErrors() {
        String emailId = "test@mailsac.com";
        when(custActivationData.getEmailId()).thenReturn(emailId);
        ocaCreateCustomerValidator.validate(custActivationData, errors);
        Assert.assertTrue(errors.getAllErrors().isEmpty());
        verifyMethodCalls(emailId);
        verify(ocaCreateCustomerValidator).validateEmailAddress(emailId);
    }

    private void verifyMethodCalls(String emailId) {
        verify(ocaCreateCustomerValidator).validateEmail(errors, emailId);
        verify(ocaCreateCustomerValidator).validateAccountInfo(errors,List.of(account));
        verify(custActivationData).getAccountsInfo();
        verify(account).getAccountId();
        verify(b2bUnitService).getUnitForUid(ACCOUNT_ID);
        verify(errors).getFieldValue(CUSTOMER_ID);
        verify(errors).getFieldValue(NAME);
        verify(errors).getFieldValue(JOB_CATEGORY);
        verify(errors).getFieldValue(PASSWORD);
        verify(errors).getFieldValue(CONTACT_ID);
        verify(errors).getFieldValue(CONSENT_INFO);
    }

}
