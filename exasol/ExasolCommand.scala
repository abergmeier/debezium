
package exasol


import akka.http.scaladsl.model.ws._
import net.liftweb.json._


abstract class Command(val command: String, val jsonFormat: String) {
	def toJsonString(): String
}

object AbortQuery {
	val commandName = "abortQuery"
}

case class AbortQuery() extends Command(AbortQuery.commandName, "{\"command\": \"%s\"}") {
	override def toJsonString(): String =
		jsonFormat.format(command)
}

object ClosePreparedStatement {
	val commandName = "closePreparedStatement"
}

case class ClosePreparedStatement() extends Command(ClosePreparedStatement.commandName, "{\"command\": \"%s\"}") {
	override def toJsonString(): String =
		jsonFormat.format(command)
}

object GetHosts {
	val commandName = "getHosts"
}

case class GetHosts() extends Command(GetHosts.commandName, "{\"command\": \"%s\"}") {
	override def toJsonString(): String =
		jsonFormat.format(command)
}

object LoginCommand {
	val commandName = "login"

	@SerialVersionUID(223L)
	case class UserData(username: String, password: String, useCompression: Boolean, sessionId: Option[Long], clientName: Option[String]) extends Product with Serializable {
	}

	object SessionData {
		def extract[T](data: T): SessionData = data match {
			case tm: TextMessage.Strict => {
				implicit val formats = DefaultFormats
				val json = parse(tm.text)
				val answer = json.extract[ResponseOrError[SessionData]]
				ResponseOrError.throwFoundError(answer)
				answer.responseData.get
			}
		case _ => throw new RuntimeException("Extract of SessionData with wrong type")
		}
	}

	case class SessionData(
		sessionId: Long,
		protocolVersion: Int,
		releaseVersion: String,
		databaseName: String,
		productName: String,
		maxDataMessageSize: Int,
		maxIdentifierLength: Int,
		maxVarcharLength: Int,
		identifierQuoteString: String,
		timeZone: String,
		timeZoneBehavior: String)
	
	object Response {
		def extract[T](data: T): Response = data match {
			case tm: TextMessage.Strict => {
				implicit val formats = DefaultFormats
				val json = parse(tm.text)
				val answer = json.extract[ResponseOrError[Response]]
				ResponseOrError.throwFoundError(answer)
				answer.responseData.get
			}
			case _ => throw new RuntimeException("Extract of Response with wrong type")
		}
	}

	case class Response(publicKeyPem: String, publicKeyModulus: String, publicKeyExponent: String) {
	}
}

@SerialVersionUID(123L)
case class LoginCommand() extends Command(LoginCommand.commandName, "{\"command\": \"%s\", \"protocolVersion\": %s}") with Serializable {
	val protocolVersion = 1

	override def toJsonString(): String =
		jsonFormat.format(command, protocolVersion)
}

case class Error(text: String, sqlCode: String) extends Exception {
}

object ResponseOrError {
	def throwFoundError[T](answer: ResponseOrError[T]) = {
		answer.status match {
			case "error" => throw answer.exception.get
		}
	}
}

case class ResponseOrError[R](status: String, responseData: Option[R], exception: Option[Error]) {
}
