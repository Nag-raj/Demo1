package com.olympus.oca.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import javax.inject.Inject;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class JobCategoryModel {
    
	@Inject
    private String jobCategoryKey;
    @Inject
    private String jobCategoryValue;

    public String getJobCategoryKey() {
        return jobCategoryKey;
    }

    public String getJobCategoryValue() {
        return jobCategoryValue;
    }

}
