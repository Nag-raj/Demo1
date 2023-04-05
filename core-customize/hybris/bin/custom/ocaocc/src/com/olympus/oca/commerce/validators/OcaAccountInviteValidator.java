package com.olympus.oca.commerce.validators;


import com.olympus.oca.commerce.constants.OcaoccConstants;
import com.olympus.oca.commerce.core.model.CRMCustomerAccountInviteModel;
import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.util.Date;

public class OcaAccountInviteValidator implements Validator {

    private OcaB2BCustomerFacade ocaB2BCustomerFacade;
    private ConfigurationService configurationService;

    @Override
    public boolean supports(Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        final String customerId = (String) obj;
        CRMCustomerAccountInviteModel customerAccountInviteModel = ocaB2BCustomerFacade.getCustomerInvite(customerId);
        Date invitationDate = customerAccountInviteModel.getLastInviteTimeStamp();
        final String emailId = customerAccountInviteModel.getEmail();

        if (isUserRegistered(emailId, customerAccountInviteModel)) {
            errors.reject("ERROR_USER_REGISTERED");
        } else if (isAccountInviteExpired(invitationDate)) {
            errors.reject("ERROR_LINK_EXPIRED");
        }
    }

    protected boolean isUserRegistered(String emailId, CRMCustomerAccountInviteModel inviteModel) {
        B2BCustomerModel customerModel = ocaB2BCustomerFacade.findExistingCustomer(emailId);
        if (customerModel == null) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isAccountInviteExpired(Date invitationDate) {
        Date activationExpiryDate = new Date(invitationDate.getTime() + (configurationService.getConfiguration().getInt(OcaoccConstants.INVITATION_VALIDITY_DAYS)) * 24 * 60 * 60 * 1000L);
        return activationExpiryDate.before(new Date());
    }

    public OcaB2BCustomerFacade getOcaB2BCustomerFacade() {
        return ocaB2BCustomerFacade;
    }

    public void setOcaB2BCustomerFacade(OcaB2BCustomerFacade ocaB2BCustomerFacade) {
        this.ocaB2BCustomerFacade = ocaB2BCustomerFacade;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}