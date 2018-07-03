
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
import io.debezium.connector.teradata.SecureConnectionMode

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
        .withDescription("Port of the Teradata database server.")

    val USER = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.USER)
        .withDisplayName("User")
        .withType(Type.STRING)
        .withWidth(Width.SHORT)
        .withImportance(Importance.HIGH)
        .withValidation(toSam(Field.isRequired _))
        .withDescription("Name of the Teradata database user to be used when connecting to the database.")

    val PASSWORD = Field.create(DATABASE_CONFIG_PREFIX + JdbcConfiguration.PASSWORD)
        .withDisplayName("Password")
        .withType(Type.PASSWORD)
        .withWidth(Width.SHORT)
        .withImportance(Importance.HIGH)
        .withDescription("Password of the Teradata database user to be used when connecting to the database.")


    val SERVER_NAME = Field.create(DATABASE_CONFIG_PREFIX + "server.name")
        .withDisplayName("Namespace")
        .withType(Type.STRING)
        .withWidth(Width.MEDIUM)
        .withImportance(Importance.HIGH)
        .withDescription("Unique name that identifies the database server and all recorded offsets, and"
             + "that is used as a prefix for all schemas and topics. "
             + "Each distinct Teradata installation should have a separate namespace and monitored by "
             + "at most one Debezium connector. Defaults to 'host:port/database'");

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
        .withValidation(toSam(TeradataConnectorConfig.validateSchemaBlacklist _))
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
        SERVER_NAME, SSL_MODE, SSL_CLIENT_CERT, SSL_CLIENT_KEY_PASSWORD, SSL_ROOT_CERT,
        SSL_CLIENT_KEY, SSL_SOCKET_FACTORY, STATUS_UPDATE_INTERVAL_MS)


    def configDef(): ConfigDef = {
        val config = new ConfigDef();
        Field.group(config, "Teradata", SERVER_NAME, DATABASE_NAME, HOSTNAME, PORT, USER,
                    PASSWORD, SSL_MODE, SSL_CLIENT_CERT, SSL_CLIENT_KEY_PASSWORD,
                    SSL_ROOT_CERT, SSL_CLIENT_KEY, SSL_SOCKET_FACTORY, 
                    STATUS_UPDATE_INTERVAL_MS);
        Field.group(config, "Events", SCHEMA_WHITELIST, SCHEMA_BLACKLIST);
        Field.group(config, "Connector", CommonConnectorConfig.POLL_INTERVAL_MS)
        config;
    }

    def validateSchemaBlacklist(config: Configuration, field: Field, problems: Field.ValidationOutput): Int = {
        val whitelist = config.getString(SCHEMA_WHITELIST);
        val blacklist = config.getString(SCHEMA_BLACKLIST);
        if (whitelist != null && blacklist != null) {
            problems.accept(SCHEMA_BLACKLIST, blacklist, "Schema whitelist is already specified");
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
class TeradataConnectorConfig(config: Configuration) extends CommonConnectorConfig(config, TeradataConnectorConfig.SERVER_NAME) {

    var _serverName = config.getString(TeradataConnectorConfig.SERVER_NAME)
    if (_serverName == None) {
        _serverName = hostname() + ":" + port() + "/" + databaseName()
    }

    def hostname(): String = {
        config.getString(TeradataConnectorConfig.HOSTNAME)
    }

    def port(): Int = {
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
        _serverName
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
