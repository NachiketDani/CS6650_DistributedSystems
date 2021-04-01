import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class ChannelPool extends GenericObjectPool<Channel> {
  private static final ChannelPool pool = new ChannelPool(PooledChannelFactory.getInstance());

  public ChannelPool(PooledChannelFactory factory) {
    super(factory);
    GenericObjectPoolConfig<Channel> config = new GenericObjectPoolConfig<>();
    config.setMaxIdle(100);
    config.setMaxTotal(200);
    this.setConfig(config);
  }

  public static ChannelPool getChannelPoolInstance() {
    return pool;
  }
}