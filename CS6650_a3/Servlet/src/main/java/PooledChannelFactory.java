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
public class PooledChannelFactory extends BasePooledObjectFactory<Channel> {
  private static PooledChannelFactory channelFactory = null;
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private static final String HOST = "localhost";
  private final ConnectionFactory factory;
  private final Connection connection;

  /**
   * Get instance method for the Channel Pool Factory
   * @return Channel Pool Factory instance
   */
  public static PooledChannelFactory getInstance() {
    if(channelFactory == null) {
      try {
        channelFactory = new PooledChannelFactory();
      } catch (IOException | TimeoutException e) {
        e.printStackTrace();
        System.err.println("ChannelFactory instance error");
      }
    }
    return channelFactory;
  }

  /**
   * Constructor for Pooled Channel factory
   * @throws IOException Interrupted I/O operations failed
   * @throws TimeoutException Exception for timeout
   */
  private PooledChannelFactory() throws IOException, TimeoutException {
    factory = new ConnectionFactory();
    factory.setHost(HOST);
    connection = factory.newConnection();
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
    channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
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
    return new DefaultPooledObject<Channel>(channel);
  }
}
