package com.yammer.dropwizard.db;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class OnDemandDAOProvider<T> implements Provider<T> {
    private final Class<T> onDemandDAOClass;
    private T onDemandDAO;

    public static <T> OnDemandDAOProvider<T> newProvider(Class<T> daoType) {
        return new OnDemandDAOProvider<T>(daoType);
    }

    private OnDemandDAOProvider(Class<T> daoType) {
        onDemandDAOClass = daoType;
    }

    @Inject
    private void setDatabase(Database db) {
        onDemandDAO = db.onDemand(onDemandDAOClass);
    }

    @Override
    public T get() {
        return onDemandDAO;
    }
}
