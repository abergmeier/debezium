/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import io.debezium.connector.teradata.AbstractResultSet

import java.sql.Date
import java.sql.Time
import java.sql.Timestamp
import java.sql.Types

import org.scalatest._

class TestResultSet(b: Byte, s: Short, i: Int, l: Long, c: String, d: Date, t: Time, ts: Timestamp) extends AbstractResultSet {
    override def getByte(i: Int): Byte = {
        i match {
            case 1 => return b
            case _ => throw new IndexOutOfBoundsException(f"No Byte at index $i")
        }
    }

    override def getShort(i: Int): Short = {
        i match {
            case 2 => return s
            case _ => throw new IndexOutOfBoundsException(f"No Short at index $i")
        }
    }

    override def getInt(i: Int): Int = {
        i match {
            case 3 => return i
            case _ => throw new IndexOutOfBoundsException(f"No Int at index $i")
        }
    }

    override def getLong(i: Int): Long = {
        i match {
            case 4 => return l
            case _ => throw new IndexOutOfBoundsException(f"No Long at index $i")
        }
    }

    override def getString(i: Int): String = {
        i match {
            case 0 => "T"
            case 5 => return c
            case _ => throw new IndexOutOfBoundsException(f"No String at index $i")
        }
    }

    override def getDate(i: Int): Date = {
        i match {
            case 6 => return d
            case _ => throw new IndexOutOfBoundsException(f"No Date at index $i")
        }
    }

    override def getTime(i: Int): Time = {
        i match {
            case 7 => return t
            case _ => throw new IndexOutOfBoundsException(f"No Time at index $i")
        }
    }

    override def getTimestamp(i: Int): Timestamp = {
        i match {
            case 8 => return ts
            case _ => throw new IndexOutOfBoundsException(f"No Timestamp at index $i")
        }
    }
}

object TestMetadata {
    def getColumnType(index: Int): Int = {
        index match {
            case 0 => Types.CHAR
            case 1 => Types.TINYINT
            case 2 => Types.SMALLINT
            case 3 => Types.INTEGER
            case 4 => Types.BIGINT
            case 5 => Types.CHAR
            case 6 => Types.DATE
            case 7 => Types.TIME
            case 8 => Types.TIMESTAMP
            case _ => throw new IndexOutOfBoundsException(f"Index $index not available")
        }
    }
}

class ResultSetValueSpec extends FlatSpec with Matchers {
    val result1 = new TestResultSet(0, 1, 2, 3, "c", new java.sql.Date(45), new java.sql.Time(67), new java.sql.Timestamp(453))
    val result2 = new TestResultSet(0, 1, 2, 3, "c", new java.sql.Date(45), new java.sql.Time(67), new java.sql.Timestamp(453))
    "ResultSetValue" should "compare equal values" in {
        val equals = (0 until 8)
            .map(ResultSetValue.cellEqual(result1, result2, TestMetadata, _))
        equals shouldEqual Iterable(true, true, true, true, true, true, true, true)
    }
}
