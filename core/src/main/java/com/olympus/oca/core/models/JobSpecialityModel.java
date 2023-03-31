package com.olympus.oca.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class JobSpecialityModel {
    
	@Inject
    private String specialityKey;
    @Inject
    private String specialityValue;

    public String getSpecialityKey() {
        return specialityKey;
    }

    public String getSpecialityValue() {
        return specialityValue;
    }

}
