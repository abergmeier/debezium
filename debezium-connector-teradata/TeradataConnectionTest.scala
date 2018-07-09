package teradata

import java.sql.{Connection, ResultSet, Timestamp}

import org.scalatest._
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import scala.collection.mutable
import scala.io.Source


case class KeySet(value: Set[String])

case class InfoMap(value: mutable.Map[String, mutable.Map[String, Any]])

class TeradataConnectionTest extends FlatSpec{

  val logger = LoggerFactory.getLogger(classOf[TeradataConnectionTest]);


  /* "CURRENT_TIMESTAMP, " +
 "FIRMKZ_NOA " +
"RG_NR, " +
 "RET_SCHL_NR, " +
 "TRIM(RET_SCHL_CHAR), " +
 "TRIM(LV_DATUM), " +
 "ART_NR, " +
 "TRIM(PROM), " +
 "RG_POS_NR, " +
 "MG_STK, " +
 "VALUTA_AUF_KZ, " +
 "RATEN_AUF_CT, " +
 "VALUTA_AUF_CT, " +
 "VK_CT, " +
 "orderChannel, " +
 "TRIM(orderDate), " +
 "shopDomain, " +
 "papaKey, " +
 "YTAN " +
 "FROM asis_ws_raas_tok_f2_view.v_invoicedetails ORDER BY TRIM(KTNR_NOA)")
*/

  it should "make connection and execute a query" in{
    val tc = new TeradataConnection
    var m = mutable.Map[String,String]()
    val filename = "debezium-connector-teradata/config.properties"
    for (line <- Source.fromFile(filename).getLines){
      val sa = line.split("=")
      m += (sa(0) -> sa(1))
    }
    tc.startConnection(m getOrElse("jdbcstring",""), m getOrElse("user",""), m getOrElse("password",""))

    val expectedTableAnDatabasenameAndTimestampKey = "asis_ws_raas_tok_f2_view_v_invoicedetails"
    val actualTableAnDatabasenameAndTimestamp = getTableAndDatabasenameAndTimestamp(tc.con)
    assert(actualTableAnDatabasenameAndTimestamp.contains(expectedTableAnDatabasenameAndTimestampKey))

    val expectedLongDatetime: Long = 1490354208000L
    val actualLastAlteredTimestamp = getLastAlteredTimestamp(tc.con)
    assert(actualLastAlteredTimestamp.getTime === expectedLongDatetime)
    tc.close()
  }

  def checkConnection(con: Connection): Boolean = {
    if (con == null) {
      logger.error("Connection to Teradata is not up!")
      false
    } else {
      true
    }
  }

  def makeQuery(con: Connection, query: String, handleF: ResultSet => Any): Any = {
    try {
      val statement = con.createStatement()
      val resultSet = statement.executeQuery(query)
      val res = handleF(resultSet)
      return res
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def getLastAlteredTimestamp(con: Connection): Timestamp = {
    if (!checkConnection(con)) {
      return null
    }
    val getLastAlteredTimestampFromTable = "SELECT createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND databasename='asis_ws_raas_tok_f2_view' AND tablename='v_invoicedetails'"
    val res = makeQuery(con,getLastAlteredTimestampFromTable, handleGetLastAlteredTimestamp)
    if (res.isInstanceOf[Timestamp]) {
      res.asInstanceOf[Timestamp]
    } else {
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

  def getTableAndDatabasenameAndTimestamp(con: Connection): mutable.Map[String, mutable.Map[String, Any]] = {
    if (!checkConnection(con)) {
      return null
    }
    val getTableAndDatabasenameAndTimestamp = "SELECT tablename, databasename, createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND tablename LIKE '%invoice%'"
    val res = makeQuery(con,getTableAndDatabasenameAndTimestamp, handleGetTableAndDatabasenameAndTimestamp)
    if (res.isInstanceOf[InfoMap]) {
      res.asInstanceOf[InfoMap].value
    } else {
      mutable.Map[String, mutable.Map[String, Any]]()
    }
  }

  def handleGetTableAndDatabasenameAndTimestamp(resultSet: ResultSet): InfoMap = {
    var infoMap = new InfoMap(mutable.Map[String, mutable.Map[String, Any]]())
    var innerMap = mutable.Map[String, Any]()
    while (resultSet.next()) {
      innerMap += ("tablename" -> resultSet.getString("tablename"))
      innerMap += ("databasename" -> resultSet.getString("databasename"))
      innerMap += ("lastaltertimestamp" -> resultSet.getTimestamp("lastaltertimestamp"))
      innerMap += ("createtimestamp" -> resultSet.getTimestamp("createtimestamp"))
      infoMap.value += (resultSet.getString("databasename").trim() + "_" + resultSet.getString("tablename").trim() -> innerMap)
      //println("t: "+resultSet.getString("tablename")+ " d: "+ resultSet.getString("databasename"))
    }
    infoMap
  }



}

