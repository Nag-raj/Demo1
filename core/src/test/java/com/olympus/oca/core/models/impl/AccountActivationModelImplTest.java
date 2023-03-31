package com.olympus.oca.core.models.impl;


import com.olympus.oca.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class AccountActivationModelImplTest {

    private final AemContext context = AppAemContext.newAemContext();

    private AccountActivationModelImpl accountActivationModel;

    @BeforeEach
    public void setup() throws Exception {
        context.load().json("/com/olympus/oca/core/models/impl/AccountActivation.json","/content");
        Resource myResource = context.resourceResolver()
                .getResource("/content");
        context.currentResource(myResource);
        accountActivationModel = context.request().adaptTo(AccountActivationModelImpl.class);
    }
    @Test
    void test() {
        Assertions.assertEquals("Activate Your Account", accountActivationModel.getActivateAccount());
        Assertions.assertEquals("Lorem ipsum dolor sit amet consectetur adipisicing elit.",accountActivationModel.getActivateAccountDescription());
        Assertions.assertEquals("Personal Information",accountActivationModel .getPersonalInformation());
        Assertions.assertEquals("I have read and understand the",accountActivationModel.getTermsofuseText());
        Assertions.assertEquals("I am authorized to make purchase and check order status for the account number listed above.",accountActivationModel .getAuthorization());
        
        Assertions.assertEquals("/content/dam/olympus/icon/Info.svg", accountActivationModel.getInformationIcon());
        Assertions.assertEquals("Confirm that the email provided is a personal work email",accountActivationModel.getConfirmEmailTitle());
        Assertions.assertEquals("The email provided will be used to login to the portal and must be verified.",accountActivationModel.getConfirmEmailDesc());
        Assertions.assertEquals("Terms of Use",accountActivationModel.getTermsofuseLinkText());
        Assertions.assertEquals("/content/olympus-customer-portal/us/en/terms-of-use.html",accountActivationModel.getTermsofuseLinkUrl());
        Assertions.assertEquals("Data provided will be used in accordance with the ", accountActivationModel.getPrivacyStatementText());
        Assertions.assertEquals("Olympus Privacy Statement.",accountActivationModel.getPrivacyStatementLinkText());
        Assertions.assertEquals("/content/olympus-customer-portal/us/en/terms-of-use.html",accountActivationModel.getPrivacyStatementLinkUrl());
        Assertions.assertEquals("Job Category",accountActivationModel.getJobCategoryTitle());
        Assertions.assertEquals("Speciality",accountActivationModel.getSpecialityTitle());
        
        Assertions.assertEquals("nurse",accountActivationModel.getJobCategories().get(0).getJobCategoryKey());
        Assertions.assertEquals("Nurse Manager",accountActivationModel.getJobCategories().get(0).getJobCategoryValue());
        Assertions.assertEquals("urology",accountActivationModel.getJobSpecialities().get(0).getSpecialityKey());
        Assertions.assertEquals("Urology",accountActivationModel.getJobSpecialities().get(0).getSpecialityValue());
    }
}
