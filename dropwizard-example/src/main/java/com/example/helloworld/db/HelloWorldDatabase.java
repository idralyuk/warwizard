package com.example.helloworld.db;

import com.example.helloworld.HelloWorldConfiguration;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.yammer.dropwizard.db.Database;
import com.yammer.dropwizard.lifecycle.Lifecycle;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionCallback;
import org.skife.jdbi.v2.TransactionStatus;

@Singleton
public class HelloWorldDatabase extends Database {
    @Inject
    public HelloWorldDatabase(HelloWorldConfiguration configuration, Lifecycle lifecycle) throws ClassNotFoundException {
        super(configuration.getDatabaseConfiguration(), lifecycle);
        inTransaction(new TransactionCallback<Void>(){
            @Override
            public Void inTransaction(Handle handle, TransactionStatus status) throws Exception {
                onDemand(PeopleDAO.class).createPeopleTable();
                return null;
            }
        });
    }
}
