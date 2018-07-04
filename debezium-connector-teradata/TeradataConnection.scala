package teradata

import java.sql.{Connection, DriverManager, SQLException, Timestamp}
import java.util.Properties


class TeradataConnection {

  def makeConnection(connectionString: String) = {
    println("Start Progress")
    var con: Connection = null
    val driver = "com.teradata.jdbc.TeraDriver"
    val connectionProps = new Properties
  try{
      Class.forName(driver)
      con = DriverManager.getConnection(connectionString)
    println("Connected to database")
      println("Make Query")
      val s = getKeysFromInvoiceDetails(con)
      println(s)
    con.close()
    } catch {
      case cnfe: ClassNotFoundException =>
        cnfe.printStackTrace()
        con.close()
      case se: SQLException =>
        se.printStackTrace()
        con.close()
    }
  }

  def getKeysFromInvoiceDetails(con: Connection): Set[String] = {
    var keys: Set[String] = Set()
    try {
      val statement = con.createStatement()
      val resultSet = statement.executeQuery("HELP VIEW asis_ws_raas_tok_f2_view.v_invoicedetails;")
      while (resultSet.next()){
        keys = keys + resultSet.getString(1)
      }
      println("no results left")
    }catch {
      case e: Exception =>
        e.printStackTrace()
        con.close()
    }
    keys
  }

  def getLastAlteredTimestamp(con: Connection): Timestamp = {
    var lat: Timestamp = null
    try {
      val statement = con.createStatement()
      val resultSet = statement.executeQuery("SELECT createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND databasename='asis_ws_raas_tok_f2_view' AND tablename='v_invoicedetails'")
      while (resultSet.next()){
          if (resultSet.isLast) {
            lat = resultSet.getTimestamp("lastaltertimestamp")
             lat
          }
      }
    }catch {
      case e: Exception =>
        e.printStackTrace()
        con.close()
    }
    lat
  }

  def printMetainfo(con: Connection, stringMatch: String) = {
    try {
      val statement = con.createStatement()
      val resultSet = statement.executeQuery("SELECT tablename, databasename, createtimestamp, lastaltertimestamp FROM DBC.Tables WHERE Tablekind = 'V' AND tablename LIKE '%"+stringMatch+"%'")

      while (resultSet.next()){
        val tn = resultSet.getObject("tablename")
        val dn = resultSet.getObject("databasename")
        val lat = resultSet.getObject("lastaltertimestamp")
        val ct = resultSet.getObject("createtimestamp")
        println("tablename: "+tn+" dname: "+dn+" ct: "+ct+" lat: "+lat)
      }
      println("No Results left")
    }catch {
      case e: Exception =>
        e.printStackTrace()
        con.close()
    }
  }

  def selectQuery(con: Connection) = {
    try {
      val statement = con.createStatement()
      val resultSet = statement.executeQuery("SELECT RSLVD_DTTM FROM asis_ws_raas_tok_f2_view.v_invoicedetails sample 10;")

      while (resultSet.next()){
        println(resultSet.getObject("RSLVD_DTTM"))
      }
      println("No Results left")
    }catch {
      case e: Exception =>
        e.printStackTrace()
        con.close()
    }
  }
}