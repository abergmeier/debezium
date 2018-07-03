
package teradata

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

class TeradataActorSpec extends FlatSpec with Matchers {
    "A TeradataActor" should "answer with multiple rows" in {
        val system = ActorSystem("TestSystem")
        val teradata = system.actorOf(Props[TeradataActor], "teradataActor")
        val future = ask(teradata, ExecuteSQL("SELECT CURRENT_TIME"))(1 seconds).mapTo[Iterable[Row]]
        val result = Await.result(future, 1 seconds)
        result should equal (Iterable(Row("Test1", "Test2"), Row("Test3", "Test4")))
    }
}
