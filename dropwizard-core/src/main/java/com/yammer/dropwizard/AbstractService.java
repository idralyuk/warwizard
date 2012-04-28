package com.yammer.dropwizard;

import com.google.common.collect.Iterables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.LoggingFactory;

import java.lang.reflect.ParameterizedType;
import java.util.Collections;

public abstract class AbstractService<T extends Configuration> extends GuiceServletContextListener {
    static {
        LoggingFactory.bootstrap();
    }

    protected abstract String getConfigurationLocation();

    @SuppressWarnings("unchecked")
    protected Class<T> getConfigurationClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    protected Injector getInjector() {
        final T conf;
        try {
            conf = ConfigurationFactory.forClass(getConfigurationClass()).build(getConfigurationLocation());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        new LoggingFactory(conf.getLoggingConfiguration(), getClass().getSimpleName()).configure();
        return Guice.createInjector(Iterables.concat(Collections.singletonList(new JerseyServletModule() {
            @Override
            protected void configureServlets() {
                bind(getConfigurationClass()).toInstance(conf);
                serve("/*").with(GuiceContainer.class);
            }
        }), createModules(conf)));
    }

    protected abstract Iterable<Module> createModules(T configuration);
}
