/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import io.debezium.connector.AbstractSourceInfo

/**
 * Information about the source of information, which for normal events contains information about the transaction id and the
 * LSN position in the server WAL.
 *
 * <p>
 * The {@link #partition() source partition} information describes the database server for which we're streaming changes.
 * Typically, the server is identified by the host address port number and the name of the database. Here's a JSON-like
 * representation of an example database:
 *
 * <pre>
 * {
 *     "server" : "production-server"
 * }
 * </pre>
 *
 * <p>
 * The {@link #offset() source offset} information describes a structure containing the position in the server's WAL for any
 * particular event, transaction id and the server timestamp at which the transaction that generated that particular event has
 * been committed. When performing snapshots, it may also contain a snapshot field which indicates that a particular record
 * is created while a snapshot it taking place.
 * Here's a JSON-like representation of an example:
 *
 * <pre>
 * {
 *     "ts_usec": 1465937,
 *     "lsn" : 99490,
 *     "txId" : 123,
 *     "snapshot": true
 * }
 * </pre>
 *
 * The "{@code ts_usec}" field contains the <em>microseconds</em> since Unix epoch (since Jan 1, 1970) representing the time at
 * which the transaction that generated the event was committed while the "{@code txId}" represents the server's unique transaction
 * identifier. The "{@code lsn}" field represent a numerical (long) value corresponding to the server's LSN for that particular
 * event and can be used to uniquely identify an event within the WAL.
 *
 * The {@link #source() source} struct appears in each message envelope and contains information about the event. It is
 * a mixture the fields from the {@link #partition() partition} and {@link #offset() offset}.
 * Like with the offset, the "{@code snapshot}" field only appears for events produced when the connector is in the
 * middle of a snapshot. Here's a JSON-like representation of the source for an event that corresponds to the above partition and
 * offset:
 *
 * <pre>
 * {
 *     "name": "production-server",
 *     "ts_usec": 1465937,
 *     "lsn" : 99490,
 *     "txId" : 123,
 *     "snapshot": true
 * }
 * </pre>
 *
 * @author Andreas Bergmeier
 */

object SourceInfo {
    val SERVER_NAME_KEY = "name"

    val SCHEMA = AbstractSourceInfo.schemaBuilder()
        .name("io.debezium.connector.teradata.Source")
        .field(SERVER_NAME_KEY, Schema.STRING_SCHEMA)
        .build()
}

class SourceInfo extends AbstractSourceInfo(Module.version()) {
    /**
     * Get a {@link Schema} representation of the source {@link #partition()} and {@link #offset()} information.
     *
     * @return the source partition and offset {@link Schema}; never null
     * @see #source()
     */
    override def schema(): Schema =
        SourceInfo.SCHEMA

    override def toString(): String = {
        val sb = new StringBuilder("source_info[")
        sb.append(']')
        sb.toString()
    }
}
