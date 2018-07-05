
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.pattern.ask

import java.time.Instant

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.mutable.Queue

object SnapshotActor {
    case class TakeSnapshot() {
    }

    case class SnapshotTaken(data: Map[String, Row], databaseTimestamp: Long) {
    }
}

class SnapshotActor(teradataActor: ActorRef) extends Actor with ActorLogging {

    private val databaseTimestamp = Instant.EPOCH.getEpochSecond
    private val snapshotReceipients = new Queue[ActorRef]()

    def receive = {
        case SnapshotActor.TakeSnapshot => {
            log.info("Taking a snapshot of the DB...")
            snapshotReceipients.enqueue(sender())
            teradataActor ! ExecuteSQL("SELECT CURRENT_TIME FROM MEVIEW")
        }
        case SQLStream(results) => {
            val receipient = snapshotReceipients.dequeue()
            val resultMap = results.map{row: Row => (row.field1, row)}.toMap
            receipient ! SnapshotActor.SnapshotTaken(resultMap, databaseTimestamp)
        }
    }
}