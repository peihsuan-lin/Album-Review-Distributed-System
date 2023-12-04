package rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class RMQChannelFactory extends BasePooledObjectFactory<Channel> {

  private final Connection connection;
  // used to count created channels for debugging
  private int count;
  private static final String QUEUE_NAME = "REVIEW_QUEUE";
  private static final String EXCHANGE_NAME = "REVIEW_EXCHANGE";

  public RMQChannelFactory(Connection connection) {
    this.connection = connection;
    count = 0;
  }

  @Override
  synchronized public Channel create() throws Exception {
    count++;
    Channel channel = connection.createChannel();
    channel.exchangeDeclare(EXCHANGE_NAME, "direct");
    channel.queueDeclare(QUEUE_NAME, true, false, false, null);
    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
    return channel;
  }

  @Override
  public PooledObject<Channel> wrap(Channel channel) {
    return new DefaultPooledObject<>(channel);
  }

  public int getChannelCount() {
    return count;
  }
}
