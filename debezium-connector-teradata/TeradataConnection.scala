package teradata

import java.sql._
import java.util.Properties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scala.collection.mutable


case class StartTeradataConnection(jdbcString: String, teradataUser: String, teradataPassword: String) {
}

class TeradataConnection {

  val logger = LoggerFactory.getLogger(classOf[TeradataConnection]);
  var con: Connection = null


  def startConnection(jdbcString: String, teradataUser: String, teradataPassword: String): Unit = {
    logger.info("Start Connection")
    val driver = "com.teradata.jdbc.TeraDriver"
    try {
      Class.forName(driver)
      this.con = DriverManager.getConnection(jdbcString, teradataUser, teradataPassword)
      logger.info("Connection successful")
    } catch {
      case cnfe: ClassNotFoundException =>
        logger.error("Class could not be found\n %v", cnfe.getMessage)
        cnfe.printStackTrace()
      case se: SQLException =>
        se.printStackTrace()
    }
  }

  def close() = {
    this.con.close()
  }


}
