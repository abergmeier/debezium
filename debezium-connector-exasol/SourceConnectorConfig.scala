
package exasol

import org.apache.kafka.common.config.ConfigDef

object ExasolSourceConnectorConfig {
	def configDef(): ConfigDef = {
		val defs = new ConfigDef()
		defs.define("config_with_default", ConfigDef.Type.STRING, "default string value", "Configuration with default value.");
		defs.define("config_with_validator", ConfigDef.Type.INT, 42, Range.atLeast(0), "Configuration with user provided validator.");
		defs.define("config_with_dependents", ConfigDef.Type.INT, "Configuration with dependents.", "group", 1, "Config With Dependents", Arrays.asList("config_with_default","config_with_validator"));
		defs
	}
}