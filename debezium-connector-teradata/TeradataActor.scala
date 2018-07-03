
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging

class TeradataActor extends Actor with ActorLogging {

    def receive = {
        case ExecuteSQL(sql) => {
            log.info("Execute SQL on Teradata: " + sql)
            sender() ! List(Row("Test1", "Test2"), Row("Test3", "Test4"))
        }
    }
}
