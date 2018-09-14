
package exasol


import akka.http.scaladsl.model.ws._

import javax.crypto.Cipher
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.spec.RSAPublicKeySpec
import java.security.Key
import java.security.KeyFactory
import java.security.PublicKey
import java.security.Security
import java.util.Base64

import org.bouncycastle.util.io.pem.PemReader
import org.json4s.DefaultFormats
import org.json4s.Formats
import org.json4s.FieldSerializer
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization

import sys.process._

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
	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	val keyFactory = KeyFactory.getInstance("RSA");
	val commandName = "login"

	case class UserData(
		username: String,
		password: String,
		useCompression: Boolean,
		sessionId: Option[Long],
		clientName: String = "Scala EXASOL Websocket",
		driverName: String = "Akka Http",
		clientRuntime: String = "Scala " + util.Properties.versionString,
		clientVersion: String = "0.0.1"
	) {
		def encrypt(pemKey: String, modulus: String, exponent: String): UserData = {
			val encryptedPassword: String = Seq("exasol/encrypter", pemKey, password).!!
			UserData(username, encryptedPassword, useCompression, sessionId)
		}
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
			case "ok" => // Nothing to throw
		}
	}
}

case class ResponseOrError[R](status: String, responseData: Option[R], exception: Option[Error]) {
}
