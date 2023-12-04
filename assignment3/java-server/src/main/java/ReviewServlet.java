import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.AlbumDao;
import dao.AlbumDaoImpl;
import dto.ResponseMessage;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.rabbitmq.client.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import rabbitmq.RMQManager;

@WebServlet(name = "ReviewServlet", value = "/AlbumStore/review/*")
public class ReviewServlet extends HttpServlet {


  private AlbumDao albumDao;
  private RMQManager rmqManager;
  GenericObjectPool<Channel> pool;
  private static final String EXCHANGE_NAME = "REVIEW_EXCHANGE";


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    albumDao = new AlbumDaoImpl();
    GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    poolConfig.setMaxTotal(200);
    rmqManager = new RMQManager();
    pool = new GenericObjectPool<>(rmqManager.getChannelFactory(), poolConfig);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String urlParts[] = urlPath.split("/");

    if (urlPath == null || urlPath.isEmpty()) {
      sendErrorResponse(res);
      return;
    }
    if (urlParts.length != 3) {
      sendErrorResponse(res);
    }
    String likeOrNot = urlParts[1];
    String albumID = urlParts[2];
    String jsonMessage = buildJsonMessage(likeOrNot, albumID);
    try {
      sendMessageToRabbitMQ(jsonMessage);
    } catch (TimeoutException e) {
      System.out.println("RabbitMQ Error in Server: " + e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      System.out.println("RabbitMQ Error in Server: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private void sendMessageToRabbitMQ(String jsonMessage) throws Exception {
    Channel channel = pool.borrowObject();
    channel.basicPublish(EXCHANGE_NAME, "", null, jsonMessage.getBytes(StandardCharsets.UTF_8));
    pool.returnObject(channel);
    System.out.println("sending message to rabbitmq: " + jsonMessage);
  }

  private String buildJsonMessage(String likeOrNot, String albumID) {
    JsonObject messageObject = new JsonObject();
    messageObject.addProperty("albumID", albumID);
    messageObject.addProperty("likeOrNot", likeOrNot);
    return messageObject.toString();
  }

  private void sendErrorResponse(HttpServletResponse res) throws IOException {
    ResponseMessage message = new ResponseMessage();
    Gson gson = new Gson();
    message.setMsg("string");
    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    res.getOutputStream().print(gson.toJson(message));
    res.getOutputStream().flush();
  }
}
