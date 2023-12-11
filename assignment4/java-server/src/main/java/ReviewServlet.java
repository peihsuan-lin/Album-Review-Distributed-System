import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.AlbumDao;
import dao.AlbumDaoImpl;
import dto.ResponseMessage;
import dto.ReviewResponse;
import java.util.Properties;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

@WebServlet(name = "ReviewServlet", value = "/AlbumStore/review/*")
public class ReviewServlet extends HttpServlet {
  private AlbumDao albumDao;
  private KafkaProducer<String, String> producer;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    albumDao = new AlbumDaoImpl();
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producer = new KafkaProducer<>(props);
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
      sendMessageToKafka(jsonMessage, likeOrNot);
    } catch (Exception e) {
      System.out.println("Kafka Error in Server: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private void sendMessageToKafka(String jsonMessage, String likeOrNot) throws Exception {
    String topic = likeOrNot.equals("like") ? "like-topic" : "dislike-topic";
    producer.send(new ProducerRecord<>(topic, jsonMessage.toString()));
    System.out.println("Sending message to Kafka topic " + topic + ": " + jsonMessage);
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

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");
    String id = urlParts[2];
    ReviewResponse response = albumDao.getReviewCountById(id);
    Gson gson = new Gson();
    String responseJson = gson.toJson(response);
    try {
      res.setStatus(HttpServletResponse.SC_OK);
      res.getOutputStream().print(responseJson);
      res.getOutputStream().flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
