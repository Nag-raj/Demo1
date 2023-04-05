/**
 *
 */
package com.olympus.oca.commerce.backoffice;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.*;

import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;
import com.hybris.samlssobackoffice.BackofficeSSOService;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OcaBackofficeSSOService extends BackofficeSSOService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OcaBackofficeSSOService.class);
	private ModelService modelService;
	private UserService userService;
	private ConfigurationService configurationService;
	
	@Override
	public UserModel getOrCreateSSOUser(final String id, final String name, final Collection<String> roles)
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(name), "User info must not be empty");
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(roles), "Roles must not be empty");
		final SSOUserMapping userMapping = this.findMapping(roles);
		Preconditions.checkArgument(userMapping != null,
				"No SSO user mapping available for roles " + roles + " - cannot accept user " + id);
		String userGroupMapping =configurationService.getConfiguration().getString(OcaCoreConstants.USER_GROUP_MAPPING);
		UserModel user = super.lookupExisting(id, userMapping);
		if(null!=user && user.getGroups().stream().anyMatch(u->Arrays.stream((userGroupMapping.split(","))).toList().contains(u.getUid())))
		{
			return user;
		}
		else if(user == null)
		{
			user = super.createNewUser(id, name, userMapping);
		}
		super.adjustUserAttributes(user, userMapping);
		modelService.save(user);
		return user;
	}
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	/**
	 * @param modelService the modelService to set
	 */
	public void setModelService(ModelService modelService)
	{
		super.setModelService(modelService);
		this.modelService = modelService;
	}

	/**
	 * @param userService the userService to set
	 */
	public void setUserService(UserService userService)
	{
		super.setUserService(userService);
		this.userService = userService;
	}
}
