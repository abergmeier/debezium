
package teradata

import java.sql.ResultSet

class RowIterable(resultSet: ResultSet) extends Iterable[ResultSet] {
    private class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
        def hasNext: Boolean = rs.next()
        def next(): ResultSet = rs
    }

    def iterator: Iterator[ResultSet] = {
        new ResultSetIterator(resultSet)
    }
}