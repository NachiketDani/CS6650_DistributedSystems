import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PooledChannelFactory extends BasePooledObjectFactory<Channel> {
  private static PooledChannelFactory channelFactory = null;
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private static final String HOST = "localhost";
  private final ConnectionFactory factory;
  private final Connection connection;

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

  private PooledChannelFactory() throws IOException, TimeoutException {
    factory = new ConnectionFactory();
    factory.setHost(HOST);
    connection = factory.newConnection();
  }

  @Override
  public Channel create() throws Exception {
    // TODO Auto-generated method stub
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
    return channel;
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    // TODO Auto-generated method stub
    return new DefaultPooledObject<Channel>(channel);
  }
}
