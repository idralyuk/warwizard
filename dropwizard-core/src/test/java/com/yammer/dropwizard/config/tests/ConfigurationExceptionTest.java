package com.yammer.dropwizard.config.tests;

import com.google.common.collect.ImmutableList;
import com.yammer.dropwizard.config.ConfigurationException;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

public class ConfigurationExceptionTest {
    @Test
    public void formatsTheViolationsIntoAHumanReadableMessage() throws Exception {
        final File file = new File("config.yml");
        final ConfigurationException e = new ConfigurationException(file.getAbsolutePath(), ImmutableList.of("woo may not be null"));

        assertThat(e.getMessage(),
                   endsWith("config.yml has the following errors:\n" +
                           "  * woo may not be null\n"));
    }
}
