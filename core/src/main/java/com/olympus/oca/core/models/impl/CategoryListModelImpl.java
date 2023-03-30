package com.olympus.oca.core.models.impl;

import com.adobe.cq.commerce.core.components.client.MagentoGraphqlClient;
import com.adobe.cq.commerce.graphql.client.GraphqlResponse;
import com.adobe.cq.commerce.magento.graphql.CategoryTree;
import com.adobe.cq.commerce.magento.graphql.CmsBlock;
import com.adobe.cq.commerce.magento.graphql.Query;
import com.adobe.cq.commerce.magento.graphql.gson.Error;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olympus.oca.core.models.CategoryListModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.*;

@Model(adaptables = SlingHttpServletRequest.class,
		adapters = {CategoryListModel.class },
		resourceType = CategoryListModelImpl.RESOURCE_TYPE_CONTAINER,
		defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CategoryListModelImpl implements CategoryListModel {

	private static final String CQ_CIF_CATEGORY_PAGE = "cq:cifCategoryPage";
	private static final String SPACE = " ";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String HTML = ".html";
	private static final String SLASH = "/";
	private static final String SUBCATEGORIES = "subcategories";
	protected static final String RESOURCE_TYPE_CONTAINER = "olympus/components/commerce/categorylist";
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryListModelImpl.class);

	@Self(injectionStrategy = InjectionStrategy.OPTIONAL)
	private MagentoGraphqlClient magentoGraphqlClient;

	@ValueMapValue
	private List<ProductCategory> l1categories = new ArrayList<>();

	@ScriptVariable
	private Page currentPage;

	@RequestAttribute
	private String parent;

	@ValueMapValue
	private String viewlabel;

	String categoryPagePath;

	List<ProductCategory> productCategoryL1 = new ArrayList<>();
	Map<String,List<ProductCategory>> productCategoryL2 = new HashMap<>();
	Map<String,List<ProductCategory>> productCategoryL3 = new HashMap<>();

	@PostConstruct
	public void init() {
		String queryString = "{ categoryList(filters: {url_path: {eq: \"all\"}}) { cms_block{content} } }";

		GraphqlResponse<Query, Error> response = getQueryGraphqlResponse(queryString);
		if (response == null) return;

		Resource pageRes = currentPage.getContentResource();
		getNodeInheritanceValueMap(pageRes);
		List<CategoryTree> results = getCategoryTrees(response);

		getJsonNode(results);
	}

	private GraphqlResponse<Query, Error> getQueryGraphqlResponse(String queryString) {
		GraphqlResponse<Query, Error> response = magentoGraphqlClient.execute(queryString);
		if (CollectionUtils.isNotEmpty(response.getErrors())) {
			LOGGER.error("Error in Category Listing query {}", response.getErrors());
			return null;
		}
		return response;
	}

	private void getNodeInheritanceValueMap(Resource pageRes) {
		if (Objects.nonNull(pageRes)) {
			InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(pageRes);
			String categroyPage = inheritanceValueMap.getInherited(CQ_CIF_CATEGORY_PAGE, String.class);
			ResourceResolver resourceResolver = pageRes.getResourceResolver();
				categoryPagePath = resourceResolver.map(categroyPage);
		}
	}

	private List<CategoryTree> getCategoryTrees(GraphqlResponse<Query, Error> response) {
		Query rootQuery = response.getData();
		List<CategoryTree> results = rootQuery.getCategoryList();
		if (results.isEmpty() || results.get(0) == null) {
			LOGGER.warn("No result for CategoryListing");
		}
		return results;
	}

	private void getJsonNode(List<CategoryTree> results) {
		CategoryTree category = results.get(0);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode1 = null;
		try {
			CmsBlock cmsBlock = (CmsBlock) category.responseData.get("cms_block");
			jsonNode1 = objectMapper.readTree(cmsBlock.responseData.get("content").toString());
			jsonNode1 = jsonNode1.get("categoryHierarchy");
		} catch (Exception e) {
			LOGGER.error("Error in Category Listing to fetch category heirarchy {}", e.getMessage());
		}
		if(jsonNode1!= null) {
			Iterator<JsonNode> fields = jsonNode1.iterator();
			while (fields.hasNext()){
				ProductCategory productCategory = new ProductCategory();
				JsonNode next = fields.next();
				productCategory.setId(next.get(ID).toString());
				productCategory.setName(next.get(NAME).toString());
				productCategory.setUrl(categoryPagePath + HTML + SLASH + productCategory.removeQuotes(next.get(ID).toString()) + HTML);
				l1categories.add(productCategory);
				if(next.get(SUBCATEGORIES) != null)
				{
					getSecondLevelCategories(productCategory, next);
				}
			}
			productCategoryL1.addAll(l1categories);
		}
	}

	private void getSecondLevelCategories(ProductCategory productCategory, JsonNode next) {
		List<ProductCategory> l2categories = new ArrayList<>();
		Iterator<JsonNode> fields1 = next.get(SUBCATEGORIES).iterator();
		while (fields1.hasNext()){
			ProductCategory childProductCategory = new ProductCategory();
			JsonNode child = fields1.next();

			childProductCategory.setId(child.get(ID).toString());
			childProductCategory.setName(child.get(NAME).toString());
			childProductCategory.setUrl(categoryPagePath + HTML + SLASH + productCategory.removeQuotes(child.get(ID).toString()) + HTML);

			List<ProductCategory> l3categories = new ArrayList<>();
			if(child.get(SUBCATEGORIES) != null)
			{
				childProductCategory.setChildren(true);
				l3categories = getThirdLevelCategories(productCategory, child);
			}
			else {
				childProductCategory.setChildren(false);
			}
			l2categories.add(childProductCategory);
			if(l3categories.size() > 4) {
				ProductCategory viewL2Category = new ProductCategory();
				viewL2Category.setId("");
				viewL2Category.setName(getViewLabel() + SPACE + productCategory.removeQuotes(child.get(NAME).toString()));
				viewL2Category.setUrl(categoryPagePath + HTML + SLASH + productCategory.removeQuotes(child.get(ID).toString()) + HTML);
				l3categories.add(viewL2Category);
			}
			productCategoryL3.put(productCategory.removeQuotes(child.get(ID).toString()), l3categories);
		}
		productCategoryL2.put(productCategory.removeQuotes(next.get(ID).toString()), l2categories);
	}

	private List<ProductCategory> getThirdLevelCategories(ProductCategory productCategory, JsonNode child) {
		List<ProductCategory> l3categories = new ArrayList<>();
		Iterator<JsonNode> fields2 = child.get(SUBCATEGORIES).iterator();
		while (fields2.hasNext()){
			ProductCategory thirdChildProductCategory = new ProductCategory();
			JsonNode thirdChild = fields2.next();
			thirdChildProductCategory.setId(thirdChild.get(ID).toString());
			thirdChildProductCategory.setName(thirdChild.get(NAME).toString());
			thirdChildProductCategory.setUrl(categoryPagePath + HTML + SLASH + productCategory.removeQuotes(thirdChild.get(ID).toString()) + HTML);
			l3categories.add(thirdChildProductCategory);
		}
		return l3categories;
	}

	@Override
	public List<ProductCategory> getCategoriesL1() {
		return productCategoryL1;
	}

	@Override
	public Map<String, List<ProductCategory>> getCategoriesL2() {
		return productCategoryL2;
	}

	@Override
	public Map<String, List<ProductCategory>> getCategoriesL3() {
		return productCategoryL3;
	}

	public String getViewLabel() {
		return viewlabel;
	}
}