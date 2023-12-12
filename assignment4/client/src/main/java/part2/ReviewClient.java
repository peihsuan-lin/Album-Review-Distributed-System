package part2;

import io.swagger.client.api.LikeApi;
import io.swagger.client.model.Likes;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ReviewClient implements Runnable {

  private int numRequests;
  private String IPAddr;
  private static AtomicInteger numOfSuccessReq = new AtomicInteger(0);
  private static AtomicInteger numOfFailReq = new AtomicInteger(0);
  private final int MAX_RETRY = 5;
  private CountDownLatch completed;
  private static ConcurrentLinkedQueue<Record> records = new ConcurrentLinkedQueue<>();

  public ReviewClient(String IPAddr, CountDownLatch completed) {
    this.IPAddr = IPAddr;
    this.completed = completed;
  }

  @Override
  public void run() {
    LikeApi likeClient = new LikeApi();
    // run GET until all POST requests are completed
    while (this.completed.getCount() > 0) {
      boolean success = false;
      for (int j = 0; j < MAX_RETRY; j++) {
        try {
//           GET REVIEW API
          long getReviewStart = System.currentTimeMillis();
          String albumId = MultiThreadClient.existingAlbumIds.take();
          Likes likes = likeClient.getLikes(albumId);
          long getReviewEnd = System.currentTimeMillis();
          records.add(new Record(getReviewStart, "GET_REVIEW", getReviewEnd - getReviewStart, 201));
          numOfSuccessReq.incrementAndGet();
          success = true;
          break;
        } catch (Exception e) {
          System.out.println("Exception: " + e.getMessage());
        }
      }
      if (!success) {
        numOfFailReq.incrementAndGet();
      }
    }
  }

  public static AtomicInteger getNumOfSuccessReq() {
    return numOfSuccessReq;
  }

  public static AtomicInteger getNumOfFailReq() {
    return numOfFailReq;
  }

  public static ConcurrentLinkedQueue<Record> getRecords() {
    return records;
  }
}
