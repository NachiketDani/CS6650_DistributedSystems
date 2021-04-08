import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import java.io.FileInputStream;
import java.util.Properties;

public class Receiver {
  private static final int NUM_THREADS = 75;

  public static void main(String[] argv) throws Exception {
    // set config properties
    FileInputStream configFile = new FileInputStream("config.properties");
    Properties props = new Properties(System.getProperties());
    props.load(configFile);
    System.setProperties(props);

    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(System.getProperty("RABBITMQ_HOST"));
    factory.setUsername(System.getProperty("RABBITMQ_USERNAME"));
    factory.setPassword(System.getProperty("RABBITMQ_PASSWORD"));
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
