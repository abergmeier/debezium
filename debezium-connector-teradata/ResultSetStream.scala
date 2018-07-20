/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import debezium.Columns
import debezium.Row

import java.sql.ResultSet

private object IterableResultSet {
    def apply(data: ResultSet, metadata: Metadata.All): IterableResultSet =
        return new IterableResultSet(data, metadata)
}

private final class IterableResultSet(val data: ResultSet, val metadata: Metadata.All) extends Iterable[Row] {

    private object ResultSetColumns extends Columns {
        override def getType(index: Int) = ResultSetValue.getType(metadata, index)

        override def length(): Int = metadata.getColumnCount
    }

    final class CurrentResultSet extends Row {
        val uglyHackData = data

        def canEqual(a: Any) = a.isInstanceOf[Row]
        override def equals(other: Any): Boolean = {
            other match {
                case other: CurrentResultSet => {
                    columnInfo.length() == other.columnInfo.length() && cellsMatch(other)
                }
                case other: Row => {
                    columnInfo.length() == other.columnInfo.length() && cellsMatch(other)
                }
                case other: Product => {
                    val data = other.productIterator.toList
                    columnInfo.length() == data.length && cellsMatch(data)
                }
                case _ => false
            }
        }

        def cellsMatch(other: CurrentResultSet): Boolean =
            (0 until columnInfo.length())
                .map(ResultSetValue.cellEqual(uglyHackData, other.uglyHackData, metadata, _))
                .forall(_ == true)

        def cellsMatch(other: Row): Boolean =
            (0 until metadata.getColumnCount())
                .map(i => ResultSetValue.valueEqual(uglyHackData, other.get(i), metadata, i))
                .forall(_ == true)

        def cellsMatch(other: List[Any]): Boolean =
            (0 until metadata.getColumnCount())
                .zip(other)
                .map{case (i, o) => ResultSetValue.valueEqual(uglyHackData, o, metadata, i)}
                .forall(_ == true)

        override def diff(other: Row): Map[Int, (Any, Any)] =
            throw new UnsupportedOperationException()

        def diff(other: CurrentResultSet): Map[Int, (Any, Any)] = {
            val columnIndices = 0 until columnInfo.length();
            columnIndices
                .filter(!ResultSetValue.cellEqual(uglyHackData, other.uglyHackData, metadata, _))
                .map((_, (get(_), other.get(_))))
                .toMap
        }
        
        override def get(index: Int): Any = {
            ResultSetValue.get(data, metadata, index)
        }

        override def columnInfo: Columns = ResultSetColumns
    }

    final class RowIterator(data: ResultSet) extends Iterator[Row] {
        private val current = new CurrentResultSet
        override def hasNext: Boolean = data.next
        override def next: Row = current
    }

    override def iterator: Iterator[Row] = new RowIterator(data)
}

object ResultSetStream {
    def apply(data: ResultSet): Stream[Row] = apply(data, data.getMetaData)
    def apply(data: ResultSet, metadata: Metadata.All): Stream[Row] =
        IterableResultSet(data, metadata).toStream
}
