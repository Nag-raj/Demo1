package com.olympus.oca.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.olympus.oca.core.services.EnvironmentDetails;
import com.olympus.oca.core.services.ExternalizerService;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;

/**
 * Service to get Externalized URLs
 */
@Component(immediate = true, service = ExternalizerService.class)
public class ExternalizerServiceImpl implements ExternalizerService {

    private static final Logger LOG = LoggerFactory.getLogger(java.lang.invoke.MethodHandles.lookup().lookupClass());

    /**
     * OSGI Externalizer service
     */
    @Reference
    private Externalizer externalizer;

    /**
     * Custom OSGI service to get current execution environment
     */
    @Reference
    private EnvironmentDetails environmentDetails;

    /**
     * Returns Externalized URL for the resource provided
     * @param path of the resource
     * @param resourceResolver
     * @return
     */
    @Override
    public String buildExternalLink(String path, ResourceResolver resourceResolver) {
        String externalLink = path;
        if (null != resourceResolver && StringUtils.isNotEmpty(path)) {
            if (StringUtils.equalsIgnoreCase(environmentDetails.getEnvironmentRunMode(), Externalizer.AUTHOR)) {
                externalLink = externalizer.authorLink(resourceResolver, path);
            } else {
                externalLink = externalizer.publishLink(resourceResolver, path);
            }
        }
        LOG.debug("externalLink {}", externalLink);
        return externalLink;
    }


}

