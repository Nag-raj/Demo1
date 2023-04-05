package com.olympus.oca.commerce.facades.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchQueryTermData;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaSearchResponseFacetsPopulatorTest
{
    @InjectMocks
    private OcaSearchResponseFacetsPopulator populator;

    @Before
    public void setUp()  {

        populator = new OcaSearchResponseFacetsPopulator();
    }

    @Test
    public void testRefineQueryRemoveFacet()  {
        SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
        SolrSearchQueryTermData term1 = new SolrSearchQueryTermData();
        term1.setKey("levelTwoCategories");
        term1.setValue("value1");
        SolrSearchQueryTermData term2 = new SolrSearchQueryTermData();
        term2.setKey("categories");
        term2.setValue("value2");
        List<SolrSearchQueryTermData> filterTerms = new ArrayList<>();
        filterTerms.add(term1);
        filterTerms.add(term2);
        searchQueryData.setFilterTerms(filterTerms);

        SolrSearchQueryData result = populator.refineQueryRemoveFacet(searchQueryData, "levelTwoCategories", "value1");

        Assert.assertEquals(1, result.getFilterTerms().size());
        Assert.assertEquals("levelTwoCategories", result.getFilterTerms().get(0).getKey());
        Assert.assertEquals("value1", result.getFilterTerms().get(0).getValue());
    }

    @Test
    public void testRefineQueryAddFacet()  {
        SolrSearchQueryData searchQueryData = new SolrSearchQueryData();
        SolrSearchQueryTermData term1 = new SolrSearchQueryTermData();
        term1.setKey("allCategories");
        term1.setValue("value1");
        List<SolrSearchQueryTermData> filterTerms = new ArrayList<>();
        filterTerms.add(term1);
        searchQueryData.setFilterTerms(filterTerms);

        SolrSearchQueryData result = populator.refineQueryAddFacet(searchQueryData, "facet2", "value2");

        Assert.assertEquals(2, result.getFilterTerms().size());
        Assert.assertEquals("facet2", result.getFilterTerms().get(1).getKey());
        Assert.assertEquals("value2", result.getFilterTerms().get(1).getValue());
    }
}
