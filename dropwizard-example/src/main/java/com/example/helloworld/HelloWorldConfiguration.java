package com.example.helloworld;

import com.example.helloworld.core.Template;
import com.google.inject.Provider;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("FieldMayBeFinal")
public class HelloWorldConfiguration extends Configuration implements Provider<Template> {

    @NotEmpty
    private String template;
    
    @NotEmpty
    private String defaultName = "Stranger";

    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();

    public String getTemplate() {
        return template;
    }

    public String getDefaultName() {
        return defaultName;
    }

    @Override
    public Template get() {
        return new Template(template, defaultName);
    }

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }
}
