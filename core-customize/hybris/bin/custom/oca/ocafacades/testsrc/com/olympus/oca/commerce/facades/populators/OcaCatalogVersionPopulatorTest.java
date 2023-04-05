package com.olympus.oca.commerce.facades.populators;

import com.google.common.collect.Sets;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.catalog.CatalogOption;
import de.hybris.platform.commercefacades.catalog.converters.populator.CatalogVersionPopulator;
import de.hybris.platform.commercefacades.catalog.converters.populator.CategoryHierarchyPopulator;
import de.hybris.platform.commercefacades.catalog.data.CatalogVersionData;
import de.hybris.platform.commercefacades.catalog.data.CategoryHierarchyData;
import junit.framework.TestCase;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


import java.util.Collection;
import java.util.Date;


import static de.hybris.platform.assertions.BaseCommerceAssertions.assertThat;
import static org.mockito.Mockito.lenient;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaCatalogVersionPopulatorTest extends TestCase {
    @Mock
    private OcaCatalogVersionPopulator ocaCatalogVersionPopulator;

    @Mock
    private CategoryHierarchyPopulator categoryHierarchyPopulator;
    @Mock
    private CategoryService categoryService;
    @Before
    public void setup(){
        ocaCatalogVersionPopulator = new OcaCatalogVersionPopulator();
        ocaCatalogVersionPopulator.setCategoryService(categoryService);
        ocaCatalogVersionPopulator.setCategoryHierarchyPopulator(categoryHierarchyPopulator);
    }
    @Test
    public void testPopulateWithBasic(){
        final CatalogVersionModel source = Mockito.mock(CatalogVersionModel.class);
        final CatalogVersionData target = new CatalogVersionData();
        Date date =new Date();
        Mockito.when(source.getVersion()).thenReturn("version1");
        Mockito.when(source.getModifiedtime()).thenReturn(date);
        Mockito.when(source.getCategorySystemName()).thenReturn("category1");
        final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.BASIC);
        final CategoryHierarchyData categoryData = new CategoryHierarchyData();
        categoryData.setUrl("/catalog/Online");
        ocaCatalogVersionPopulator.populate(source,target,options);
        assertEquals("version1",target.getId());
        assertEquals(date,target.getLastModified());
        assertEquals("category1",target.getName());
        assertThat(target.getCategoriesHierarchyData()).isEmpty();
        assertEquals("/catalog/Online",categoryData.getUrl());
    }

    @Test
    public void testPopulateWithCategories(){
        final CatalogVersionModel source = Mockito.mock(CatalogVersionModel.class);
        final CatalogVersionData target = new CatalogVersionData();
        Date date =new Date();
        final Collection<CategoryModel> rootCategories;
        Mockito.when(source.getVersion()).thenReturn("version2");
        Mockito.when(source.getModifiedtime()).thenReturn(date);
        Mockito.when(source.getCategorySystemName()).thenReturn("category2");
        final CategoryHierarchyData categoryData = new CategoryHierarchyData();
        categoryData.setUrl("/catalog/online");
        final Collection<CatalogOption> options = Sets.newHashSet(CatalogOption.CATEGORIES);
        final CategoryModel category = new CategoryModel();
        Mockito.when(categoryService.getRootCategoriesForCatalogVersion(source)).thenReturn(Lists.newArrayList(category));
        ocaCatalogVersionPopulator.populate(source,target,options);
        assertEquals("version2",target.getId());
        assertEquals(date,target.getLastModified());
        assertEquals("category2",target.getName());
        assertThat(target.getCategoriesHierarchyData()).hasSize(1);
        assertEquals("/catalog/online",categoryData.getUrl());
    }

}