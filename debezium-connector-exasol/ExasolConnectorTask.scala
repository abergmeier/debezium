
package exasol

import akka.actor.{ Actor, ActorLogging, ActorRef, ActorSystem, Props }

import java.sql.DriverManager
import java.sql.Connection

import io.debezium.connector.common.BaseSourceTask

class ExasolConnectorTask extends BaseSourceTask {
	val system: ActorSystem = ActorSystem("exasolAkka")

	var connection: Connection = None
	val logger = LOGGER.getLogger()
	var reader = None

	def using[T <: { def close() }](resource: T)
		(block: T => Unit)
	{
		try {
			block(resource)
		} finally {
			if (resource == null)
				return
			resource.close()
		}
	}

	class Poller extends Actor {
		def receive = {

		}
	}

	override def start(config: Configuration) {

		logger.info("Starting Exasol connector task");

		s



		Class.forName("com.exasol.jdbc.EXADriver");

		connection = DriverManager.getConnection(
			"jdbc:exa:192.168.1.1..2:8563",
			"user",
			"password"
		);

		Reader()
			.snapshot()
			.poll()

		val chainedReaderBuilder = new ChainedReader.Builder();
		// TODO: Implement snapshot reader
		//chainedReaderBuilder.addReader(snapshotReader);
		readers = chainedReaderBuilder.build()
		readers.uponCompletion(completeReaders)
		readers.initialize()
		readers.start()
	}

	override def poll() List[SourceRecord] {
		readers.poll()
	}

	override def stop() {
		logger.info("Stopping Exasol connector task")

		if (readers == null)
			return

		readers.stop()
		readers.destroy()
	}

	def closeConnection() {
		connection.close()
	}

	def completeReaders() {
		closeConnection()
	}
}