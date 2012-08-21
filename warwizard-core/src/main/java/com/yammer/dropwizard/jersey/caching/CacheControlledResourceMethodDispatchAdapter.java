package com.yammer.dropwizard.jersey.caching;

import com.google.inject.Singleton;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;

import javax.ws.rs.ext.Provider;

@Provider
@Singleton
public class CacheControlledResourceMethodDispatchAdapter implements ResourceMethodDispatchAdapter {
    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider) {
        return new CacheControlledResourceMethodDispatchProvider(provider);
    }
}
