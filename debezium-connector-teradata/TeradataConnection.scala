package teradata

import java.sql._
import java.util.Properties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scala.collection.mutable

case class KeySet(value: Set[String])
case class InfoMap(value: mutable.Map[String,mutable.Map[String,Any]])

class TeradataConnection {

  val logger = LoggerFactory.getLogger(classOf[TeradataConnection]);
  var con: Connection = null


  def startConnection(jdbcString: String, teradataUser: String, teradataPassword: String): Unit ={
    logger.info("Start Connection")
    val driver = "com.teradata.jdbc.TeraDriver"
    try{
      Class.forName(driver)
      this.con = DriverManager.getConnection(jdbcString, teradataUser, teradataPassword)
      logger.info("Connection successful")
    }catch {
      case cnfe: ClassNotFoundException =>
        logger.error("Class could not be found\n %v",cnfe.getMessage)
        cnfe.printStackTrace()
      case se: SQLException =>
        se.printStackTrace()
    }
  }

  def checkConnection(): Boolean = {
    if(this.con == null){
      logger.error("Connection to Teradata is not up!")
      false
    }else{
      true
    }
  }


  def close() = {
    this.con.close()
  }

  def makeQuery(query: String, handleF: ResultSet => Any): Any = {
    try {
      val statement = this.con.createStatement()
      val resultSet = statement.executeQuery(query)
      val res = handleF(resultSet)
      return res
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def getKeysFromInvoiceDetails(): Set[String] = {
    if(!checkConnection){
      return null
    }
    val getKeysInvoicdeDetails = "HELP VIEW asis_ws_raas_tok_f2_view.v_invoicedetails;"
    val res = makeQuery(getKeysInvoicdeDetails, handleGetKeysFromInvoiceDetails)
    if (res.isInstanceOf[KeySet]) {
      res.asInstanceOf[KeySet].value
    }else{
      Set()
    }
  }

  def handleGetKeysFromInvoiceDetails(resultSet: ResultSet): KeySet = {
    var keys: Set[String] = Set()
    while (resultSet.next()) {
      keys += resultSet.getString(1)
    }
    new KeySet(keys)
  }

  def getLastAlteredTimestamp(): Timestamp = {
    if(!checkConnection){
      return null
    }
    val getLastAlteredTimestampFromTable = "SELECT createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND databasename='asis_ws_raas_tok_f2_view' AND tablename='v_invoicedetails'"
    val res = makeQuery(getLastAlteredTimestampFromTable, handleGetLastAlteredTimestamp)
    if (res.isInstanceOf[Timestamp]) {
      res.asInstanceOf[Timestamp]
    }else{
      null
    }
  }

  def handleGetLastAlteredTimestamp(resultSet: ResultSet): Timestamp = {
    var lat: Timestamp = null
    while (resultSet.next()) {
      if (resultSet.isLast) {
        lat = resultSet.getTimestamp("lastaltertimestamp")
      }
    }
    lat
  }

  def getTableAndDatabasenameAndTimestamp(): mutable.Map[String,mutable.Map[String,Any]] = {
    if(!checkConnection){
      return null
    }
    val getTableAndDatabasenameAndTimestamp = "SELECT tablename, databasename, createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND tablename LIKE '%invoice%'"
    val res = makeQuery(getTableAndDatabasenameAndTimestamp, handleGetTableAndDatabasenameAndTimestamp)
    if (res.isInstanceOf[InfoMap]){
      res.asInstanceOf[InfoMap].value
    }else{
      mutable.Map[String, mutable.Map[String, Any]]()
    }
  }

  def handleGetTableAndDatabasenameAndTimestamp(resultSet: ResultSet): InfoMap = {
    var infoMap = new InfoMap(mutable.Map[String, mutable.Map[String, Any]]())
    var innerMap = mutable.Map[String,Any]()
    while (resultSet.next()) {
      innerMap += ("tablename" -> resultSet.getString("tablename"))
      innerMap += ("databasename" -> resultSet.getString("databasename"))
      innerMap += ("lastaltertimestamp" -> resultSet.getTimestamp("lastaltertimestamp"))
      innerMap += ("createtimestamp" -> resultSet.getTimestamp("createtimestamp"))
       infoMap.value += (resultSet.getString("databasename") +"_"+resultSet.getString("tablename") -> innerMap)
    }
    infoMap
  }
}
