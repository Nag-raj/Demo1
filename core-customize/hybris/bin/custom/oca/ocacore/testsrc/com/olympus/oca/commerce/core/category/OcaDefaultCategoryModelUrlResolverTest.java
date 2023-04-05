package com.olympus.oca.commerce.core.category;

import de.hybris.platform.category.model.CategoryModel;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OcaDefaultCategoryModelUrlResolverTest {

        private final OcaDefaultCategoryModelUrlResolver urlResolver = new OcaDefaultCategoryModelUrlResolver();

        @Test
        public void tesUrlResolver() {
            CategoryModel category = new CategoryModel();
            category.setCode("testCategory");
            String expectedUrl = "/categories/testCategory/products";
            String actualUrl = urlResolver.resolveInternal(category);
            assertEquals(expectedUrl, actualUrl);
        }
    }


