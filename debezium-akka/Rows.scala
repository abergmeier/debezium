/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package debezium

import io.debezium.data.Bits
import io.debezium.time.Date
import io.debezium.time.Time
import io.debezium.time.Timestamp

trait Columns {
	def getType(index: Int): Class[_ <: Any]
	def length(): Int
}

trait Row {

	def diff(other: Row): Map[Int, (Any, Any)]

	def get(index: Int): Any
/*
	def getByte(index: Int): Byte
	def getShort(index: Int): Short
	def getInt(index: Int): Int
	def getLong(index: Int): Long
	def getString(index: Int): String

	// def getBits(index: Int): Bits
	def getDate(index: Int): Date
	def getString(index: Int): String
	def getTime(index: Int): Time
	def getTimestamp(index: Int): Timestamp
*/
	def columnInfo: Columns
}
