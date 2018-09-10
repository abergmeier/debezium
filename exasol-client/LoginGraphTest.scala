
package exasol

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.ActorMaterializerSettings
import akka.stream.ClosedShape
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.Supervision

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
            system.log.info("About to fail")
            fail("Unhandled exception in stream" + e)
            Supervision.Stop
        case _ => Supervision.Stop
    }
    val materializerSettings = ActorMaterializerSettings(system).withSupervisionStrategy(terminateDecider)
    implicit val materializer = ActorMaterializer(materializerSettings)

    val resultSink = Sink.head[LoginCommand.SessionData]

    val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit builder => sink =>

        val loginData = Future {
            LoginCommand.UserData("dummyuser", "dummypass", true, None, Some(classOf[LoginGraphSpec].toString()))
        }(ExecutionContext.global)

        val login = Login.graph(system, loginData, "ws://heise.de")

        import GraphDSL.Implicits._
        login ~> sink.in
        ClosedShape
    })

    "A LoginGraph" should "be runnable" in {
        val session = g.run()
        Await.result(session, 1000.millis) should not be (null)
    }
}
