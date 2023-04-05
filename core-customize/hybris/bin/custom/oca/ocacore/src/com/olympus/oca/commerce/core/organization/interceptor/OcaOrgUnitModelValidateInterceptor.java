package com.olympus.oca.commerce.core.organization.interceptor;

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Required;

import java.util.Set;
import java.util.stream.Collectors;

public class OcaOrgUnitModelValidateInterceptor implements ValidateInterceptor {

    private static final String ERROR_ORGUNIT_ENABLE_ORGUNITPARENT_DISABLED = "error.orgunit.enable.orgunitparent.disabled";

    private L10NService l10NService;

    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {
        if (model instanceof OrgUnitModel)
        {
            final OrgUnitModel unit = (OrgUnitModel) model;

            // Restrain orgUnit from being in more than one orgUnit group (e.g 1 parent).
            if (unit.getGroups() != null)
            {
                // Filter groups by OrgUnit type
                final Set<OrgUnitModel> groups = unit.getGroups().stream().filter(grp -> grp.getItemtype().equals(unit.getItemtype()))
                        .filter(grp -> grp instanceof OrgUnitModel).map(grp -> (OrgUnitModel) grp).collect(Collectors.toSet());

                final OrgUnitModel parentUnit = groups.stream().findFirst().orElse(null);

                // Do not allow to activate units whose parents have be disabled.
                if (unit.getActive().booleanValue() && parentUnit != null && !parentUnit.getActive().booleanValue())
                {
                    throw new InterceptorException(
                            getL10NService().getLocalizedString(ERROR_ORGUNIT_ENABLE_ORGUNITPARENT_DISABLED, new Object[]
                                    { unit.getClass().getSimpleName(), unit.getUid(), parentUnit.getUid() }));
                }
            }
        }
    }

    @Required
    public void setL10NService(final L10NService l10NService)
    {
        this.l10NService = l10NService;
    }

    protected L10NService getL10NService()
    {
        return l10NService;
    }
}
