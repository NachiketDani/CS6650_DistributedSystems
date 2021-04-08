import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Singleton Class for Channel Pool
 */
public class ChannelPool extends GenericObjectPool<Channel> {
  private static final ChannelPool pool = new ChannelPool(ChannelFactory.getInstance());

  /**
   * Constructor for Channel Pool class
   * @param factory channel factory
   */
  public ChannelPool(ChannelFactory factory) {
    super(factory);
    GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
    config.setMaxIdle(100);
    config.setMaxTotal(200);
    this.setConfig(config);
  }

  /**
   * Get Instance method for Channel Pool
   * @return Channel Pool instance
   */
  public static ChannelPool getChannelPoolInstance() {
    return pool;
  }
}