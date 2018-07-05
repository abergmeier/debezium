
package teradata

import java.sql.ResultSet
import akka.actor.Actor
import akka.actor.ActorLogging

import com.sun.rowset.CachedRowSetImpl

import java.sql.CachedRowSet

class TeradataActor extends Actor with ActorLogging {

    def receive = {
        case ExecuteSQL(sql) => {
            log.info("Execute SQL on Teradata: " + sql)
            val tc = new TeradataConnection
            tc.startConnection()
            val crs = new CachedRowSetImpl()
            crs.setType(CachedRowSet.TYPE_FORWARD_ONLY)
            crs.setFetchDirection(CachedRowSet.FETCH_FORWARD)
            crs.setCommand(sql)
            crs.execute(tc)
            sender() ! SQLStream(crs)
            tc.close()
        }
    }

    def handleResultInQuery(resultSet: ResultSet): String = {
        var l: String = ""
        while(resultSet.next()) {
           l = resultSet.getString(1)
        }
        l
    }

}
