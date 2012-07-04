package com.yammer.dropwizard.jersey;

import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class DropwizardResourceConfig extends DefaultResourceConfig {
    public DropwizardResourceConfig(boolean testOnly) {
        super();
        getFeatures().put(ResourceConfig.FEATURE_DISABLE_WADL, Boolean.TRUE);
    }
}
