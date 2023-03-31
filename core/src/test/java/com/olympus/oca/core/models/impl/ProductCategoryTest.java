package com.olympus.oca.core.models.impl;

import com.olympus.oca.core.testcontext.AppAemContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class })
class ProductCategoryTest {

	ProductCategory productCategory = new ProductCategory();

	private final AemContext context = AppAemContext.newAemContext();



	@BeforeEach
	public void setup() throws Exception {
		productCategory.setId("id");
		productCategory.setUrl("/content/olympus");
		productCategory.setName("name");
		productCategory.setChildren(true);
			}

	@Test
	void test() {
		assertEquals("id",productCategory.getId());
		assertEquals("name", productCategory.getName());
		assertEquals("/content/olympus", productCategory.getUrl());
		assertEquals(true, productCategory.getChildren());
		assertSame("id", productCategory.removeQuotes("id"));
	}

}
