
package teradata

import io.debezium.time.Date
import io.debezium.time.Time
import io.debezium.time.Timestamp

import java.sql.ResultSet

import java.time.temporal.TemporalAdjuster

object DebeziumValue {
    def getDate(data: ResultSet, index: Int): Int = {
        val adjustor: Option[TemporalAdjuster] = None
        Date.toEpochDay(data.getDate(index), null)
    }

    def getTime(data: ResultSet, index: Int): Int = {
        val adjustor: Option[TemporalAdjuster] = None
        Time.toMilliOfDay(data.getTime(index), null)
    }

    def getTimestamp(data: ResultSet, index: Int): Long = {
        val adjustor: Option[TemporalAdjuster] = None
        Timestamp.toEpochMillis(data.getTimestamp(index), null)
    }
}
