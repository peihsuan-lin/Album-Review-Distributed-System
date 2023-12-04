import com.rabbitmq.client.Connection;
import connections.RMQConn;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumReviewConsumerService {

  private static final int CONSUMER_THREADS = 250;

  public static void main(String[] args) {
    Connection rmqConn = new RMQConn().getConnection();
    ExecutorService pool = Executors.newFixedThreadPool(CONSUMER_THREADS);
    for (int i = 0; i < CONSUMER_THREADS; i++) {
      pool.execute(new AlbumReviewConsumer(rmqConn));
    }
  }
}