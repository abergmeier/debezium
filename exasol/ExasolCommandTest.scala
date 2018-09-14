package exasol

import org.json4s.jackson.Serialization
import org.json4s.DefaultFormats

import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure

class ExasolCommandSpec extends FlatSpec with Matchers {

	"UserData" should "serialize to JSON" in {
		val userData = LoginCommand.UserData(
			"username",
			"password",
			false,
			Some(4),
			Some("client")
		)
		val formats = DefaultFormats
		val json = Serialization.write(userData)(formats)
		json shouldBe raw"""{"username":"username","password":"password","useCompression":false,"sessionId":4,"clientName":"client"}"""
	}

	"LoginCommand" should "serialize" in {
		val json = Command.toJson(new LoginCommand)
		json shouldBe raw"""{"protocolVersion":1,"command":"login"}"""
	}
/*
	"UserData" should "have same values after serialization cycle" in {
		val former = LoginCommand.UserData(
			"username",
			"password",
			false,
			Some(4),
			Some("client")
		)

		val jsonString = Serialization.write(userData, DefaultFormats)
		val json = JsonMethods.parse(jsonString)
		val latter = json.extract[LoginCommand.UserData]
		former shouldBe equal latter
	}
*/
}
