/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import io.debezium.time.Date
import io.debezium.time.Time
import io.debezium.time.Timestamp

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Types

package object Metadata {
    type Type = {def getColumnType(index: Int): Int}
    type Length = {def getColumnCount(): Int}
    type All = {
        def getColumnCount(): Int
        def getColumnType(index: Int): Int
    }
}

package object ResultSetValue {
    def get[M <: Metadata.Type](data: ResultSet, metadata: M, index: Int): Any = {
        metadata.getColumnType(index) match {
            case Types.TINYINT   => SQLValue.getByte(data, index)
            case Types.SMALLINT  => SQLValue.getShort(data, index)
            case Types.INTEGER   => SQLValue.getInt(data, index)
            case Types.BIGINT    => SQLValue.getLong(data, index)
            case Types.CHAR      => SQLValue.getString(data, index)
            case Types.VARCHAR   => SQLValue.getString(data, index)
            case Types.DATE      => DebeziumValue.getDate(data, index)
            case Types.TIME      => DebeziumValue.getTime(data, index)
            case Types.TIMESTAMP => DebeziumValue.getTimestamp(data, index)
            case _ => throw new UnsupportedOperationException()
        }
    }

    def cellEqual[M <: Metadata.Type](lhs: ResultSet, rhs: ResultSet, metadata: M, index: Int): Boolean = {
        val columnType = metadata.getColumnType(index)
        columnType match {
            case Types.TINYINT   => SQLValue.byteEqual(index, lhs, rhs)
            case Types.SMALLINT  => SQLValue.shortEqual(index, lhs, rhs)
            case Types.INTEGER   => SQLValue.intEqual(index, lhs, rhs)
            case Types.BIGINT    => SQLValue.longEqual(index, lhs, rhs)
            case Types.CHAR      => SQLValue.stringEqual(index, lhs, rhs)
            case Types.VARCHAR   => SQLValue.stringEqual(index, lhs, rhs)
            case Types.DATE      => SQLValue.dateEqual(index, lhs, rhs)
            case Types.TIME      => SQLValue.timeEqual(index, lhs, rhs)
            case Types.TIMESTAMP => SQLValue.timestampEqual(index, lhs, rhs)
            case _         => throw new UnsupportedOperationException()
        }
    }

    def getType[M <: Metadata.Type](metadata: M, index: Int): Class[_ <: Any] = {
        metadata.getColumnType(index) match {
            case Types.TINYINT   => classOf[Byte]
            case Types.SMALLINT  => classOf[Short]
            case Types.INTEGER   => classOf[Int]
            case Types.BIGINT    => classOf[Long]
            case Types.CHAR      => classOf[String]
            case Types.VARCHAR   => classOf[String]
            case Types.DATE      => classOf[Date]
            case Types.TIME      => classOf[Time]
            case Types.TIMESTAMP => classOf[Timestamp]
            case _ => throw new UnsupportedOperationException()
        }
    }

    def valueEqual[M <: Metadata.Type](lhs: ResultSet, rhs: Any, metadata: M, index: Int): Boolean = {
        metadata.getColumnType(index) match {
            case Types.TINYINT   => lhs.getByte(index) == rhs
            case Types.SMALLINT  => lhs.getShort(index) == rhs
            case Types.INTEGER   => lhs.getInt(index) == rhs
            case Types.BIGINT    => lhs.getLong(index) == rhs
            case Types.CHAR      => lhs.getString(index) == rhs
            case Types.VARCHAR   => lhs.getString(index) == rhs
            case Types.DATE      => DebeziumValue.getDate(lhs, index) == rhs
            case Types.TIME      => DebeziumValue.getTime(lhs, index) == rhs
            case Types.TIMESTAMP => DebeziumValue.getTimestamp(lhs, index) == rhs
            case _ => throw new UnsupportedOperationException()
        }
    }
}