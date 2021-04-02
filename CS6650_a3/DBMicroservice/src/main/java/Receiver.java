import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import java.io.FileInputStream;
import java.util.Properties;

public class Receiver {
  private static final int NUM_THREADS = 10;
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";

  public static void main(String[] argv) throws Exception {
    // set config properties
    FileInputStream configFile = new FileInputStream("config.properties");
    Properties props = new Properties(System.getProperties());
    props.load(configFile);
    System.setProperties(props);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(System.getProperty("RABBITMQ_HOST"));
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();
    String purchasesQueue = channel.queueDeclare().getQueue();

    Thread[] threads = new Thread[NUM_THREADS];

    for (int i = 0; i < NUM_THREADS; i++) {
      threads[i] = new Thread(new ReceiverRunnable(connection, purchasesQueue));
      threads[i].start();
    }

    for (int i = 0; i < NUM_THREADS; i++) {
      threads[i].join();
    }
  }
}
