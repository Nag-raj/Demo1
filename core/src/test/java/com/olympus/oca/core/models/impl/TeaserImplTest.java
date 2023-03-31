package com.olympus.oca.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.olympus.oca.core.testcontext.AppAemContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class TeaserImplTest {

	private final AemContext context = AppAemContext.newAemContext();

	private TeaserImpl teaser;

	@BeforeEach
	public void setup() throws Exception {
		context.load().json("/com/olympus/oca/core/models/impl/Teaser.json", "/content");
		Resource myResource = context.resourceResolver().getResource("/content");
		context.currentResource(myResource);
		teaser = context.request().adaptTo(TeaserImpl.class);
	}

	@Test
	void test() {
		assertEquals("/content/dam/olympus/icons8-amazon-alexa-logo-100.png", teaser.getIcon());
		assertEquals("home title", teaser.getTitle());
		assertEquals("Take Action", teaser.getLinksBoxText());
		assertEquals("true", teaser.getLinksBox());
		assertEquals("<p>home description of the olympus teaser component</p>\r\n", teaser.getDescription());
		
		assertEquals("/content/olympus/us/en", teaser.getTiles().get(0).getIconPath());
		assertEquals("Title text1", teaser.getTiles().get(0).getTileText());
		assertEquals("/content/olympus/us/en", teaser.getTiles().get(0).getLinkPath());	
		
	}
}
