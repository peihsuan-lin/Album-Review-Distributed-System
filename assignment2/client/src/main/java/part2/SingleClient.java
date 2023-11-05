package part2;

import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import io.swagger.client.model.ImageMetaData;
import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleClient implements Runnable {

  private int numRequests;
  private String IPAddr;
  private static AtomicInteger numOfSuccessReq = new AtomicInteger(0);
  private static AtomicInteger numOfFailReq = new AtomicInteger(0);
  private final int MAX_RETRY = 5;
  private CountDownLatch completed;
  private static ConcurrentLinkedQueue<Record> records = new ConcurrentLinkedQueue<>();


  public SingleClient(int numRequests, String IPAddr, CountDownLatch completed) {
    this.numRequests = numRequests;
    this.IPAddr = IPAddr;
    this.completed = completed;
  }

  @Override
  public void run() {
    DefaultApi client = new DefaultApi();
    client.getApiClient().setBasePath(IPAddr);

    for (int i = 0; i < numRequests; i++) {
      boolean success = false;
      for (int j = 0; j < MAX_RETRY; j++) {
        try {
          AlbumsProfile album = new AlbumsProfile();
          album.setArtist("Sex Pistols");
          album.setTitle("Never Mind the Bollocks");
          album.setYear("1977");
          // POST API
          String imagePath = System.getProperty("user.dir") + "/src/main/resources/image.png";
          long postStart = System.currentTimeMillis();
          ImageMetaData imageMetaData = client.newAlbum(new File(imagePath), album);
          long postEnd = System.currentTimeMillis();
          records.add(new Record(postStart, "POST", postEnd - postStart, 201));
          // GET API
          long startGet = System.currentTimeMillis();
          AlbumInfo albumByKey = client.getAlbumByKey(imageMetaData.getAlbumID());
          long endGet = System.currentTimeMillis();
          records.add(new Record(startGet, "GET", endGet - startGet, 200));
          numOfSuccessReq.incrementAndGet();
          success = true;
          completed.countDown();
          break;
        } catch (ApiException e) {
          e.printStackTrace();
        }
      }
      if (!success) {
        numOfFailReq.incrementAndGet();
        completed.countDown();
      }
    }
  }

  public static ConcurrentLinkedQueue<Record> getRecords() {
    return records;
  }

  public static AtomicInteger getNumOfSuccessReq() {
    return numOfSuccessReq;
  }

  public static AtomicInteger getNumOfFailReq() {
    return numOfFailReq;
  }
}
