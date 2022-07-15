## kafka-as-persistent-cache

```sh
# Run Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Run Kafka 
bin/kafka-server-start.sh config/server.properties

# Show topics
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Write some events into the topic
bin/kafka-console-producer.sh --topic storage-topic --bootstrap-server localhost:9092

# For example
# key1:value1
# key2:value2
# key3:value3

# Read the events
bin/kafka-console-consumer.sh --topic storage-topic --from-beginning --property print.key=true --property print.value=true --bootstrap-server localhost:9092

# Terminate the kafka environment
# Remove log, topics, everything
rm -rf /tmp/kafka-logs /tmp/zookeeper
```

Application links, for testing
* [http://localhost:8080/sample/](http://localhost:8080/sample/)
* [http://localhost:8080/sample/add?key=one&value=1](http://localhost:8080/sample/add?key=one&value=1)
* [http://localhost:8080/sample/find?key=one](http://localhost:8080/sample/find?key=one)
