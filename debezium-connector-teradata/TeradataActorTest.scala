
package teradata

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

object TestActor {
    object Test {
    }
}

class TestActor(teradataActor: ActorRef) extends Actor with Matchers {
    def receive = {
        case TestActor.Test => {
            teradataActor ! ExecuteSQL("SELECT CURRENT_TIME")
        }
        case SQLStream(data) => {
            data shouldBe (Iterable[Row](Row("Test1", "Test2"), Row("Test3", "Test4")))
        }
    }
}

class TeradataActorSpec extends FlatSpec with Matchers {

    val system = ActorSystem("TestSystem")

    "A TeradataActor" should "answer with multiple rows" in {
        val teradata = system.actorOf(Props[TeradataActor], "teradataActor")
        val test = system.actorOf(Props(classOf[TestActor], teradata), "sinkActor")
        test ! TestActor.Test
    }
}
