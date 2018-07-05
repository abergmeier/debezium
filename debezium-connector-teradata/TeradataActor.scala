
package teradata

import java.sql.ResultSet
import akka.actor.Actor
import akka.actor.ActorLogging

class TeradataActor extends Actor with ActorLogging {

    def receive = {
        case ExecuteSQL(sql) => {
            log.info("Execute SQL on Teradata: " + sql)
            val tc = new TeradataConnection
            tc.startConnection()
            val statement = tc.con.createStatement()
            val resultSet = statement.executeQuery(sql.asInstanceOf[String])
            sender() ! SQLStream(resultSet)
            tc.close()
            //sender() ! SQLStream(Iterable[Row](Row("Test1", "Test2"), Row("Test3", "Test4")))
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
