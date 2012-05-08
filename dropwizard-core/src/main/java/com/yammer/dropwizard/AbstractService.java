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
import com.yammer.dropwizard.jersey.DropwizardGuiceContainer;
import com.yammer.dropwizard.lifecycle.Lifecycle;

import javax.servlet.ServletContextEvent;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;

public abstract class AbstractService<T extends Configuration> extends GuiceServletContextListener {
    static {
        LoggingFactory.bootstrap();
    }

    protected abstract String getConfigurationLocation();

    @SuppressWarnings("unchecked")
    protected Class<T> getConfigurationClass() {Type t = getClass();
        while (t instanceof Class<?>) {
            t = ((Class<?>) t).getGenericSuperclass();
        }
        /* This is not guaranteed to work for all cases with convoluted piping
         * of type parameters: but it can at least resolve straight-forward
         * extension with single type parameter (as per [Issue-89]).
         * And when it fails to do that, will indicate with specific exception.
         */
        if (t instanceof ParameterizedType) {
            // should typically have one of type parameters (first one) that matches:
            for (Type param : ((ParameterizedType) t).getActualTypeArguments()) {
                if (param instanceof Class<?>) {
                    Class<?> cls = (Class<?>) param;
                    if (Configuration.class.isAssignableFrom(cls)) {
                        return (Class<T>) cls;
                    }
                }
            }
        }
        throw new IllegalStateException("Can not figure out Configuration type parameterization for "+getClass().getName());
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
                bind(GuiceContainer.class).to(DropwizardGuiceContainer.class);
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
