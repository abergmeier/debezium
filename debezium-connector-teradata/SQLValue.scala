
package teradata

import java.sql.ResultSet

object SQLValue {
    def byteEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getByte(index) == rhs.getByte(index)
    }

    def shortEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getShort(index) == rhs.getShort(index)
    }

    def intEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getInt(index) == rhs.getInt(index)
    }

    def longEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getLong(index) == rhs.getLong(index)
    }

    def stringEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getString(index) == rhs.getString(index)
    }

    def dateEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getDate(index) == rhs.getDate(index)
    }

    def timeEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getTime(index) == rhs.getTime(index)
    }

    def timestampEqual(index: Int, lhs: ResultSet, rhs: ResultSet): Boolean = {
        lhs.getTimestamp(index) == rhs.getTimestamp(index)
    }

    def getByte(data: ResultSet, index: Int): Byte = data.getByte(index)
    def getShort(data: ResultSet, index: Int): Short = data.getShort(index)
    def getInt(data: ResultSet, index: Int): Int = data.getInt(index)
    def getLong(data: ResultSet, index: Int): Long = data.getLong(index)
    def getString(data: ResultSet, index: Int): String = data.getString(index)
}