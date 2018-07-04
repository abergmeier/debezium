
package teradata

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

class DataActorSpec extends FlatSpec with Matchers {
    val system = ActorSystem("TestSystem")

    "A DataActor" should "be initially empty" in {
        val store = system.actorOf(Props[DataActor], "dataActorInitial")
        val future = ask(store, DataActor.Get(Set[String]()))(1 second).mapTo[Map[String, Row]]
        val result = Await.result(future, 1 second)
        result shouldBe empty
    }

    "A DataActor" should "store rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorStore")
        val data = Map[String, Row]("A" -> Row("Value5", "Value6"), "B" -> Row("Value2", "Value3"))
        store ! DataActor.Put(data)
        val future = ask(store, DataActor.Get(Set[String]("A")))(1 second).mapTo[Map[String, Row]]
        var result = Await.result(future, 1 second)
        result should equal (Map[String, Row]("A" -> Row("Value5", "Value6")))
    }

    "A DataActor" should "delete rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorDelete")
        val data = Map[String, Row]("C" -> Row("Value9", "Value8"), "D" -> Row("Value1", "Value2"))
        store ! DataActor.Put(data)
        store ! DataActor.Delete(Set[String]("C"))
        val future = ask(store, DataActor.Get(Set[String]("C")))(1 second).mapTo[Map[String, Row]]
        val result = Await.result(future, 1 second)
        result shouldBe empty
    }
}
