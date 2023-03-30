package com.olympus.oca.core.models.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.service.component.annotations.Reference;

import com.olympus.oca.core.models.AccountActivationModel;
import com.olympus.oca.core.models.JobCategoryModel;
import com.olympus.oca.core.models.JobSpecialityModel;
import com.olympus.oca.core.services.ExternalizerService;
import com.olympus.oca.core.utils.LinkUtils;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {AccountActivationModel.class },
        resourceType = AccountActivationModelImpl.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class AccountActivationModelImpl implements AccountActivationModel{
	
    protected static final String RESOURCE_TYPE = "olympus/components/accountactivation";

    @SlingObject
    private ResourceResolver resolver;
    
    @Reference
    ExternalizerService externalizerService;
    
    @ValueMapValue
    private String activateAccount;

    @ValueMapValue
    private String activateAccountDescription;

    @ValueMapValue
    private String personalInformation;

    @ValueMapValue
    private String informationIcon;

    @ValueMapValue
    private String confirmEmailTitle;
    
    @ValueMapValue
    private String confirmEmailDesc;

    @ValueMapValue
    private String termsofuseText;
    
    @ValueMapValue
    private String termsofuseLinkText;
    
    @ValueMapValue
    private String termsofuseLinkUrl;
    
    @ValueMapValue
    private String authorization;
    
    @ValueMapValue
    private String privacyStatementText;
    
    @ValueMapValue
    private String privacyStatementLinkText;
    
    @ValueMapValue
    private String privacyStatementLinkUrl;

    @ValueMapValue
    private String jobCategoryTitle;
    
    @ValueMapValue
    private String specialityTitle;

    @Inject
    @Named("jobCategoryMulti/.")
    @Via ("resource")
    private List<JobCategoryModel> jobCategories;
    
    @Inject
    @Named("specialityMulti/.")
    @Via ("resource")
    private List<JobSpecialityModel> specialities;
   
	@Override
	public String getTermsofuseText() {
		return termsofuseText;
	}

	@Override
	public String getTermsofuseLinkText() {
		return termsofuseLinkText;
	}

	@Override
	public String getTermsofuseLinkUrl() {
		if (resolver != null && termsofuseLinkUrl != null && !StringUtils.isEmpty(termsofuseLinkUrl))
		{
			return LinkUtils.resolve(resolver, termsofuseLinkUrl);
		}
		return StringUtils.EMPTY;
	}

	@Override
	public String getPrivacyStatementText() {
		return privacyStatementText;
	}

	@Override
	public String getPrivacyStatementLinkText() {
		return privacyStatementLinkText;
	}

	@Override
	public String getPrivacyStatementLinkUrl() {
		if (resolver != null && privacyStatementLinkUrl != null && !StringUtils.isEmpty(privacyStatementLinkUrl))
		{
			return LinkUtils.resolve(resolver, privacyStatementLinkUrl);
		}
		return StringUtils.EMPTY;
	}

	@Override
    public String getInformationIcon() {
		return informationIcon;
	}

	@Override
	public String getConfirmEmailTitle() {
		return confirmEmailTitle;
	}

	@Override
	public String getConfirmEmailDesc() {
		return confirmEmailDesc;
	}

	@Override
    public String getActivateAccount() {
        return activateAccount;
    }

    @Override
    public String getActivateAccountDescription() {
        return activateAccountDescription;
    }

    @Override
    public String getPersonalInformation() {
        return personalInformation;
    }

    @Override
    public String getAuthorization() {
        return authorization;
    }
    
    @Override
    public String getJobCategoryTitle() {
        return jobCategoryTitle;
    }
    
    @Override
    public String getSpecialityTitle() {
        return specialityTitle;
    }
    
    @Override
    public List<JobCategoryModel> getJobCategories()
    {
    	final List<JobCategoryModel> jobCategoryList = jobCategories;
		return jobCategoryList;
    }

    @Override
    public List<JobSpecialityModel> getJobSpecialities()
    {
    	final List<JobSpecialityModel> jobSpecialityList = specialities;
		return jobSpecialityList;
    }
}

