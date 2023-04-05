package com.olympus.oca.commerce.validators;

import com.olympus.oca.commerce.facades.company.OcaB2BCustomerFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class OcaAccountVerificationValidator implements Validator {

    private OcaB2BCustomerFacade ocaB2BCustomerFacade;
    @Override
    public boolean supports(Class<?> arg0) {
        return String.class.isAssignableFrom(arg0);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        final String emailId = (String) obj;
        B2BCustomerModel b2BCustomerModel = ocaB2BCustomerFacade.findExistingCustomer(emailId);
        if (b2BCustomerModel == null){
            errors.reject("ERROR_USER_DOESNT_EXIST");
        }
        else if(null != b2BCustomerModel && b2BCustomerModel.isActivationStatus()){
            errors.reject("ERROR_USER_REGISTERED");
        }
    }

    public OcaB2BCustomerFacade getOcaB2BCustomerFacade() {
        return ocaB2BCustomerFacade;
    }

    public void setOcaB2BCustomerFacade(OcaB2BCustomerFacade ocaB2BCustomerFacade) {
        this.ocaB2BCustomerFacade = ocaB2BCustomerFacade;
    }
}
