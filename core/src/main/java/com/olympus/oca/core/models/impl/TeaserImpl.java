package com.olympus.oca.core.models.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import com.olympus.oca.core.models.TileModel;

import lombok.experimental.Delegate;

@Model(adaptables = SlingHttpServletRequest.class,
        adapters = Teaser.class,
        resourceType = "olympus/components/teaser",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TeaserImpl implements Teaser{

    @Self
    @Via(type = ResourceSuperType.class)
    @Delegate(excludes = DelegationExclusion.class)
    private Teaser teaser;

    @SlingObject
    Resource resource;

    @ValueMapValue
    private String icon;

    @Inject
    @Named("textTileMulti/.")
    @Via ("resource")
    private List<TileModel> tiles;

    @ValueMapValue
    private String textTileMulti;

    public String getIcon() {
        return icon;
    }

    public List<TileModel> getTiles() {
        List<TileModel> tileModelList = tiles;
        return tileModelList;
    }


    @Override
    public String getTitle(){
        return resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class);
    }

    @Override
    public String getDescription(){
        return resource.getValueMap().get(JcrConstants.JCR_DESCRIPTION, String.class);
    }
    
    public String getLinksBox(){
        return resource.getValueMap().get("linksBox", String.class);
    }

    public String getLinksBoxText(){
        return resource.getValueMap().get("linksBoxText", String.class);
    }
    
    private interface DelegationExclusion {
        String getTitle();
        String getDescription();
    }

}


