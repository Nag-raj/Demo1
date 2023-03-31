package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.client.MagentoGraphqlClient;
import com.adobe.cq.commerce.graphql.client.GraphqlResponse;
import com.adobe.cq.commerce.magento.graphql.CategoryTree;
import com.adobe.cq.commerce.magento.graphql.CmsBlock;
import com.adobe.cq.commerce.magento.graphql.Query;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junitx.util.PrivateAccessor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CategoryListModelImplTest {

    @InjectMocks
    CategoryListModelImpl categoryListModel;

    protected static final String RESOURCE_TYPE_CONTAINER = "olympus/components/commerce/categorylist";

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryListModelImpl.class);

    @Mock
    private MagentoGraphqlClient magentoGraphqlClient;

    @Mock
    GraphqlResponse graphqlResponse;

    @Mock
    private Page currentPage;

    @Mock
    Resource pageRes;

    private List<CategoryTree> results = new ArrayList<>();

    private HashMap<String, Object> responseData = new HashMap();
    private HashMap<String, Object> categoryMap = new HashMap();

    @Mock
    CmsBlock cmsBlock;

    @Mock
    InheritanceValueMap inheritanceValueMap;
    HierarchyNodeInheritanceValueMap hierarchyNodeInheritanceValueMap;

    @Mock
    JsonNode jsonNode1;

    @Mock
    ObjectMapper objectMapper;

    @Mock
    Iterator<JsonNode> fields;

    CategoryTree ct = mock(CategoryTree.class);
    CategoryTree category = mock(CategoryTree.class);

    @Mock
    ResourceResolver resourceResolver;

    public AemContext context = new AemContext();

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        PrivateAccessor.setField(categoryListModel, "viewlabel", "view label");

    }

    @Test
    void init() throws NoSuchMethodException {
        String queryString = "{ categoryList(filters: {url_path: {eq: \"all\"}}) { cms_block{content} } }";
        lenient().when(magentoGraphqlClient.execute(queryString)).thenReturn(graphqlResponse);
        lenient().when(graphqlResponse.getErrors()).thenReturn(null);
        Resource pageResource = context.create()
                .resource("/content/app/en-us/page", "jcr:title", "Title Page", "cq:cifCategoryPage", "test");
        lenient().when(currentPage.getContentResource()).thenReturn(pageResource);
        lenient().when(inheritanceValueMap.getInherited("cq:cifCategoryPage", String.class)).thenReturn("test");
        lenient().when(pageRes.getResourceResolver()).thenReturn(resourceResolver);
        lenient().when(resourceResolver.map("test")).thenReturn("/content/olympus-customer-portal/us/en/my-olympus/shop/products/category-page");

        CategoryTree categoryTree = new CategoryTree();
        Query query = mock(Query.class);
        lenient().when(graphqlResponse.getData()).thenReturn(query);
        categoryTree.responseData.put("cms_block", cmsBlock);

        List<CategoryTree> results = query.getCategoryList();
        results.add(ct);
        lenient().when(query.getCategoryList()).thenReturn(results);

        CmsBlock cmsBlock1 = (CmsBlock) categoryTree.responseData.get("cms_block");
        JsonNode jsonNode = mock(JsonNode.class);
        Assertions.assertEquals(cmsBlock1,categoryTree.responseData.get("cms_block"));

        categoryListModel.init();

    }

    @Test
    void getCategoriesL1() {
        assertNotNull(categoryListModel.getCategoriesL1());
    }

    @Test
    void getCategoriesL2() {
        assertNotNull(categoryListModel.getCategoriesL2());
    }

    @Test
    void getCategoriesL3() {
        assertNotNull(categoryListModel.getCategoriesL3());
    }

    @Test
    void getViewLabel() {
        Assertions.assertEquals("view label", categoryListModel.getViewLabel());
    }
}