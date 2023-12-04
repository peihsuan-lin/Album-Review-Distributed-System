package connections;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import rabbitmq.RMQManager;

public class RMQConn {

  private Connection connection;

  public RMQConn() {
    ConnectionFactory factory = new ConnectionFactory();
    try (InputStream input = RMQManager.class.getClassLoader()
        .getResourceAsStream("rabbitmq.properties")) {

      if (input == null) {
        System.out.println("unable to find rabbitmq.properties");
        return;
      }
      Properties props = new Properties();
      props.load(input);
      String host = props.getProperty("host");
      int port = Integer.parseInt(props.getProperty("port"));
      String username = props.getProperty("username");
      String password = props.getProperty("password");
      factory.setHost(host);
      factory.setPort(port);
      factory.setUsername(username);
      factory.setPassword(password);
      this.connection = factory.newConnection();
    } catch (IOException | TimeoutException | NumberFormatException e) {
      throw new RuntimeException(e);
    }
  }

  public Connection getConnection() {
    return connection;
  }
}
