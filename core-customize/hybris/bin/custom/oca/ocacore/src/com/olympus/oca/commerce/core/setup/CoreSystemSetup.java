/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.olympus.oca.commerce.core.setup;

import com.olympus.oca.commerce.core.event.QuoteBuyerSubmitEventListener;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import com.olympus.oca.commerce.core.constants.OcaCoreConstants;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * This class provides hooks into the system's initialization and update processes.
 */
@SystemSetup(extension = OcaCoreConstants.EXTENSIONNAME)
public class CoreSystemSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(CoreSystemSetup.class);
	public static final String IMPORT_ACCESS_RIGHTS = "accessRights";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	/**
	 * This method will be called by system creator during initialization and system update. Be sure that this method can
	 * be called repeatedly.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void createEssentialData(final SystemSetupContext context)
	{
		importImpexFile(context, "/ocacore/import/common/essential-data.impex");
		importImpexFile(context, "/ocacore/import/common/countries.impex");
		importImpexFile(context, "/ocacore/import/common/delivery-modes.impex");

		importImpexFile(context, "/ocacore/import/common/themes.impex");
		importImpexFile(context, "/ocacore/import/common/user-groups.impex");
		importImpexFile(context, "/ocacore/import/common/cronjobs.impex");

		importCoreReleaseData(context);
	}

	/**
	 * Generates the Dropdown and Multi-select boxes for the project data import
	 */
	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<>();

		params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", true));

		return params;
	}

	/**
	 * This method will be called during the system initialization.
	 *
	 * @param context
	 *           the context provides the selected parameters and values
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		final boolean importAccessRights = getBooleanSystemSetupParameter(context, IMPORT_ACCESS_RIGHTS);

		final List<String> extensionNames = getExtensionNames();

		processCockpit(context, importAccessRights, extensionNames, "cmsbackoffice",
				"/ocacore/import/cockpits/cmscockpit/cmscockpit-users.impex",
				"/ocacore/import/cockpits/cmscockpit/cmscockpit-access-rights.impex");

		processCockpit(context, importAccessRights, extensionNames, "productcockpit",
				"/ocacore/import/cockpits/productcockpit/productcockpit-users.impex",
				"/ocacore/import/cockpits/productcockpit/productcockpit-access-rights.impex",
				"/ocacore/import/cockpits/productcockpit/productcockpit-constraints.impex");

		processCockpit(context, importAccessRights, extensionNames, "customersupportbackoffice",
				"/ocacore/import/cockpits/cscockpit/cscockpit-users.impex",
				"/ocacore/import/cockpits/cscockpit/cscockpit-access-rights.impex");
	}

	protected void processCockpit(final SystemSetupContext context, final boolean importAccessRights,
			final List<String> extensionNames, final String cockpit, final String... files)
	{
		if (importAccessRights && extensionNames.contains(cockpit))
		{
			for (final String file : files)
			{
				importImpexFile(context, file);
			}
		}
	}

	protected List<String> getExtensionNames()
	{
		return Registry.getCurrentTenant().getTenantSpecificExtensionNames();
	}

	private void importCoreReleaseData(final SystemSetupContext context) {
		String releaseFolderName = configurationService.getConfiguration().getString(OcaCoreConstants.RELEASE_FOLDER_NAME,"");
		if(StringUtils.isNotEmpty(releaseFolderName)) {
			List<String> fileNames = findFileNamesFromFolder(releaseFolderName);
			if (CollectionUtils.isNotEmpty(fileNames)) {
				for (String fileName:fileNames) {
					importImpexFile(context, releaseFolderName +"/"+fileName);
				}
			}
		}
	}
	private List<String> findFileNamesFromFolder(final String folder) {
		try (final InputStream resourceAsStream = getClass().getResourceAsStream(folder)) {
			if (resourceAsStream != null) {
				return IOUtils.readLines(resourceAsStream, Charsets.UTF_8);
			} else {
				return Collections.emptyList();
			}
		} catch (IOException e) {
			LOG.error("Error during reading of impex file names ::", e);
			throw new IllegalStateException(e);
		}
	}
}
