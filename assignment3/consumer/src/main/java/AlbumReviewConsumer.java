import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

public class AlbumReviewConsumer implements Runnable {

  private static String QUEUE_NAME = "REVIEW_QUEUE";
  private static String EXCHANGE_NAME = "REVIEW_EXCHANGE";
  private final Gson gson = new Gson();
  private final Connection rabbitConn;
  private final DBHandler dbHandler;

  public AlbumReviewConsumer(Connection rabbitConn) {
    this.rabbitConn = rabbitConn;
    this.dbHandler = new DBHandler();
  }

  @Override
  public void run() {
    try {
      Channel channel = rabbitConn.createChannel();
      channel.exchangeDeclare(this.EXCHANGE_NAME, "direct");
      channel.queueDeclare(this.QUEUE_NAME, true, false, false, null);
      channel.queueBind(this.QUEUE_NAME, this.EXCHANGE_NAME, "");
      channel.basicQos(1);
      final DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        ReviewMessage reviewMessage = gson.fromJson(message, ReviewMessage.class);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        dbHandler.storeReviewInDb(reviewMessage);
      };
      channel.basicConsume(this.QUEUE_NAME, false, deliverCallback, (consumerTag) -> {
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}