import java.util.Properties;
import org.apache.kafka.clients.producer.*;

public class KafkaPublisher {
  private final String DO_NOT_WAIT = "0";
  private final String WAIT_FOR_LEADER = "1";
  private final String WAIT_FOR_ALL = "all";  // most durable option
  private static final String KAFKA_HOST = System.getProperty("KAFKA_HOST");
  private static final String KAFKA_PORT = System.getProperty("KAFKA_PORT");
  private static KafkaPublisher publisher = new KafkaPublisher();
  private final Producer<String, String> producer;

  private KafkaPublisher() {
    Properties props = new Properties();
    props.put("bootstrap.servers", KAFKA_HOST + ":" + KAFKA_PORT);
    props.put("acks", WAIT_FOR_LEADER);
    props.put("retries", 0);
    props.put("linger.ms", 50);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<>(props);
  }
  public static KafkaPublisher getInstance() {
    return publisher;
  }
  public Producer<String, String> getProducer() {
    return producer;
  }
}
