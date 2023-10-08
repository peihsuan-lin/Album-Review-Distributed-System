import com.google.gson.Gson;
import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumInfo;
import java.io.BufferedReader;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "AlbumServlet", value = "/AlbumServlet")
public class AlbumServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    String[] urlParts = urlPath.split("/");
    if (isValidRequestDoGet(urlParts)) {
      sendAlbumResponse(res);
    } else {
      sendErrorResponse(res);
    }
  }
  private boolean isValidRequestDoGet(String[] urlPath) {
    if (urlPath.length != 3) {
      return false;
    }
    try {
      Integer.parseInt(urlPath[2]);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
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
  private void sendAlbumResponse(HttpServletResponse res) throws IOException {
    AlbumResponse albumResponse = new AlbumResponse();
    albumResponse.setArtist("Sex Pistols");
    albumResponse.setTitle("Never Mind the Bollocks");
    albumResponse.setYear("1977");
    Gson gson = new Gson();
    res.setStatus(HttpServletResponse.SC_OK);
    res.getOutputStream().print(gson.toJson(albumResponse));
    res.getOutputStream().flush();
  }
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("application/json");
    String urlPath = req.getPathInfo();
    NewAlbumResponse albumMessage = new NewAlbumResponse();
    ResponseMessage message = new ResponseMessage();
    Gson gson = new Gson();
    String urlParts[] = urlPath.split("/");

    if (urlPath == null || urlPath.isEmpty()) {
      sendErrorResponse(res);
      return;
    }
    if (!isValidRequestDoPost(urlParts)) {
      sendErrorResponse(res);
      return;
    }
    BufferedReader reader = req.getReader();
    StringBuilder requestBody = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      requestBody.append(line);
    }
    String jsonData = requestBody.toString();
    sendNewAlbumResponse(res);
  }
  private void sendNewAlbumResponse(HttpServletResponse res) throws IOException {
    NewAlbumResponse albumResponse = new NewAlbumResponse();
    albumResponse.setAlbumID("string");
    albumResponse.setImageSize("string");
    Gson gson = new Gson();
    res.setStatus(HttpServletResponse.SC_OK);
    res.getOutputStream().print(gson.toJson(albumResponse));
    res.getOutputStream().flush();
  }

}
