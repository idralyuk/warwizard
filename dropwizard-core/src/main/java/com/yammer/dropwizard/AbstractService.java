package com.yammer.dropwizard;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.yammer.dropwizard.config.LoggingFactory;

import java.util.Collections;

public abstract class AbstractService extends GuiceServletContextListener {
    static {
        LoggingFactory.bootstrap();
    }

    @Override
    protected Injector getInjector() {
        return Guice.createInjector(Iterables.concat(Collections.singletonList(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                serve("/*").with(GuiceContainer.class);
            }
        }), createModules()));
    }

    protected abstract Iterable<Module> createModules();
}
