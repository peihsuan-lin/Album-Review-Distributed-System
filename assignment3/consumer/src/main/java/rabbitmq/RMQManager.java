package rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RMQManager {

  final Connection RMQConn;
  private RMQChannelFactory channelFactory;

  public RMQManager() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    this.RMQConn = factory.newConnection();
    this.channelFactory = new RMQChannelFactory(this.RMQConn);
  }

  public Connection getRMQConn() {
    return RMQConn;
  }

  public RMQChannelFactory getChannelFactory() {
    return channelFactory;
  }
}
