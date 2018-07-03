/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */

package teradata

import java.sql.SQLException
import java.util.ArrayList
import java.util.List
import java.util.Map
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import io.debezium.config.Configuration
import io.debezium.config.Field
import io.debezium.connector.base.ChangeEventQueue
import io.debezium.connector.common.BaseSourceTask
import io.debezium.util.LoggingContext

/**
 * Kafka connect source task which uses Postgres logical decoding over a streaming replication connection to process DB changes.
 *
 * @author Andreas Bergmeier
 */
class TeradataConnectorTask extends BaseSourceTask {

    val logger = LoggerFactory.getLogger(classOf[TeradataConnectorTask]);
    val running = new AtomicBoolean(false);

    override def start(config: Configuration): Unit = {
        if (running.get()) {
            // already running
            return
        }

        val connectorConfig = new TeradataConnectorConfig(config);
    }

    def createSnapshotProducer(sourceInfo: SourceInfo) = {
        logger.info("Taking a new snapshot of the DB and streaming logical changes once the snapshot is finished...");
        //producer = new RecordsSnapshotProducer(taskContext, sourceInfo, true);
    }

    override def commit() = {
        if (running.get()) {
            //producer.commit(lastProcessedLsn);
        }
    }

    override def poll(): List[SourceRecord] = {
        new ArrayList[SourceRecord]()
        /*
        List<ChangeEvent> events = changeEventQueue.poll();

        if (events.size() > 0) {
            for (int i = events.size() - 1; i >= 0; i--) {
                SourceRecord r = events.get(i).getRecord();
                if (events.get(i).isLastOfLsn()) {
                    Map<String, ?> offset = r.sourceOffset();
                    lastProcessedLsn = (Long)offset.get(SourceInfo.LSN_KEY);
                    break;
                }
            }
        }
        return events.stream().map(ChangeEvent::getRecord).collect(Collectors.toList());
        */
    }

    override def stop() = {
        if (running.compareAndSet(true, false)) {
            //producer.stop();
        }
    }

    override def version(): String = {
        Module.version()
    }

    override def getAllConfigurationFields(): java.lang.Iterable[Field] = {
        TeradataConnectorConfig.ALL_FIELDS
    }
}
