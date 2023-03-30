package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.models.common.ProductListItem;
import com.adobe.cq.commerce.core.components.models.searchresults.SearchResults;
import com.adobe.cq.commerce.core.components.storefrontcontext.SearchResultsStorefrontContext;
import com.adobe.cq.commerce.core.components.storefrontcontext.SearchStorefrontContext;
import com.adobe.cq.commerce.core.search.models.SearchResultsSet;
import com.adobe.cq.commerce.magento.graphql.ProductAttributeFilterInput;
import com.adobe.cq.commerce.magento.graphql.ProductInterfaceQuery;
import com.olympus.oca.core.models.OlympusSearchResults;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy= DefaultInjectionStrategy.OPTIONAL, adapters = OlympusSearchResults.class, resourceType = OlympusSearchResultsImpl.RESOURCE_TYPE)
public class OlympusSearchResultsImpl implements OlympusSearchResults{

    protected static final String RESOURCE_TYPE = "olympus/components/commerce/searchresults";

    @Self
    @Via(type = ResourceSuperType.class)
    private SearchResults searchResults;

    private Consumer<ProductInterfaceQuery> productQueryHook;

    private Function<ProductAttributeFilterInput, ProductAttributeFilterInput> productAttributeFilterHook;

    @PostConstruct
    public void initModel() {
        searchResults.extendProductQueryWith(p -> p
                .description(desc -> desc.html())
                .stockStatus()
                .onConfigurableProduct(cp -> cp
                        .variants(v -> v.product(simpleProductQuery -> simpleProductQuery
                                .metaTitle()
                                .metaDescription()
                                .sku()
                                .stockStatus()
                                .priceRange(priceRangeQuery -> priceRangeQuery
                                        .maximumPrice(mp -> mp
                                                .finalPrice(f -> f.value())
                                        )
                                )))));
    }

    @Override
    public SearchStorefrontContext getSearchStorefrontContext() {
        return searchResults.getSearchStorefrontContext();
    }

    @Override
    public SearchResultsStorefrontContext getSearchResultsStorefrontContext() {
        return searchResults.getSearchResultsStorefrontContext();
    }

    @Override
    public void extendProductQueryWith(Consumer<ProductInterfaceQuery> consumer) {
        if (this.productQueryHook == null) {
            this.productQueryHook = consumer;
        } else {
            this.productQueryHook = this.productQueryHook.andThen(consumer);
        }

    }

    @Override
    public void extendProductFilterWith(Function<ProductAttributeFilterInput, ProductAttributeFilterInput> function) {
        if (this.productAttributeFilterHook == null) {
            this.productAttributeFilterHook = function;
        } else {
            this.productAttributeFilterHook = this.productAttributeFilterHook.andThen(function);
        }
    }

    @Override
    public Collection<ProductListItem> getProducts() {
        return searchResults.getProducts();
    }

    @Override
    public SearchResultsSet getSearchResultsSet() {
        return searchResults.getSearchResultsSet();
    }

    @Override
    public boolean loadClientPrice() {
        return searchResults.loadClientPrice();
    }

    @Override
    public String getPaginationType() {
        return searchResults.getPaginationType();
    }
}
