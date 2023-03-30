package com.olympus.oca.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.olympus.oca.core.services.EnvironmentDetails;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import junit.framework.Assert;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ExternalizerServiceImplTest {

    private String path = "/content/olympus-customer-portal/us/en/my-olympus/products/category-page";

    @InjectMocks
    ExternalizerServiceImpl externalizerService;

    @Mock
    private Externalizer externalizer;

    @Mock
    private EnvironmentDetails environmentDetails;

    @Mock
    ResourceResolver resourceResolver;

    @Test
    void buildExternalLink() {
        lenient().when(environmentDetails.getEnvironmentRunMode()).thenReturn(Externalizer.AUTHOR);
        lenient().when(externalizer.authorLink(resourceResolver,path)).thenReturn("https://author-p96561-e882978.adobeaemcloud.com/content/olympus-customer-portal/us/en/my-olympus/products/category-page.html");
        externalizerService.buildExternalLink(path,resourceResolver);
        Assertions.assertEquals("https://author-p96561-e882978.adobeaemcloud.com/content/olympus-customer-portal/us/en/my-olympus/products/category-page.html", externalizer.authorLink(resourceResolver,path));
    }

    @Test
    void buildPublishLink(){
        lenient().when(environmentDetails.getEnvironmentRunMode()).thenReturn(Externalizer.PUBLISH);
        lenient().when(externalizer.publishLink(resourceResolver,path)).thenReturn("https://publish-p96561-e882978.adobeaemcloud.com/us/en/my-olympus/products/category-page.html/1005.html");
        externalizerService.buildExternalLink(path,resourceResolver);
        Assertions.assertEquals("https://publish-p96561-e882978.adobeaemcloud.com/us/en/my-olympus/products/category-page.html/1005.html", externalizer.publishLink(resourceResolver,path));
    }
}