import com.google.gson.Gson;
import dao.AlbumDao;
import dao.AlbumDaoImpl;
import dto.NewAlbumResponse;
import dto.ResponseMessage;
import io.swagger.client.model.AlbumsProfile;
import java.io.InputStream;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@WebServlet(name = "AlbumServlet", value = "/AlbumStore/*")
@MultipartConfig
public class AlbumServlet extends HttpServlet {

  private AlbumDao albumDao;


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
