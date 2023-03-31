package com.olympus.oca.core.models.impl;

import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;

import lombok.experimental.Delegate;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Breadcrumb.class, resourceType = "olympus/components/breadcrumb", defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbImpl implements Breadcrumb {

	@Self
	@Via(type = ResourceSuperType.class)
	@Delegate(excludes = DelegationExclusion.class)
	private Breadcrumb breadcrumb;

	@ScriptVariable
	private Page currentPage;

	private String showbreadcrumb;

	@PostConstruct
	public void init() {
		Resource pageRes = currentPage.getContentResource();
		if (Objects.nonNull(pageRes)) {
			InheritanceValueMap inheritanceValueMap = new HierarchyNodeInheritanceValueMap(pageRes);
			showbreadcrumb = inheritanceValueMap.getInherited("showbreadcrumb", String.class);
		}
	}

	public String getShowbreadcrumb() {
		return showbreadcrumb;
	}

	private interface DelegationExclusion {
	}

}
