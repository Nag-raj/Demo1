package com.olympus.oca.core.services.impl;

import com.olympus.oca.core.services.EnvironmentDetails;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Component(immediate = true, service = EnvironmentDetails.class, configurationPolicy = ConfigurationPolicy.OPTIONAL)
@Designate(ocd = EnvironmentDetailsImpl.Configuration.class)
public class EnvironmentDetailsImpl implements EnvironmentDetails {

    /**
     * Activate.
     */
    @Activate
    private Configuration config;

    @Override
    public String getEnvironmentRunMode() {
        return config.environmentRunMode();
    }

    @Override
    public String getEnvironmentType() {
        return config.environmentType();
    }

    /**
     * The Interface Configuration.
     */
    @ObjectClassDefinition(name = "Environment Type Configurations")
    public @interface Configuration {

        @AttributeDefinition(name = "Runmode", description = "Run mode of the execution environment", type = AttributeType.STRING)
        String environmentRunMode();

        @AttributeDefinition(name = "Environment Type", description = "Execution environment type", type = AttributeType.STRING)
        String environmentType();

    }


}
