
package exasol

import org.apache.kafka.connect.source.SourceConnector

class ExasolSourceConnector(/*xc: Int, yc: Int*/) extends SourceConnector {
/*
  val x: Int = xc
  val y: Int = yc
  override def move(dx: Int, dy: Int): Point =
    new Point(x + dx, y + dy)
*/
	override def version(): String = {
		"0.0.1"
	}
}
