package teradata

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.ask

import java.time.Instant

import scala.concurrent.duration._

import org.scalatest._

import scala.concurrent.Await

class SourceActor extends Actor with ActorLogging {
    def receive = {
        case ExecuteSQL => {
            log.info("Answering with dummy...")
            sender() ! Iterable[Row](Row("ABCD", "EFGH"), Row("IJKL", "MNOP"))
        }
    }
}

object TestActor {
    object Test {
    }
}

class TestActor(snapshotActor: ActorRef) extends Actor with ActorLogging with Matchers{
    def receive = {
        case TestActor.Test => {
            snapshotActor ! SnapshotActor.TakeSnapshot
        }
        case SnapshotActor.SnapshotTaken(data, databaseTimestamp) => {
            data shouldBe (Map[String, Row]("ABCD" -> Row("ABCD", "EFGH"), "IJKL" -> Row("IJKL", "MNOP")))
            databaseTimestamp shouldBe Instant.EPOCH.getEpochSecond
        }
    }
}

class SnapshotActorSpec extends FlatSpec with Matchers {

    private val system = ActorSystem("TestSystem")

    "A SnapshotActor" should "answer with multiple ChangeEvents" in {
        val source = system.actorOf(Props[SourceActor], "sourceActor")
        val snapshotter = system.actorOf(Props(classOf[SnapshotActor], source), "snapshotActor")
        val tester = system.actorOf(Props(classOf[TestActor], snapshotter), "testActor")
        tester ! TestActor.Test
    }
}