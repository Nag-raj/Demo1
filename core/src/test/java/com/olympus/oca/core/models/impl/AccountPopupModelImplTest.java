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
class AccountPopupModelImplTest {

	private final AemContext context = AppAemContext.newAemContext();

	private AccountPopupModelImpl accountPopupModel;

	@BeforeEach
	public void setup() throws Exception {
			context.load().json("/com/olympus/oca/core/models/impl/AccountPopup.json", "/content");
			Resource myResource = context.resourceResolver()
					.getResource("/content");
			context.currentResource(myResource);
			accountPopupModel = context.request().adaptTo(AccountPopupModelImpl.class);
		}

	@Test
	void test() {
		Assertions.assertEquals("title", accountPopupModel.getTitle());
		Assertions.assertEquals("description", accountPopupModel.getDescription());
		Assertions.assertEquals("accountsDropDown", accountPopupModel.getAccountsDropDown());
	}

}
