package com.yammer.dropwizard.lifecycle;

/**
 * An interface for objects which need to be started and stopped as the service is started or
 * stopped.
 */
public interface Managed {
    /**
     * Stops the object. Called <i>after</i> the service is no longer accepting requests.
     *
     * @throws Exception if something goes wrong.
     */
    public void stop() throws Exception;
}
