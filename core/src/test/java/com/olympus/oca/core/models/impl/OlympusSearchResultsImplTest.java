package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.models.common.ProductListItem;
import com.adobe.cq.commerce.core.components.models.searchresults.SearchResults;
import com.adobe.cq.commerce.core.components.storefrontcontext.SearchResultsStorefrontContext;
import com.adobe.cq.commerce.core.components.storefrontcontext.SearchStorefrontContext;
import com.adobe.cq.commerce.core.search.models.SearchResultsSet;
import com.adobe.cq.commerce.magento.graphql.*;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    Junit for OlympusSearchResultsImpl
 */

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class OlympusSearchResultsImplTest {

    @InjectMocks
    OlympusSearchResultsImpl searchResults;

    @Mock
    SearchResultsSet searchResultsSet;

    @Mock
    SearchResultsStorefrontContext searchResultsStorefrontContext;

    @Mock
    SearchStorefrontContext searchStorefrontContext;

    private Collection<ProductListItem> list = new ArrayList<>();

    @Mock
    ProductListItem item;

    @Mock
    Consumer<ProductInterfaceQuery> consumer;

    @Mock
    Function<ProductAttributeFilterInput, ProductAttributeFilterInput> function;

    @Mock
    private SearchResults results;

    @BeforeEach
    void setUp(){
        list.add(item);
        searchResults.initModel();
    }

    @Test
    void getSearchStorefrontContext() {
        lenient().when(searchResults.getSearchStorefrontContext()).thenReturn(searchStorefrontContext);
        assertNotNull(searchResults.getSearchStorefrontContext());
    }

    @Test
    void getSearchResultsStorefrontContext() {
        lenient().when(searchResults.getSearchResultsStorefrontContext()).thenReturn(searchResultsStorefrontContext);
        assertNotNull(searchResults.getSearchResultsStorefrontContext());
    }

    @Test
    void extendProductQueryWith() {
        searchResults.extendProductQueryWith(consumer);
        assertNotNull(searchResults);
    }

    @Test
    void extendProductFilterWith() {
        searchResults.extendProductFilterWith(function);
        assertNotNull(searchResults);
    }

    @Test
    void getProducts() {
        lenient().when(searchResults.getProducts()).thenReturn(list);
        assertNotNull(searchResults.getProducts());
    }

    @Test
    void getSearchResultsSet() {
        lenient().when(searchResults.getSearchResultsSet()).thenReturn(searchResultsSet);
        assertNotNull(searchResults.getSearchResultsSet());
    }

    @Test
    void loadClientPrice() {
        lenient().when(searchResults.loadClientPrice()).thenReturn(true);
        Assertions.assertEquals(true,searchResults.loadClientPrice());
    }

    @Test
    void getPaginationType() {
        lenient().when(searchResults.getPaginationType()).thenReturn("test");
        Assertions.assertEquals("test",searchResults.getPaginationType());
    }

    @Test
    void isAddToWishListEnabled() {
        assertEquals(false, searchResults.isAddToWishListEnabled());
    }

    @Test
    void isAddToCartEnabled() {
        assertEquals(false, searchResults.isAddToCartEnabled());
    }
}