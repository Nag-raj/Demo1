/**
 *
 */
package com.olympus.oca.commerce.facades.populators;


import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.commerceservices.search.facetdata.ProductCategorySearchPageData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.solrfacetsearch.search.SearchResult;
import de.hybris.platform.variants.model.VariantCategoryModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class OcaSearchResponseCategoryFacetPopulator<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, ITEM>
		implements
		Populator<SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, SearchResult>, ProductCategorySearchPageData<SolrSearchQueryData, ITEM, CategoryModel>>
{

	private CommerceCategoryService commerceCategoryService;
	private CategoryService categoryService;

	private static final String ALLCATEGORIES = "allCategories";
	private static final String FORWARD_SLASH = "/";

	@Override
	public void populate(
			final SolrSearchResponse<FACET_SEARCH_CONFIG_TYPE, INDEXED_TYPE_TYPE, INDEXED_PROPERTY_TYPE, SEARCH_QUERY_TYPE, INDEXED_TYPE_SORT_TYPE, SearchResult> source,
			final ProductCategorySearchPageData<SolrSearchQueryData, ITEM, CategoryModel> target)
	{
		final String categoryCode = source.getRequest().getSearchQueryData().getCategoryCode();
		final SearchResult solrSearchResult = source.getSearchResult();
		if (Objects.nonNull(solrSearchResult))
		{
			final Set<String> subLevelCategories = new HashSet();
			final List<FacetValueData<SolrSearchQueryData>> facetValuesList = new ArrayList();
			if (Objects.nonNull(categoryCode))
			{
				final CategoryModel currentCategory = getCommerceCategoryService().getCategoryForCode(categoryCode);
				createSubCategoryList(subLevelCategories, currentCategory);
			}
			else
			{
				final Collection<CategoryModel> rootCategories = getCategoryService()
						.getRootCategoriesForCatalogVersion(source.getRequest().getCatalogVersions().get(0));

				for (final CategoryModel rootCategory : rootCategories)
				{
					if (!(rootCategory instanceof VariantCategoryModel))
					{
						createSubCategoryList(subLevelCategories, rootCategory);
					}
				}
			}
			final List<FacetData<SolrSearchQueryData>> categoryfacets = new ArrayList();
			final List<FacetData<SolrSearchQueryData>> otherfacets = new ArrayList();
			target.getFacets().stream().forEach(facet -> {
				if (ALLCATEGORIES.equals(facet.getCode()))
				{
					categoryfacets.add(facet);
				}
				else
				{
					otherfacets.add(facet);
				}
			});
			if (CollectionUtils.isNotEmpty(categoryfacets) && categoryfacets.get(0) != null)
			{
				subLevelCategories.stream().forEach(category -> {
					final List<FacetValueData<SolrSearchQueryData>> facetValues = categoryfacets.get(0).getValues().stream()
							.filter(facetValue -> facetValue.getCode().equals(category)).collect(Collectors.toList());
					facetValuesList.addAll(facetValues);
				});
				if (CollectionUtils.isNotEmpty(facetValuesList))
				{
					categoryfacets.get(0).setValues(facetValuesList);
					otherfacets.addAll(categoryfacets);
				}
				target.setFacets(otherfacets);
			}
		}
	}

	/**
	 * @param subLevelCategories
	 * @param currentCategory
	 */
	private void createSubCategoryList(final Set<String> subLevelCategories, final CategoryModel currentCategory)
	{
		if (CollectionUtils.isNotEmpty(currentCategory.getCategories()))
		{
			for (final CategoryModel subCategory : currentCategory.getCategories())
			{
				subLevelCategories.add(subCategory.getCode());
			}
		}
	}

	/**
	 * @param property
	 * @param categoryCode
	 * @return
	 */
	private String getSubLevelCategory(final String url, String categoryCode)
	{
		String subLevelCategory = null;
		String[] element = null;
		if (null != categoryCode)
		{
			categoryCode = buildCategoryPath(categoryCode);
			final String[] subString = url.split(categoryCode);
			if (subString.length > 1)
			{
				element = subString[1].split(FORWARD_SLASH);
				subLevelCategory = element[1];
			}
		}
		else if (StringUtils.isNotEmpty(url))
		{
			element = url.split(FORWARD_SLASH);
			if (element.length > 2)
			{
				subLevelCategory = element[2];
			}
		}
		return subLevelCategory;
	}

	protected String buildCategoryPath(final String categoryCode)
	{
		final StringBuilder accumulator = new StringBuilder();
		accumulator.append('/').append(categoryCode);
		return accumulator.toString();
	}

	protected CommerceCategoryService getCommerceCategoryService()
	{
		return commerceCategoryService;
	}

	@Required
	public void setCommerceCategoryService(final CommerceCategoryService commerceCategoryService)
	{
		this.commerceCategoryService = commerceCategoryService;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	/**
	 * @param categoryService
	 *                           the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}
}
