package com.example.helloworld;

import com.example.helloworld.core.Template;
import com.example.helloworld.db.HelloWorldDatabase;
import com.example.helloworld.db.PeopleDAO;
import com.example.helloworld.resources.HelloWorldResource;
import com.example.helloworld.resources.PeopleResource;
import com.example.helloworld.resources.PersonResource;
import com.example.helloworld.resources.ProtectedResource;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.yammer.dropwizard.AbstractService;
import com.yammer.dropwizard.db.Database;
import com.yammer.dropwizard.db.OnDemandDAOProvider;
import com.yammer.dropwizard.jersey.JacksonMessageBodyProvider;
import com.yammer.dropwizard.json.Json;

import java.util.List;

public class HelloWorldService extends AbstractService<HelloWorldConfiguration> {
    @Override
    protected String getConfigurationLocation() {
        return "/hello-world.yaml";
    }

    @Override
    protected Iterable<Module> createModules(final HelloWorldConfiguration configuration) {
        List<Module> modules = Lists.newArrayList();
        modules.add(new AbstractModule(){
            @Override protected void configure() {
                bind(Template.class).toProvider(configuration);

                        bind(Database.class).to(HelloWorldDatabase.class);
                bind(PeopleDAO.class).toProvider(OnDemandDAOProvider.newProvider(PeopleDAO.class));

                bind(HelloWorldResource.class);
                bind(ProtectedResource.class);

                bind(PeopleResource.class);
                bind(PersonResource.class);
                bind(JacksonMessageBodyProvider.class).toInstance(new JacksonMessageBodyProvider(new Json()));
            }
        });
        return modules;
    }
}