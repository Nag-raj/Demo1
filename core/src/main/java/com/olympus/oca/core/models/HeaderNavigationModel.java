package com.olympus.oca.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class HeaderNavigationModel {

	@ValueMapValue
	private String homeIcon;

	@ValueMapValue
	private String homeLabel;

	@ValueMapValue
	private String homeLinkPath;

	@ValueMapValue
	private String greetingLabel;

	@ValueMapValue
	private String helpIcon;

	@ValueMapValue
	private String helpLinkPath;

	@ValueMapValue
	private String cartIcon;

	@ValueMapValue
	private String notificationIcon;

	@ValueMapValue
	private String cartTitle;

	@ValueMapValue
	private String closeIcon;

	@ValueMapValue
	private String cartIconInOverlay;

	@ValueMapValue
	private String subHeading;

	@ValueMapValue
	private String buttonLabel;



	@ValueMapValue
	private String buttonLinkPath;

	@ValueMapValue
	private String cartPagePath;

	public String getHomeIcon() {
		return homeIcon;
	}

	public String getHomeLabel() {
		return homeLabel;
	}

	public String getHomeLinkPath() {
		return homeLinkPath;
	}

	public String getGreetingLabel() {
		return greetingLabel;
	}

	public String getHelpIcon() {
		return helpIcon;
	}

	public String getHelpLinkPath() {
		return helpLinkPath;
	}

	public String getCartIcon() {
		return cartIcon;
	}

	public String getNotificationIcon() {
		return notificationIcon;
	}

	public String getCartTitle() {
		return cartTitle;
	}

	public String getCloseIcon() {
		return closeIcon;
	}

	public String getCartIconInOverlay() {
		return cartIconInOverlay;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}



	public String getButtonLinkPath() {
		return buttonLinkPath;
	}

	public String getCartPagePath() {
		return cartPagePath;
	}


}