import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumReviewConsumerService {

  private static final int CONSUMER_THREADS = 3; // 3 threads for 3 partitions

  public static void main(String[] args) {
    ExecutorService pool = Executors.newFixedThreadPool(CONSUMER_THREADS);
    for (int i = 0; i < CONSUMER_THREADS; i++) {
      pool.execute(new AlbumReviewConsumer());
    }
  }
}