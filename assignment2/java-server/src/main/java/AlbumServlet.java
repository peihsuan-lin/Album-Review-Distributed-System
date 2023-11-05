import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dao.AlbumDao;
import dao.AlbumDaoImpl;
import io.swagger.client.model.AlbumInfo;
import io.swagger.client.model.AlbumsProfile;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import dao.DynamoManager;

@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
@MultipartConfig
public class AlbumServlet extends HttpServlet {

  private DynamoDbClient dynamoDbClient;
  private AlbumDao albumDao;
  private static final Logger logger = Logger.getLogger(AlbumServlet.class.getName());


  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    albumDao = new AlbumDaoImpl();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");
    String id = urlParts[2];
    if (isValidRequestDoGet(urlParts)) {
      GetItemResponse response = albumDao.getItemById(id);
      if (response == null || !response.hasItem()) {
        // key not found
        sendErrorResponse(res);
        return;
      }
      if (response.hasItem()) {
        String profileJson = response.item().get("profile").s();
        String decodedJson = new Gson().fromJson(profileJson, String.class);
        res.setStatus(HttpServletResponse.SC_OK);
        res.getOutputStream().print(decodedJson);
        res.getOutputStream().flush();
      } else {
        sendErrorResponse(res);
      }
    } else {
      sendErrorResponse(res);
    }
  }

  private boolean isValidRequestDoGet(String[] urlPath) {
    return urlPath.length == 3;
  }

  private boolean isValidRequestDoPost(String[] urlPath) {
    if (urlPath.length != 2) {
      return false;
    }
    return true;
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
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String urlParts[] = urlPath.split("/");

    if (urlPath == null || urlPath.isEmpty()) {
      sendErrorResponse(res);
      return;
    }
    if (!isValidRequestDoPost(urlParts)) {
      sendErrorResponse(res);
      return;
    }
    Part imagePart = req.getPart("image");
    String profileString = req.getParameter("profile");
    AlbumsProfile profile = new AlbumsProfile();
    String[] lines = profileString.split("\n");
    for (String line : lines) {
      line = line.trim();
      if (line.startsWith("artist:")) {
        profile.setArtist(line.replace("artist:", "").trim());
      } else if (line.startsWith("title:")) {
        profile.setTitle(line.replace("title:", "").trim());
      } else if (line.startsWith("year:")) {
        profile.setYear(line.replace("year:", "").trim());
      }
    }
    String jsonProfile = new Gson().toJson(profile);
    InputStream imageInputStream = imagePart.getInputStream();
    byte[] imageByteArray = imageInputStream.readAllBytes();
    String uuid = albumDao.putNewItem(imageByteArray, jsonProfile);
    sendNewAlbumResponse(res, uuid, String.valueOf(imageByteArray.length));
  }

  private void sendNewAlbumResponse(HttpServletResponse res, String imageId, String size)
      throws IOException {
    NewAlbumResponse albumResponse = new NewAlbumResponse();
    albumResponse.setAlbumID(imageId);
    albumResponse.setImageSize(size + " bytes");
    Gson gson = new Gson();
    res.setStatus(HttpServletResponse.SC_OK);
    res.getOutputStream().print(gson.toJson(albumResponse));
    res.getOutputStream().flush();
  }

}
