package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.facades.company.impl.DefaultOcaB2BCustomerFacade;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerAccountData;
import de.hybris.platform.commercefacades.user.data.CustomerActivationData;
import de.hybris.platform.core.model.user.UserModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OcaCreateCustomerValidator implements Validator {

    private static final Logger LOG = LoggerFactory.getLogger(OcaCreateCustomerValidator.class);
    private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

    public static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

    @Override
    public boolean supports(Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LOG.debug("Entered OcaCreateCustomerValidator");
        final CustomerActivationData custActivationData = (CustomerActivationData) target;
        validateEmail(errors,custActivationData.getEmailId());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "customerId", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobCategory", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactID", FIELD_REQUIRED_MESSAGE_ID);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consentInfo", FIELD_REQUIRED_MESSAGE_ID);
        validateAccountInfo(errors,custActivationData.getAccountsInfo());
    }

    protected void validateAccountInfo(final Errors errors,  List<CustomerAccountData> accountsInfo) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountsInfo", FIELD_REQUIRED_MESSAGE_ID);

            for(CustomerAccountData account : accountsInfo){
            if(Objects.isNull(b2bUnitService.getUnitForUid(account.getAccountId()))){
                errors.reject("ERROR_INVALID_USER_ACCOUNTID");
                break;
            }
    }
    }
        protected void validateEmail ( final Errors errors, final String email){

            if (StringUtils.isEmpty(email) || (StringUtils.length(email) > 255 || !validateEmailAddress(email))) {

                errors.rejectValue("emailId", "ERROR_INVALID_USER_EMAIL");
            }

        }

        protected boolean validateEmailAddress ( final String email){
            final Matcher matcher = EMAIL_REGEX.matcher(email);
            return matcher.matches();
        }

    public void setB2bUnitService(B2BUnitService<B2BUnitModel, UserModel> b2bUnitService) {
        this.b2bUnitService = b2bUnitService;
    }
}
