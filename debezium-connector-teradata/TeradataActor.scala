
package teradata

import akka.actor.Actor
import akka.actor.ActorLogging

import com.sun.rowset.CachedRowSetImpl

import javax.sql.rowset.CachedRowSet

class TeradataActor extends Actor with ActorLogging {

    val teradataConnection = new TeradataConnection()

    def receive = {
        case StartTeradataConnection(jdbcstring, teradataUser, teradataPassword) => {
            teradataConnection.startConnection(jdbcstring,teradataUser,teradataPassword)
        }
        case ExecuteSQL(sql) => {
            log.info("Execute SQL on Teradata: " + sql)
            val crs = new CachedRowSetImpl()
            //crs.setType(CachedRowSet.TYPE_FORWARD_ONLY)
            //crs.setFetchDirection(CachedRowSet.FETCH_FORWARD)
            crs.setCommand(sql)
            crs.execute(teradataConnection.con)
            sender() ! SQLStream(crs)
            teradataConnection.close()
        }
    }
}
