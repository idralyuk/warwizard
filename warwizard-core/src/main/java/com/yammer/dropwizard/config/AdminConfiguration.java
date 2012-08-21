package com.yammer.dropwizard.config;

import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 */
public class AdminConfiguration {

    @JsonProperty
    @NotNull
    protected String username;

    @JsonProperty
    @NotNull
    protected String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
