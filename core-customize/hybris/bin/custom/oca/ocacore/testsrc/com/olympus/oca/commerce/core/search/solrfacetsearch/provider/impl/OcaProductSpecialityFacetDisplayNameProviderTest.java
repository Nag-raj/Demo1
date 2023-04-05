package com.olympus.oca.commerce.core.search.solrfacetsearch.provider.impl;

import com.olympus.oca.commerce.core.enums.ExternalMaterialGroup;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.search.SearchQuery;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class OcaProductSpecialityFacetDisplayNameProviderTest extends TestCase {
    @InjectMocks
    private OcaProductSpecialityFacetDisplayNameProvider provider;
    @Mock
    private EnumerationService enumerationService;
    @Mock
    private I18NService i18NService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ExternalMaterialGroup externalMaterialGroupEnum;
    @Mock
    private SearchQuery query;
    @Before
    public void setup(){
        provider = new OcaProductSpecialityFacetDisplayNameProvider();
        provider.setCommonI18NService(commonI18NService);
        provider.setEnumerationService(enumerationService);
        provider.setI18nService(i18NService);
    }

    @Test
    public void testGetDisplayNameWhenFacetIsNull() {
        final IndexedProperty property = new IndexedProperty();
        assertEquals("", provider.getDisplayName(query, property, null));
    }

    @Test
    public void testWhenQueryIsNull(){
        final IndexedProperty property = new IndexedProperty();
        final SearchQuery query = null;
        final String facetValue = "";
        final Locale englishLocale = new Locale("en");
        Mockito.when(i18NService.getCurrentLocale()).thenReturn(englishLocale);
        provider.getDisplayName(null,property,facetValue);
        assertEquals(englishLocale,i18NService.getCurrentLocale());
    }

    @Test
    public void testWhenQueryLocaleIsNullAndQueryIsNotNull(){
        final IndexedProperty property = new IndexedProperty();
        final String facetValue = new String();
        Locale queryLocale = null;
        Mockito.when(query.getLanguage()).thenReturn("french");
        Mockito.when(commonI18NService.getLocaleForLanguage(commonI18NService.getLanguage("french"))).thenReturn(queryLocale);
        provider.getDisplayName(query,property,facetValue);
        assertEquals(queryLocale,commonI18NService.getLocaleForLanguage(commonI18NService.getLanguage(query.getLanguage())));
    }
    @Test
    public void testWhenExternalMaterialGroupNameIsNull(){
        final IndexedProperty property = new IndexedProperty();
        final Locale englishLocale = new Locale("en");
        Mockito.when(enumerationService.getEnumerationValue(ExternalMaterialGroup.class, "ENERGY")).thenReturn(externalMaterialGroupEnum);
        Mockito.when(enumerationService.getEnumerationName(externalMaterialGroupEnum, englishLocale)).thenReturn("ENERGY");
        assertEquals("ENERGY", provider.getDisplayName(query, property, "ENERGY"));
    }
}