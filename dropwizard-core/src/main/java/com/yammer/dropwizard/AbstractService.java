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
import com.yammer.dropwizard.lifecycle.Lifecycle;

import javax.servlet.ServletContextEvent;
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
                bind(Lifecycle.class).toInstance(lifecycle);
                bind(getConfigurationClass()).toInstance(conf);
                serve("/*").with(GuiceContainer.class);
            }
        }), createModules(conf)));
    }

    protected abstract Iterable<Module> createModules(T configuration);

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        lifecycle.stop();
        super.contextDestroyed(servletContextEvent);
    }

    private final Lifecycle lifecycle = new Lifecycle();
}
