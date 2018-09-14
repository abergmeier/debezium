
package exasol

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.stream.ClosedShape
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.Supervision
import akka.stream.testkit.scaladsl.TestSink

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions

import java.io.File

import org.scalatest._
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure

class LoginGraphSpec extends FlatSpec with Matchers {

    implicit val system = ActorSystem("TestSystem")
    val terminateDecider: Supervision.Decider = {
        case e: Exception =>
            fail("Deciding to terminate due to: " + e)
            Supervision.Stop
        case _ => Supervision.Stop
    }
    val materializerSettings = ActorMaterializerSettings(system).withSupervisionStrategy(terminateDecider)
    implicit val materializer = ActorMaterializer(materializerSettings)
    val http = Http(system)

    val configOptions = ConfigParseOptions.defaults()
        .setAllowMissing(false)
    val configFile = new File("exasol-client/exasol.conf")
    val exasolConfig = ConfigFactory.parseFile(configFile, configOptions)
    val exasolServer = exasolConfig.getString("exasol.server")
    val userData = LoginCommand.UserData(
        exasolConfig.getString("exasol.username"),
        exasolConfig.getString("exasol.password"),
        true, None)

    "A LoginGraph" should "handle announce" in {

        val resultSink = Sink.head[LoginCommand.Response]

        val a = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder => sink =>

            val announce = Login.announceGraph(http, exasolServer)

            import GraphDSL.Implicits._
            announce ~> sink.in
            ClosedShape
        })

        val cryptData = a.run()
        Await.result(cryptData, 10000.millis) should not be (null)
    }

    "A LoginGraph" should "be runnable" in {

        val resultSink = TestSink.probe[LoginCommand.SessionData]

        val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder => sink =>

            val loginData = Future {
                userData
            }(ExecutionContext.global)
            val login = Login.graph(http, loginData, exasolServer)

            import GraphDSL.Implicits._
            login ~> sink.in
            ClosedShape
        })

        val session = g.run()
        session.requestNext(LoginCommand.SessionData(0, 1, "2", "db", "w", 59, 546, 45, "q", "TZ", "TZB")).expectComplete()
    }
}
