package com.sample;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Kafka as a persistent cache.
 */
@Slf4j
@Component
public class KafkaDAO {
    /**
     * Kafka topic name.
     */
    private static final String STORAGE_TOPIC = "storage-topic";

    /**
     * Value can be in any format Apache Avro, Protocol Buffers, JSON.
     */
    @Autowired
    private KafkaTemplate<String, String> template;

    /**
     * Cache items from Kafka.
     */
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    /**
     * Find item by key.
     */
    public String find(String key) {
        return CACHE.get(key);
    }

    /**
     * Receive all items.
     */
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(CACHE);
    }

    /**
     * Add New item or Update exist item with new value.
     */
    public void addOrUpdate(String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            log.error("cannot put empty message: ({},{})", key, value);
            return;
        }

        template.send(STORAGE_TOPIC, key, value)
                .addCallback(new KafkaListenableFutureCallback());
        CACHE.put(key, value);
        print();
    }

    /**
     * Every time after startup application, the Listener read all messages from topic.
     * If any instance of application add item to Kafka,
     * then these changes will be automatically synchronized to all instances.
     */
    @KafkaListener(topicPartitions = @TopicPartition(
            topic = STORAGE_TOPIC, partitionOffsets =
            {@PartitionOffset(partition = "0", initialOffset = "0")}))
    public void listener(ConsumerRecord<String, String> record) {
        log.info("reading message: ({},{})", record.key(), record.value());
        CACHE.put(record.key(), record.value());
    }

    /**
     * Just print all items, for debug.
     */
    public String print() {
        log.info("cache items:");

        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : CACHE.entrySet()) {
            String message = String.format("(%s,%s)", entry.getKey(), entry.getValue());
            log.info(message);

            result.append(message);
            result.append("<br/>");
        }

        log.info("");

        return result.toString();
    }

    /**
     * Create compact topic with auto delete old items from Kafka.
     */
    @Bean
    public NewTopic getStorageTopic() {
        return TopicBuilder.name(STORAGE_TOPIC)
                .partitions(1)
                .replicas(1)
                .compact()
                .config(TopicConfig.RETENTION_MS_CONFIG, "100")
                .config(TopicConfig.MAX_COMPACTION_LAG_MS_CONFIG, "100")
                .build();
    }
}
