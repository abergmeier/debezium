/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import io.debezium.connector.teradata.AbstractResultSet

import java.sql.Types

import org.scalatest._

class TestResultSet(b: Byte, s: Short) extends AbstractResultSet {
    private var hasNext = true

    override def getByte(i: Int): Byte = {
        i match {
            case 1 => b
            case _ => throw new IndexOutOfBoundsException(f"No Byte at index $i")
        }
    }

    override def getShort(i: Int): Short = {
        i match {
            case 2 => s
            case _ => throw new IndexOutOfBoundsException(f"No Short at index $i")
        }
    }

    override def getString(i: Int): String = {
        i match {
            case 0 => "TR"
            case _ => throw new IndexOutOfBoundsException(f"No String at index $i")
        }
    }

    override def next(): Boolean = {
        if (!hasNext)
            return false

        hasNext = false
        true
    }
}

object TestMetadata {
    def getColumnType(index: Int): Int = {
        index match {
            case 0 => Types.CHAR
            case 1 => Types.TINYINT
            case 2 => Types.SMALLINT
            case _ => throw new IndexOutOfBoundsException(f"Index $index not available")
        }
    }

    def getColumnCount(): Int = 3
}

class ResultSetStreamSpec extends FlatSpec with Matchers {

    val result = new TestResultSet(5, 13)

    "A ResultSetStream" should "produce List" in {
        val results = ResultSetStream(result, TestMetadata)
            .toList

        results shouldEqual List(("TR", 5, 13))
    }
}
