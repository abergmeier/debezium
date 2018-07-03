
package teradata

import java.math.BigDecimal
import java.util.Map
import java.util.concurrent.TimeUnit

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance
import org.apache.kafka.common.config.ConfigDef.Type
import org.apache.kafka.common.config.ConfigDef.Width
import org.apache.kafka.common.config.ConfigValue

import io.debezium.config.CommonConnectorConfig
import io.debezium.config.Configuration
import io.debezium.config.EnumeratedValue
import io.debezium.config.Field
import io.debezium.jdbc.JdbcConfiguration
import io.debezium.jdbc.JdbcValueConverters.DecimalMode
import io.debezium.jdbc.TemporalPrecisionMode

/**
 * The set of predefined SecureConnectionMode options or aliases.
 */
object SecureConnectionMode {

    /**
     * Establish an unencrypted connection
     *
     * see the {@code sslmode} Postgres JDBC driver option
     */
    val DISABLED = new SecureConnectionMode("disable")

        /**
         * Establish a secure connection if the server supports secure connections.
         * The connection attempt fails if a secure connection cannot be established
         *
         * see the {@code sslmode} Postgres JDBC driver option
         */
    val REQUIRED = new SecureConnectionMode("require")

        /**
         * Like REQUIRED, but additionally verify the server TLS certificate against the configured Certificate Authority
         * (CA) certificates. The connection attempt fails if no valid matching CA certificates are found.
         *
         * see the {@code sslmode} Postgres JDBC driver option
         */
    val VERIFY_CA = new SecureConnectionMode("verify-ca")

        /**
         * Like VERIFY_CA, but additionally verify that the server certificate matches the host to which the connection is
         * attempted.
         *
         * see the {@code sslmode} Postgres JDBC driver option
         */
    val VERIFY_FULL = new SecureConnectionMode("verify-full")

    def values(): Array[SecureConnectionMode] = {
        return Array(DISABLED, REQUIRED, VERIFY_CA, VERIFY_FULL)
    }

    /**
     * Determine if the supplied value is one of the predefined options.
     *
     * @param value the configuration property value; may not be null
     * @return the matching option, or null if no match is found
     */
    def parse(value: String): Option[SecureConnectionMode] = {
        if (value == None)
            return Some(REQUIRED)
        val trimmedValue = value.trim()
        for (option <- values()) {
            if (option.getValue().equalsIgnoreCase(trimmedValue))
                return Some(option)
        }
        None
    }

    /**
     * Determine if the supplied value is one of the predefined options.
     *
     * @param value the configuration property value; may not be null
     * @param defaultValue the default value; may be null
     * @return the matching option, or null if no match is found and the non-null default is invalid
     */
    def parse(value: String, defaultValue: String): Option[SecureConnectionMode] = {
        var mode = parse(value)
        if (mode == None && defaultValue != None)
            mode = parse(defaultValue)
        mode
    }
}

class SecureConnectionMode(value: String) extends EnumeratedValue {

    override def getValue(): String = {
        value
    }
}

object TeradataConnectorConfig {
    val DATABASE_CONFIG_PREFIX = "database."
    val DEFAULT_PORT = 5432

    val TABLE_WHITELIST_NAME = "table.whitelist"

    def toSam(h: (Configuration, Field, Field.ValidationOutput) => Int): Field.Validator =
        new Field.Validator {
            override def validate(config: Configuration, field: Field, problems: Field.ValidationOutput): Int =
                h(config, field, problems)
        }

    val HOSTNAME = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.HOSTNAME)
        .withDisplayName("Hostname")
        .withType(Type.STRING)
        .withWidth(Width.MEDIUM)
        .withImportance(Importance.HIGH)
        .withValidation(toSam(Field.isRequired _))
        .withDescription("Resolvable hostname or IP address of the Teradata database server.")

    val PORT = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.PORT)
        .withDisplayName("Port")
        .withType(Type.INT)
        .withWidth(Width.SHORT)
        .withDefault(DEFAULT_PORT)
        .withImportance(Importance.HIGH)
        .withValidation(toSam(Field.isInteger _))
        .withDescription("Port of the Postgres database server.")

    val USER = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.USER)
        .withDisplayName("User")
        .withType(Type.STRING)
        .withWidth(Width.SHORT)
        .withImportance(Importance.HIGH)
        .withValidation(toSam(Field.isRequired _))
        .withDescription("Name of the Postgres database user to be used when connecting to the database.")

    val PASSWORD = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.PASSWORD)
        .withDisplayName("Password")
        .withType(Type.PASSWORD)
        .withWidth(Width.SHORT)
        .withImportance(Importance.HIGH)
        .withDescription("Password of the Postgres database user to be used when connecting to the database.")

    val DATABASE_NAME = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.DATABASE)
        .withDisplayName("Database")
        .withType(Type.STRING)
        .withWidth(Width.MEDIUM)
        .withImportance(Importance.HIGH)
        .withValidation(toSam(Field.isRequired _))
        .withDescription("The name of the database the connector should be monitoring")


    val SSL_MODE = Field.create(DATABASE_CONFIG_PREFIX + "sslmode")
        .withDisplayName("SSL mode")
        .withEnum(classOf[SecureConnectionMode], SecureConnectionMode.DISABLED)
        .withWidth(Width.MEDIUM)
        .withImportance(Importance.MEDIUM)
        .withDescription("Whether to use an encrypted connection to Postgres. Options include"
              + "'disable' (the default) to use an unencrypted connection; "
              + "'require' to use a secure (encrypted) connection, and fail if one cannot be established; "
              + "'verify-ca' like 'required' but additionally verify the server TLS certificate against the configured Certificate Authority "
              + "(CA) certificates, or fail if no valid matching CA certificates are found; or"
              + "'verify-full' like 'verify-ca' but additionally verify that the server certificate matches the host to which the connection is attempted.")

    val SSL_CLIENT_CERT = Field.create(DATABASE_CONFIG_PREFIX + "sslcert")
         .withDisplayName("SSL Client Certificate")
         .withType(Type.STRING)
         .withWidth(Width.LONG)
         .withImportance(Importance.MEDIUM)
         .withDescription("File containing the SSL Certificate for the client. See the Postgres SSL docs for further information")

    val SSL_CLIENT_KEY = Field.create(DATABASE_CONFIG_PREFIX + "sslkey")
        .withDisplayName("SSL Client Key")
        .withType(Type.STRING)
        .withWidth(Width.LONG)
        .withImportance(Importance.MEDIUM)
        .withDescription("File containing the SSL private key for the client. See the Postgres SSL docs for further information")

    val SSL_CLIENT_KEY_PASSWORD = Field.create(DATABASE_CONFIG_PREFIX + "sslpassword")
        .withDisplayName("SSL Client Key Password")
        .withType(Type.PASSWORD)
        .withWidth(Width.MEDIUM)
        .withImportance(Importance.MEDIUM)
        .withDescription("Password to access the client private key from the file specified by 'database.sslkey'. See the Postgres SSL docs for further information");

    val SSL_ROOT_CERT = Field.create(DATABASE_CONFIG_PREFIX + "sslrootcert")
        .withDisplayName("SSL Root Certificate")
        .withType(Type.STRING)
        .withWidth(Width.LONG)
        .withImportance(Importance.MEDIUM)
        .withDescription("File containing the root certificate(s) against which the server is validated. See the Postgres JDBC SSL docs for further information");

    val SSL_SOCKET_FACTORY = Field.create(DATABASE_CONFIG_PREFIX + "sslfactory")
        .withDisplayName("SSL Root Certificate")
        .withType(Type.STRING)
        .withWidth(Width.LONG)
        .withImportance(Importance.MEDIUM)
        .withDescription("A name of class to that creates SSL Sockets. Use org.postgresql.ssl.NonValidatingFactory to disable SSL validation in development environments");

    /**
     * A comma-separated list of regular expressions that match schema names to be monitored.
     * May not be used with {@link #SCHEMA_BLACKLIST}.
     */
    val SCHEMA_WHITELIST = Field.create("schema.whitelist")
        .withDisplayName("Schemas")
        .withType(Type.LIST)
        .withWidth(Width.LONG)
        .withImportance(Importance.HIGH)
        .withDependents(TABLE_WHITELIST_NAME)
        .withDescription("The schemas for which events should be captured")

    /**
     * A comma-separated list of regular expressions that match schema names to be excluded from monitoring.
     * May not be used with {@link #SCHEMA_WHITELIST}.
     */
    val SCHEMA_BLACKLIST = Field.create("schema.blacklist")
        .withDisplayName("Exclude Schemas")
        .withType(Type.STRING)
        .withWidth(Width.LONG)
        .withImportance(Importance.MEDIUM)
        .withValidation(toSam(PostgresConnectorConfig.validateSchemaBlacklist _))
        .withInvisibleRecommender()
        .withDescription("")

    val STATUS_UPDATE_INTERVAL_MS = Field.create("status.update.interval.ms")
        .withDisplayName("Status update interval (ms)")
        .withType(Type.INT) // Postgres doesn't accept long for this value
        .withWidth(Width.SHORT)
        .withImportance(Importance.MEDIUM)
        .withDescription("Frequency in milliseconds for sending replication connection status updates to the server. Defaults to 10 seconds (10000 ms).")
        .withValidation(toSam(Field.isPositiveInteger _))

    /**
     * The set of {@link Field}s defined as part of this configuration.
     */
    val ALL_FIELDS = Field.setOf(DATABASE_NAME, USER, PASSWORD, HOSTNAME, PORT,
        CommonConnectorConfig.POLL_INTERVAL_MS, SCHEMA_WHITELIST, SCHEMA_BLACKLIST,
        SSL_MODE, SSL_CLIENT_CERT, SSL_CLIENT_KEY_PASSWORD, SSL_ROOT_CERT,
        SSL_CLIENT_KEY, SSL_SOCKET_FACTORY, STATUS_UPDATE_INTERVAL_MS)


    def configDef(): ConfigDef = {
        val config = new ConfigDef();
        Field.group(config, "Teradata", DATABASE_NAME, HOSTNAME, PORT, USER,
                    PASSWORD, SSL_MODE, SSL_CLIENT_CERT, SSL_CLIENT_KEY_PASSWORD,
                    SSL_ROOT_CERT, SSL_CLIENT_KEY, SSL_SOCKET_FACTORY, 
                    STATUS_UPDATE_INTERVAL_MS);
        Field.group(config, "Events", SCHEMA_WHITELIST, SCHEMA_BLACKLIST);
        Field.group(config, "Connector", CommonConnectorConfig.POLL_INTERVAL_MS)
        config;
    }

    def validateSchemaBlacklist(config: Configuration, field: Field, problems: Field.ValidationOutput): int = {
        val whitelist = config.getString(SCHEMA_WHITELIST);
        val blacklist = config.getString(SCHEMA_BLACKLIST);
        if (whitelist != null && blacklist != null) {
            problems.accept(SCHEMA_BLACKLIST, blacklist, "Schema whitelist is already specified");
            1
        }
        else
            0
    }

    def validateTableBlacklist(config: Configuration, field: Field, problems: Field.ValidationOutput): int = {
        val whitelist = config.getString(TABLE_WHITELIST);
        val blacklist = config.getString(TABLE_BLACKLIST);
        if (whitelist != null && blacklist != null) {
            problems.accept(TABLE_BLACKLIST, blacklist, "Table whitelist is already specified");
            1
        }
        else
            0
    }
}

/**
 * The configuration properties for the {@link TeradataConnectorConfig}
 *
 * @author Andreas Bergmeier
 */
class TeradataConnectorConfig(config: Configuration) extends CommonConnectorConfig {

    def this(config: Configuration) = {
        this(config, TeradataConnectorConfig.SERVER_NAME)
    }

    val serverName = config.getString(TeradataConnectorConfig.SERVER_NAME)
    if (serverName == None) {
        serverName = hostname() + ":" + port() + "/" + databaseName()
    }

    def hostname(): String = {
        config.getString(TeradataConnectorConfig.HOSTNAME)
    }

    def port(): int = {
        config.getInteger(TeradataConnectorConfig.PORT)
    }

    def databaseName(): String = {
        config.getString(TeradataConnectorConfig.DATABASE_NAME)
    }

    def statusUpdateIntervalMillis(): Integer = {
        config.getInteger(TeradataConnectorConfig.STATUS_UPDATE_INTERVAL_MS, null)
    }

    def jdbcConfig(): Configuration = {
        config.subset(TeradataConnectorConfig.DATABASE_CONFIG_PREFIX, true)
    }

    def serverName(): String = {
        serverName
    }

    def validate(): Map[String, ConfigValue] = {
        config.validate(TeradataConnectorConfig.ALL_FIELDS)
    }

    def schemaBlacklist(): String = {
        config.getString(TeradataConnectorConfig.SCHEMA_BLACKLIST);
    }

    def schemaWhitelist(): String = {
        config.getString(TeradataConnectorConfig.SCHEMA_WHITELIST);
    }
}
