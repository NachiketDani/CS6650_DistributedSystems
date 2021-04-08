import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Singleton Class for Channel Pool Factory
 */
public class ChannelFactory extends BasePooledObjectFactory<Channel> {
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private static final String HOST = System.getProperty("RABBITMQ_HOST");
  private static final String USERNAME = System.getProperty("RABBITMQ_USERNAME");
  private static final String PASSWORD = System.getProperty("RABBITMQ_PASSWORD");
  private static final boolean DURABLE = false;
  private static ChannelFactory channelFactory;
  private static ConnectionFactory factory=null;
  private static Connection connection=null;


  static {
    try {
      channelFactory = new ChannelFactory();
      factory = new ConnectionFactory();
      factory.setHost(HOST);
      factory.setUsername(USERNAME);
      factory.setPassword(PASSWORD);
      connection = factory.newConnection();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
      System.err.println("Error instantiating ChannelFactory");
    }
  }

  /**
   * Get instance method for the Channel Pool Factory
   * @return Channel Pool Factory instance
   */
  public static ChannelFactory getInstance() {
    return channelFactory;
  }


  /**
   * Create Channel factory method
   * @return Channel
   * @throws Exception Exception for failure to create channel
   */
  @Override
  public Channel create() throws Exception {
    // TODO Auto-generated method stub
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, DURABLE);
    return channel;
  }

  /**
   * Wrap method for the Channel factory pool
   * @param channel
   * @return
   */
  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    // TODO Auto-generated method stub
    return new DefaultPooledObject<>(channel);
  }
}
