package com.olympus.oca.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ExternalizerService {

    /**
     * Returns externalized link for the provided resourcePath
     * @return String
     */
    public String buildExternalLink(String path, ResourceResolver resourceResolver);
}
