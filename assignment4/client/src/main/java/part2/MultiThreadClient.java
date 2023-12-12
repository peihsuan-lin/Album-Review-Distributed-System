package part2;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class MultiThreadClient {

  private static int threadGroupSize;
  private static int numThreadGroups;
  private static int delay;
  private static String url;

  protected static BlockingQueue<String> existingAlbumIds = new LinkedBlockingQueue<>();

  public MultiThreadClient(int threadGroupSize, int numThreadGroups, int delay, String url) {
    this.threadGroupSize = threadGroupSize;
    this.numThreadGroups = numThreadGroups;
    this.delay = delay;
//    this.url = "http://localhost:8080/server/AlbumStore";
    this.url = "http://ec2-54-188-214-24.us-west-2.compute.amazonaws.com:8080/java-server-archive/AlbumStore";
  }


  final static private int FIRSTNUMTHREADS = 10;
  final static private int APIREQUEST1 = 100;
  final static private double millisecondsPerSecond = 1000.0;

  final static private int GETREVIEWTHREADS = 3;

  public void execute() throws InterruptedException, IOException {
    CountDownLatch completed = new CountDownLatch(FIRSTNUMTHREADS * APIREQUEST1);
    for (int i = 0; i < FIRSTNUMTHREADS; i++) {
      Thread t =
          new Thread(new SingleClient(APIREQUEST1, url, completed));
      t.start();
    }
    completed.await();
    CountDownLatch groupLatch = new CountDownLatch(threadGroupSize * numThreadGroups * APIREQUEST1);

    long start = System.currentTimeMillis();
    for (int i = 0; i < numThreadGroups; i++) {
      for (int j = 0; j < threadGroupSize; j++) {
        Thread t = new Thread(new SingleClient(APIREQUEST1, url, groupLatch));
        t.start();
      }
      if (i == 0) {
        // start 3 GET_REVIEW threads
        for (int j = 0; j < GETREVIEWTHREADS; j++) {
          Thread t2 = new Thread(new ReviewClient(url, groupLatch));
          t2.start();
        }

      }
      Thread.sleep(delay);
    }
    groupLatch.await();

    long end = System.currentTimeMillis();
    System.out.println("Test load:");
    System.out.println(
        "threadGroupSize: " + threadGroupSize + ", numThreadGroups: " + numThreadGroups
            + ", delay: " + delay);
    System.out.println("Time taken: " + (end - start) + " ms");
    double walltime = (end - start - delay * (numThreadGroups - 1)) / millisecondsPerSecond;
    int numOfRequests =
        SingleClient.getNumOfSuccessReq().get() + SingleClient.getNumOfFailReq().get();
    double throughput = (numOfRequests) / walltime;
    double getThroughput = ReviewClient.getNumOfSuccessReq().get() / walltime;

    System.out.println("Number of successful requests: " + SingleClient.getNumOfSuccessReq());
    System.out.println("Number of fail requests: " + SingleClient.getNumOfFailReq());
    System.out.println("Walltime: " + walltime + " seconds");
    System.out.println("Total throughput: " + throughput + " req/s");

    // GET metrics
    System.out.println("----------------------------------");
    System.out.println("GET metrics:");
    System.out.println("Number of successful requests: " + ReviewClient.getNumOfSuccessReq());
    System.out.println("Number of fail requests: " + ReviewClient.getNumOfFailReq());
    System.out.println("Walltime: " + walltime + " seconds");
    System.out.println("Total throughput: " + getThroughput  + " req/s");

    writeRecordsToFile();
  }

  private void writeRecordsToFile() throws IOException {
    RecordWriter writer = RecordWriter.getInstance();
    writer.calculateOutput(SingleClient.getRecords());
  }
}
