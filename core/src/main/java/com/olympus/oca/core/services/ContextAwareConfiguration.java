package com.olympus.oca.core.services;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

@Configuration( label = "Context Aware Configuration", description = "Configuration Desc")

public @interface ContextAwareConfiguration {
    @Property(label="Text", description = "Text Description")
    String textField();
}
