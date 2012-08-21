package com.yammer.dropwizard.config;

/**
 * An exception thrown where there is an error parsing a configuration object.
 */
public class ConfigurationException extends Exception {
    private static final long serialVersionUID = 5325162099634227047L;

    /**
     * Creates a new {@link ConfigurationException} for the given file with the given errors.
     *
     * @param location  the location of the bad configuration file
     * @param errors    the errors in the file
     */
    public ConfigurationException(String location, Iterable<String> errors) {
        super(formatMessage(location, errors));
    }

    private static String formatMessage(String location, Iterable<String> errors) {
        final StringBuilder msg = new StringBuilder(location)
                .append(" has the following errors:\n");
        for (String error : errors) {
            msg.append("  * ").append(error).append('\n');
        }
        return msg.toString();
    }
}
