
package debezium

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

class TestResultSet(val value1: String, val value2: String) extends Row {
    def canEqual(a: Any) = a.isInstanceOf[TestResultSet]
    override def equals(that: Any): Boolean =
        that match {
            case that: TestResultSet => value1 == that.value1 && value2 == that.value2
            case _ => false
    }

    override def hashCode: Int = value1.hashCode ^ value2.hashCode

    override def diff(other: Row): Map[Int, (Any, Any)] =
        throw new UnsupportedOperationException()

    override def get(index: Int): Any =
        throw new UnsupportedOperationException()

    override def columnInfo: Columns =
        throw new UnsupportedOperationException()
}

class DataActorSpec extends FlatSpec with Matchers {
    val system = ActorSystem("TestSystem")

    "A DataActor" should "be initially empty" in {
        val store = system.actorOf(Props[DataActor], "dataActorInitial")
        val future = ask(store, DataActor.Get(Set[Any]()))(1 second).mapTo[DataActor.DataResponse]
        val result = Await.result(future, 1 second)
        result.data shouldBe empty
    }

    "A DataActor" should "store rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorStore")
        val data = Map[Any, Row]("A" -> new TestResultSet("Value5", "Value6"), "B" -> new TestResultSet("Value2", "Value3"))
        store ! DataActor.Put(data)
        val future = ask(store, DataActor.Get(Set[Any]("A")))(1 second).mapTo[DataActor.DataResponse]
        var result = Await.result(future, 1 second)
        result.data should equal (Map[Any, Row]("A" -> new TestResultSet("Value5", "Value6")))
    }

    "A DataActor" should "delete rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorDelete")
        val data = Map[Any, Row]("C" -> new TestResultSet("Value9", "Value8"), "D" -> new TestResultSet("Value1", "Value2"))
        store ! DataActor.Put(data)
        store ! DataActor.Delete(Set[Any]("C"))
        val future = ask(store, DataActor.Get(Set[Any]("C")))(1 second).mapTo[DataActor.DataResponse]
        val result = Await.result(future, 1 second)
        result.data shouldBe empty
    }
}
