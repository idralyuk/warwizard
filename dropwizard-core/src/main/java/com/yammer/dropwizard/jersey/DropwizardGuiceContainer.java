package com.yammer.dropwizard.jersey;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.WebConfig;

import javax.servlet.ServletException;
import java.util.Map;

/**
 * Uses the dropwizard resource config instead of DefaultResourceConfig
 */
@Singleton
public class DropwizardGuiceContainer extends GuiceContainer {

    /**
     * Creates a new container.
     *
     * @param injector the Guice injector
     */
    @Inject
    public DropwizardGuiceContainer(Injector injector) {
        super(injector);
    }

    @Override
    protected ResourceConfig getDefaultResourceConfig(Map<String, Object> props,
                                                      WebConfig webConfig) throws ServletException {
        return new DropwizardResourceConfig(/*testOnly=*/false);
    }

}
