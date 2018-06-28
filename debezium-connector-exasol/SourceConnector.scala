
package exasol

import java.util.List
import java.util.HashMap
import java.util.Map
import java.util.Collections
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.source.SourceConnector
import org.apache.kafka.connect.connector.Task

class ExasolSourceConnector(/*xc: Int, yc: Int*/) extends SourceConnector {
	var props: Option[java.util.Map[String,String]] = None
/*
  val x: Int = xc
  val y: Int = yc
  override def move(dx: Int, dy: Int): Point =
    new Point(x + dx, y + dy)
*/
	override def config(): ConfigDef = {
		ExasolSourceConnectorConfig.configDef()
	}

	override def start(props: java.util.Map[String,String]): Unit = {
		this.props = Some(props)
	}

	override def stop(): Unit = {
		this.props = None
	}

	override def taskClass(): Class[_ <: org.apache.kafka.connect.connector.Task] = {
		classOf[ExasolConnectorTask];
	}

	override def taskConfigs(workerCount: Int): java.util.List[java.util.Map[String,String]] = {
		if (props == None)
			Collections.emptyList()
		else
			Collections.singletonList(new HashMap[String, String](props.get));
	}

	override def version(): String = {
		"0.0.1"
	}
}
