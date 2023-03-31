package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.models.common.ProductListItem;
import com.adobe.cq.commerce.core.components.models.productlist.ProductList;
import com.adobe.cq.commerce.core.components.models.retriever.AbstractCategoryRetriever;
import com.adobe.cq.commerce.core.components.storefrontcontext.CategoryStorefrontContext;
import com.adobe.cq.commerce.core.search.models.SearchResultsSet;
import com.adobe.cq.sightly.SightlyWCMMode;
import com.olympus.oca.core.models.OlympusProductList;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Model(adaptables = SlingHttpServletRequest.class, adapters = OlympusProductList.class, resourceType = OlympusProductListImpl.RESOURCE_TYPE)
public class OlympusProductListImpl implements OlympusProductList {

    protected static final String RESOURCE_TYPE = "olympus/components/commerce/productlist";

    @Self
    @Via(type = ResourceSuperType.class)
    private ProductList productList;

    @ScriptVariable(
            name = "wcmmode",
            injectionStrategy = InjectionStrategy.OPTIONAL
    )
    private SightlyWCMMode wcmMode;

    private AbstractCategoryRetriever categoriesRetriever;

    @PostConstruct
    public void initModel() {
        if(!this.wcmMode.isEdit()) {
            categoriesRetriever = productList.getCategoryRetriever();
            categoriesRetriever.extendProductQueryWith(p -> p
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
    }

    @Override
    public boolean showTitle() {
        return productList.showTitle();
    }

    @Override
    public String getTitle() {
        return productList.getTitle();
    }

    @Override
    public String getImage() {
        return productList.getImage();
    }

    @Override
    public boolean showImage() {
        return productList.showImage();
    }

    @Override
    public AbstractCategoryRetriever getCategoryRetriever() {
        return categoriesRetriever;
    }

    @Override
    public CategoryStorefrontContext getStorefrontContext() {
        return productList.getStorefrontContext();
    }

    @Override
    public String getMetaDescription() {
        return productList.getMetaDescription();
    }

    @Override
    public String getMetaKeywords() {
        return productList.getMetaKeywords();
    }

    @Override
    public String getMetaTitle() {
        return productList.getMetaTitle();
    }

    @Override
    public String getCanonicalUrl() {
        return productList.getCanonicalUrl();
    }

    @Override
    public Collection<ProductListItem> getProducts() {
        return productList.getProducts();
    }

    @Override
    public SearchResultsSet getSearchResultsSet() {
        return productList.getSearchResultsSet();
    }

    @Override
    public boolean loadClientPrice() {
        return productList.loadClientPrice();
    }

    @Override
    public String getPaginationType() {
        return productList.getPaginationType();
    }
}
