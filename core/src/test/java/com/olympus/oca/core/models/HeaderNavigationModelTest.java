package com.olympus.oca.core.models;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.olympus.oca.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class HeaderNavigationModelTest {

	private final AemContext context = AppAemContext.newAemContext();

	private HeaderNavigationModel headerNavigationModel;
    public static final String JSON_FILE_PATH = "/com/olympus/oca/core/models/impl/HeaderNavigationModel.json";
    public static final String CONTENT_PATH = "/content";

	@BeforeEach
	public void setup() throws Exception {
		context.load().json(JSON_FILE_PATH, CONTENT_PATH);
		Resource myResource = context.resourceResolver().getResource(CONTENT_PATH);
		context.currentResource(myResource);
		headerNavigationModel = context.currentResource().adaptTo(HeaderNavigationModel.class);
	}

	@Test
	void test() {
		assertNotNull(headerNavigationModel.getButtonLabel());
		assertNotNull(headerNavigationModel.getButtonLinkPath());
		assertNotNull(headerNavigationModel.getCartIcon());
		assertNotNull(headerNavigationModel.getCartIconInOverlay());
		assertNotNull(headerNavigationModel.getCartTitle());
		assertNotNull(headerNavigationModel.getCloseIcon());
		assertNotNull(headerNavigationModel.getGreetingLabel());
		assertNotNull(headerNavigationModel.getHelpIcon());
		assertNotNull(headerNavigationModel.getHelpLinkPath());
		assertNotNull(headerNavigationModel.getHomeIcon());
		assertNotNull(headerNavigationModel.getHomeLabel());
		assertNotNull(headerNavigationModel.getNotificationIcon());
		assertNotNull(headerNavigationModel.getSubHeading());
		assertNotNull(headerNavigationModel.getHomeLinkPath());
	}

}