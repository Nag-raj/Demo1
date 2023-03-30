package com.olympus.oca.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TileModel {
    @Inject
    private String tileText;
    @Inject
    private String iconPath;
    @Inject
    private String linkPath;

    public String getIconPath() {
        return iconPath;
    }

    public String getTileText() {
        return tileText;
    }

    public String getLinkPath() {
        return linkPath;
    }

}
