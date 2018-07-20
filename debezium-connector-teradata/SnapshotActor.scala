
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.pattern.ask

import debezium.Row

import java.time.Instant
import java.sql.ResultSet

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.collection.mutable.Queue

object SnapshotActor {
    case class TakeSnapshot() {
    }

    case class SnapshotTaken(data: Map[Any, Row], databaseTimestamp: Long) {
    }
}

class SnapshotActor(teradataActor: ActorRef) extends Actor with ActorLogging {

    private val snapshotReceipients = new Queue[ActorRef]()

    def receive = {
        case SnapshotActor.TakeSnapshot => {
            log.info("Taking a snapshot of the DB...")
            snapshotReceipients.enqueue(sender())
            teradataActor ! ExecuteSQL("SELECT TRIM(KTNR_NOA), CURRENT_TIMESTAMP FROM asis_ws_raas_tok_f2_view.v_invoicedetails ORDER BY TRIM(KTNR_NOA)")
        }
        case SQLStream(data) => {
            val receipient = snapshotReceipients.dequeue()
            var databaseTimestamp: Long = 0
            val resultMap = ResultSetStream(data)
                .map{row: Row => (row.get(1), row)}
                .toMap
            receipient ! SnapshotActor.SnapshotTaken(resultMap, databaseTimestamp)
        }
    }
}