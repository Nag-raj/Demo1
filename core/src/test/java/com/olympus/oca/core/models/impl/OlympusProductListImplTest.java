package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.internal.models.v1.productlist.ProductListImpl;
import com.adobe.cq.commerce.core.components.models.common.ProductListItem;
import com.adobe.cq.commerce.core.components.models.retriever.AbstractCategoryRetriever;
import com.adobe.cq.commerce.core.components.storefrontcontext.CategoryStorefrontContext;
import com.adobe.cq.commerce.core.search.models.SearchResultsSet;
import com.adobe.cq.sightly.SightlyWCMMode;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

/*
    Junit for OlympusProductListImpl
 */

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class OlympusProductListImplTest {

    @InjectMocks
    OlympusProductListImpl olympusProductList;

    @Mock
    ProductListImpl productList;

    @Mock
    private AbstractCategoryRetriever categoriesRetriever;

    @Mock
    CategoryStorefrontContext categoryStorefrontContext;

    @Mock
    SearchResultsSet searchResultsSet;

    @Mock
    private Collection<ProductListItem> list;

    @Mock
    private SightlyWCMMode wcmMode;

    ProductListItem item;

    @BeforeEach
    void setUp() {
        lenient().when(wcmMode.isEdit()).thenReturn(true, false);
        lenient().when(productList.getCategoryRetriever()).thenReturn(categoriesRetriever);
        olympusProductList.initModel();
        list.add(item);
    }

    @Test
    void showTitle() {
        lenient().when(olympusProductList.showTitle()).thenReturn(true);
        Assertions.assertEquals(true, olympusProductList.showTitle());
    }

    @Test
    void getTitle() {
        lenient().when(olympusProductList.getTitle()).thenReturn("title");
        Assertions.assertEquals("title",olympusProductList.getTitle());
    }

    @Test
    void getImage() {
        lenient().when(olympusProductList.getImage()).thenReturn("/content/dam/product1");
        Assertions.assertEquals("/content/dam/product1",olympusProductList.getImage());
    }

    @Test
    void showImage() {
        lenient().when(olympusProductList.showImage()).thenReturn(true);
        Assertions.assertEquals(true, olympusProductList.showImage());
    }

    @Test
    void getCategoryRetriever() {
        assertNotNull(olympusProductList.getCategoryRetriever());
    }

    @Test
    void getStorefrontContext() {
        lenient().when(olympusProductList.getStorefrontContext()).thenReturn(categoryStorefrontContext);
        assertNotNull(olympusProductList.getStorefrontContext());
    }

    @Test
    void getMetaDescription() {
        lenient().when(olympusProductList.getMetaDescription()).thenReturn("This is description");
        Assertions.assertEquals("This is description",olympusProductList.getMetaDescription());
    }

    @Test
    void getMetaKeywords() {
        lenient().when(olympusProductList.getMetaKeywords()).thenReturn("Keyword");
        Assertions.assertEquals("Keyword",olympusProductList.getMetaKeywords());
    }

    @Test
    void getMetaTitle() {
        lenient().when(olympusProductList.getMetaTitle()).thenReturn("metaTitle");
        Assertions.assertEquals("metaTitle",olympusProductList.getMetaTitle());
    }

    @Test
    void getCanonicalUrl() {
        lenient().when(olympusProductList.getCanonicalUrl()).thenReturn("https://abc.com");
        Assertions.assertEquals("https://abc.com",olympusProductList.getCanonicalUrl());
    }

    @Test
    void getProducts() {
        lenient().when(olympusProductList.getProducts()).thenReturn(list);
        assertNotNull(olympusProductList.getProducts());
    }

    @Test
    void getSearchResultsSet() {
        lenient().when(olympusProductList.getSearchResultsSet()).thenReturn(searchResultsSet);
        assertNotNull(olympusProductList.getSearchResultsSet());
    }

    @Test
    void loadClientPrice() {
        lenient().when(olympusProductList.loadClientPrice()).thenReturn(true);
        Assertions.assertEquals(true,olympusProductList.loadClientPrice());
    }

    @Test
    void getPaginationType() {
        lenient().when(olympusProductList.getPaginationType()).thenReturn("test");
        Assertions.assertEquals("test",olympusProductList.getPaginationType());
    }
}