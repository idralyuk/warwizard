package com.yammer.dropwizard.lifecycle;

import com.google.common.collect.Sets;
import com.yammer.dropwizard.logging.Log;

import java.util.Set;

public class Lifecycle {
    private static final Log LOG = Log.forClass(Lifecycle.class);

    public void addManaged(Managed m) {
        managed.add(m);
    }

    private final Set<Managed> managed = Sets.newHashSet();

    public void stop() {
        for (Managed item : managed) {
            try {
                item.stop();
            } catch (Exception e) {
                LOG.warn(e, "Exception thrown while stopping managed item");
            }
        }
    }
}
