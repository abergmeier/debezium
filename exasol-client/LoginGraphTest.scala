
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
    val userData = LoginCommand.UserData("sys", "exasol", true, None, Some(classOf[LoginGraphSpec].toString()))
    val http = Http(system)

    "A LoginGraph" should "handle announce" in {

        val resultSink = Sink.head[LoginCommand.Response]

        val a = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder => sink =>

            val loginData = Future {
                userData
            }(ExecutionContext.global)
            val announce = Login.announceGraph(http, "ws://192.168.56.2:8563")

            import GraphDSL.Implicits._
            announce ~> sink.in
            ClosedShape
        })

        val cryptData = a.run()
        Await.result(cryptData, 1000.millis) should not be (null)
    }

    "A LoginGraph" should "be runnable" in {

        val resultSink = TestSink.probe[LoginCommand.SessionData]

        val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder => sink =>

            val loginData = Future {
                userData
            }(ExecutionContext.global)
            val login = Login.graph(http, loginData, "ws://192.168.56.2:8563")

            import GraphDSL.Implicits._
            login ~> sink.in
            ClosedShape
        })

        val session = g.run()
        session.requestNext(LoginCommand.SessionData(0, 1, "2", "db", "w", 59, 546, 45, "q", "TZ", "TZB")).expectComplete()
    }
}
