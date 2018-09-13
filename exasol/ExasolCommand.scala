
package exasol


import akka.http.scaladsl.model.ws._
import org.json4s.{DefaultFormats, FieldSerializer, Formats}
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization

object Command {
	def toJson[C <: Command](c: C)(implicit mc: scala.reflect.Manifest[C]): String = {
		val formats = DefaultFormats + FieldSerializer[C]()
		Serialization.write(c)(formats)
	}
}

abstract class Command(val command: String) {
}

object AbortQuery {
	val commandName = "abortQuery"
}

case class AbortQuery() extends Command(AbortQuery.commandName) {
}

object ClosePreparedStatement {
	val commandName = "closePreparedStatement"
}

case class ClosePreparedStatement() extends Command(ClosePreparedStatement.commandName) {
}

object GetHosts {
	val commandName = "getHosts"
}

case class GetHosts() extends Command(GetHosts.commandName) {
}

object LoginCommand {
	val commandName = "login"

	case class UserData(
		username: String,
		password: String,
		useCompression: Boolean,
		sessionId: Option[Long],
		clientName: Option[String]
	) {
	}

	object SessionData {
		def extract[T](data: T): SessionData = data match {
			case TextMessage.Strict(text) => {
				implicit val formats = DefaultFormats.withStrictArrayExtraction
				val json = JsonMethods.parse(text)
				val answer = json.extract[ResponseOrError[SessionData]]
				ResponseOrError.throwFoundError(answer)
				answer.responseData.get
			}
		case _ => throw new IllegalArgumentException("Extract of SessionData with wrong type")
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
			case TextMessage.Strict(text) => {
				implicit val formats = DefaultFormats.withStrictArrayExtraction
				val json = JsonMethods.parse(text)
				val answer = json.extract[ResponseOrError[Response]]
				ResponseOrError.throwFoundError(answer)
				answer.responseData.get
			}
			case _ => throw new IllegalArgumentException("Extract of Response with wrong type")
		}
	}

	case class Response(publicKeyPem: String, publicKeyModulus: String, publicKeyExponent: String) {
	}
}

case class LoginCommand() extends Command(LoginCommand.commandName) {
	val protocolVersion = 1
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
