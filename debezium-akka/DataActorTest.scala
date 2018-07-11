
package teradata

import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

import io.debezium.connector.teradata.AbstractResultSet

import java.sql.ResultSet

class TestResultSet(value1: String, value2: String) extends AbstractResultSet {
    def canEqual(a: Any) = a.isInstanceOf[TestResultSet]
    override def equals(that: Any): Boolean =
        that match {
            case that: TestResultSet => that.canEqual(this) && this.hashCode == that.hashCode
            case _ => false
    }

    override def hashCode: Int = {
        return value1.hashCode ^ value2.hashCode
    }
}

class DataActorSpec extends FlatSpec with Matchers {
    val system = ActorSystem("TestSystem")

    "A DataActor" should "be initially empty" in {
        val store = system.actorOf(Props[DataActor], "dataActorInitial")
        val future = ask(store, DataActor.Get(Set[String]()))(1 second).mapTo[DataActor.Data]
        val result = Await.result(future, 1 second)
        result.data shouldBe empty
    }

    "A DataActor" should "store rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorStore")
        val data = Map[String, ResultSet]("A" -> new TestResultSet("Value5", "Value6"), "B" -> new TestResultSet("Value2", "Value3"))
        store ! DataActor.Put(data)
        val future = ask(store, DataActor.Get(Set[String]("A")))(1 second).mapTo[DataActor.Data]
        var result = Await.result(future, 1 second)
        result.data should equal (Map[String, ResultSet]("A" -> new TestResultSet("Value5", "Value6")))
    }

    "A DataActor" should "delete rows" in {
        val store = system.actorOf(Props[DataActor], "dataActorDelete")
        val data = Map[String, ResultSet]("C" -> new TestResultSet("Value9", "Value8"), "D" -> new TestResultSet("Value1", "Value2"))
        store ! DataActor.Put(data)
        store ! DataActor.Delete(Set[String]("C"))
        val future = ask(store, DataActor.Get(Set[String]("C")))(1 second).mapTo[DataActor.Data]
        val result = Await.result(future, 1 second)
        result.data shouldBe empty
    }
}
