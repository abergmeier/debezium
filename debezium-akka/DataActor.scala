
package debezium

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.concurrent.duration._

object DataActor {
    case class Delete(keys: Set[Any]) {
    }

    case class Get(keys: Set[Any]) {
    }

    case class Put(data: Map[Any, Row]) {
    }

    case class DataResponse(data: Map[Any, Row]) {
    }
}

class DataActor extends Actor with ActorLogging {

    private var dataMap = Map[Any, Row]()

    def receive = {
        case DataActor.Get(keys) => {
            log.info("Getting by keys " + keys + "...")
            val requestedMap = dataMap.filterKeys(keys.contains(_))
            sender() ! DataActor.DataResponse(requestedMap)
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