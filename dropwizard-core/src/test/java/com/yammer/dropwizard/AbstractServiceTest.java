package com.yammer.dropwizard;

import com.google.inject.Module;
import com.yammer.dropwizard.config.Configuration;
import org.junit.Assert;
import org.junit.Test;

public class AbstractServiceTest {
    public static class MyConfig extends Configuration { }

    static class DirectService extends AbstractService<MyConfig> {
        // needed since super-class method is protected
        public Class<?> getParameterization() { return super.getConfigurationClass(); }

        @Override
        protected String getConfigurationLocation() {
            return null;
        }

        @Override
        protected Iterable<Module> createModules(MyConfig configuration) {
            return null;
        }
    }

    static class UberService extends DirectService { }
    
    @Test
    public void canResolveParameterization() {
        // first, simple case with direct sub-class parameterization:
        Assert.assertEquals(new DirectService().getParameterization(), MyConfig.class);
        // then indirect one
        Assert.assertEquals(new UberService().getParameterization(), MyConfig.class);
    }

}
