
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging

import java.sql.ResultSet

import scala.concurrent.duration._

object DataActor {
    case class Delete(keys: Set[String]) {
    }

    case class Get(keys: Set[String]) {
    }

    case class Data(data: Map[String, ResultSet]) {
    }

    case class Put(data: Map[String, ResultSet]) {
    }
}

class DataActor extends Actor with ActorLogging {

    private var dataMap = Map[String, ResultSet]()

    def receive = {
        case DataActor.Get(keys) => {
            log.info("Getting keys " + keys + "...")
            val requestedMap = dataMap.filterKeys(keys.contains(_))
            sender() ! DataActor.Data(requestedMap)
        }
        case DataActor.Put(data) => {
            log.info("Put data...")
            dataMap ++= data
        }
        case DataActor.Delete(keys) => {
            log.info("Deleting keys " + keys + "...")
            dataMap --= keys
        }
    }
}