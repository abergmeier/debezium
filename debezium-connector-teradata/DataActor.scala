
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.concurrent.duration._

object DataActor {
    case class Delete(keys: Set[String]) {
    }

    case class Get(keys: Set[String]) {
    }

    case class Put(data: Map[String, Row]) {
    }
}

class DataActor extends Actor with ActorLogging {

    private var dataMap = Map[String, Row]()

    def receive = {
        case DataActor.Get(keys) => {
            log.info("Getting keys " + keys + "...")
            val requestedMap = dataMap.filterKeys(keys.contains(_))
            sender() ! requestedMap
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