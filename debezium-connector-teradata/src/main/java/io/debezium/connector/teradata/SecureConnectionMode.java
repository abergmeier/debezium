
package io.debezium.connector.teradata;

import io.debezium.config.EnumeratedValue;

/**
 * The set of predefined SecureConnectionMode options or aliases.
 */
public enum SecureConnectionMode implements EnumeratedValue {

    /**
     * Establish an unencrypted connection
     *
     * see the {@code sslmode} Postgres JDBC driver option
     */
    DISABLED("disable"),

    /**
     * Establish a secure connection if the server supports secure connections.
     * The connection attempt fails if a secure connection cannot be established
     *
     * see the {@code sslmode} Postgres JDBC driver option
     */
    REQUIRED("require"),

    /**
     * Like REQUIRED, but additionally verify the server TLS certificate against the configured Certificate Authority
     * (CA) certificates. The connection attempt fails if no valid matching CA certificates are found.
     *
     * see the {@code sslmode} Postgres JDBC driver option
     */
    VERIFY_CA("verify-ca"),

    /**
     * Like VERIFY_CA, but additionally verify that the server certificate matches the host to which the connection is
     * attempted.
     *
     * see the {@code sslmode} Postgres JDBC driver option
     */
    VERIFY_FULL("verify-full");

    private final String value;

    SecureConnectionMode(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    /**
     * Determine if the supplied value is one of the predefined options.
     *
     * @param value the configuration property value; may not be null
     * @return the matching option, or null if no match is found
     */
    public static SecureConnectionMode parse(String value) {
        if (value == null) return null;
        value = value.trim();
        for (SecureConnectionMode option : SecureConnectionMode.values()) {
            if (option.getValue().equalsIgnoreCase(value)) return option;
        }
        return null;
    }

    /**
     * Determine if the supplied value is one of the predefined options.
     *
     * @param value the configuration property value; may not be null
     * @param defaultValue the default value; may be null
     * @return the matching option, or null if no match is found and the non-null default is invalid
     */
    public static SecureConnectionMode parse(String value, String defaultValue) {
        SecureConnectionMode mode = parse(value);
        if (mode == null && defaultValue != null) mode = parse(defaultValue);
        return mode;
    }
}