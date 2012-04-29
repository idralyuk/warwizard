package com.yammer.dropwizard.db;

import com.google.inject.AbstractModule;
import com.yammer.dropwizard.jersey.LoggingDBIExceptionMapper;
import com.yammer.dropwizard.jersey.LoggingSQLExceptionMapper;

/**
 * A module for logging SQLExceptions and DBIExceptions so that their actual causes aren't overlooked.
 */
public class DBIExceptionsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LoggingDBIExceptionMapper.class);
        bind(LoggingSQLExceptionMapper.class);
    }
}
