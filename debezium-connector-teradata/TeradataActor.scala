
package teradata

import java.sql.ResultSet
import akka.actor.Actor
import akka.actor.ActorLogging

import com.sun.rowset.CachedRowSetImpl

import javax.sql.rowset.CachedRowSet

class TeradataActor extends Actor with ActorLogging {

    def receive = {
        case ExecuteSQL(sql) => {
            log.info("Execute SQL on Teradata: " + sql)
            val tc = new TeradataConnection
            tc.startConnection()
            val crs = new CachedRowSetImpl()
            //crs.setType(CachedRowSet.TYPE_FORWARD_ONLY)
            //crs.setFetchDirection(CachedRowSet.FETCH_FORWARD)
            crs.setCommand(sql)
            crs.execute(tc.con)
            crs.next()
            sender() ! SQLStream(crs)
            tc.close()
        }
    }
}
