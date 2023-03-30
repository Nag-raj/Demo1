package com.olympus.oca.core.models.impl;

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
class BreadcrumbImplTest {

	private final AemContext context = AppAemContext.newAemContext();

	private BreadcrumbImpl breadcrumb;

	@BeforeEach
	public void setup() throws Exception {
		context.load().json("/com/olympus/oca/core/models/impl/Breadcrumb.json", "/content");
		Resource myResource = context.resourceResolver()
				.getResource("/content/jcr:content/root/container/container/breadcrumb");
		context.currentResource(myResource);
		context.currentPage("/content");
		breadcrumb = context.request().adaptTo(BreadcrumbImpl.class);
	}

	@Test
	void test() {
		assertNotNull(breadcrumb.getShowbreadcrumb());

	}

}
