package com.olympus.oca.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;

public class LinkUtils {

	public static String resolve(final ResourceResolver resolver, final String path) {

		String mappedURL = null;
		final String targetURL = getTargetUrl(path);
		if (resolver != null && targetURL != null && !StringUtils.isEmpty(targetURL)) {
			mappedURL = resolver.map(targetURL);
		}

		return mappedURL;
	}

	public static String getTargetUrl(final String path) {
		String result = null;
		if (StringUtils.isBlank(path)) {
			result = StringUtils.EMPTY;
		} else if (path.startsWith("https:") || path.startsWith("http:")) {
			result = path;
		} else if (path.startsWith("/content") && path.indexOf(".html") == -1) {
			result = path + ".html";
		} else {
			result = path;
		}
		return result;
	}

}