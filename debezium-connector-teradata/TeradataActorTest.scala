
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask
import scala.concurrent.duration._
import org.scalatest._

import scala.io.Source
import scala.collection.mutable
import scala.concurrent.Await
import java.io.File

object TestActor {
    object Test {
    }
}

class TestActor(teradataActor: ActorRef) extends Actor with Matchers with ActorLogging{
    def receive = {
        case TestActor.Test => {
            var m = mutable.Map[String,String]()
            val filename = "debezium-connector-teradata/config.properties"
            for (line <- Source.fromFile(filename).getLines){
                val sa = line.split("=")
                m += (sa(0) -> sa(1))
            }
            teradataActor ! StartTeradataConnection(m getOrElse("jdbcstring",""), m getOrElse("user",""), m getOrElse("password",""))
            teradataActor ! ExecuteSQL("SELECT TRIM(KTNR_NOA), CURRENT_TIMESTAMP FROM asis_ws_raas_tok_f2_view.v_invoicedetails ORDER BY TRIM(KTNR_NOA)")
        }
        case SQLStream(data) => {
            data.getTimestamp(2) should not be None
            data.getString(1) shouldBe "102099267"
            data.next()
            context.system.terminate()
        }
    }
}

class TeradataActorSpec extends FlatSpec with Matchers {

    "A TeradataActor" should "answer with multiple rows" in {
        val system = ActorSystem("TestSystem")
        val teradata = system.actorOf(Props[TeradataActor], "teradataActor")
        val test = system.actorOf(Props(classOf[TestActor], teradata), "testActor")
        test ! TestActor.Test
        Await.ready(system.whenTerminated, 5 seconds)
    }
}
