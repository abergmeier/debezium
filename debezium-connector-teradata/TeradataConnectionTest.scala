package teradata

import org.scalatest._

class TeradataConnectionTest extends FlatSpec{

  it should "make connection" in{
    val tc = new TeradataConnection
    tc.makeConnection(<insertmehere>)
  }
}
