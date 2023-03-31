package com.olympus.oca.core.models;

import java.util.List;

public interface AccountActivationModel {
	String getActivateAccount();
	String getActivateAccountDescription();
	String getPersonalInformation();
	String getAuthorization();
	String getInformationIcon();
	String getConfirmEmailTitle();
	String getConfirmEmailDesc();
	String getTermsofuseText();
	String getTermsofuseLinkText();
	String getTermsofuseLinkUrl();
	String getPrivacyStatementText();
	String getPrivacyStatementLinkText();
	String getPrivacyStatementLinkUrl();
	List<JobCategoryModel> getJobCategories();
	List<JobSpecialityModel> getJobSpecialities();
	String getSpecialityTitle();
	String getJobCategoryTitle();
}


