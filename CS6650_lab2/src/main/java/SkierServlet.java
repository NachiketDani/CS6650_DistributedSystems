import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "SkierServlet")
public class SkierServlet extends HttpServlet {

  protected void doPost(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {

    res.setContentType(("text/plain"));
    String urlPath = req.getPathInfo();
    // check we have a URL
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }
    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)
    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("Invalid parameters");
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // TODO: process url params in `urlParts`
      // TODO: process the JSON request body. Convert to a string!
      BufferedReader requestBodyReader = req.getReader();
      StringBuilder requestBodyBuilder = new StringBuilder();
      String line;
      while ( (line = requestBodyReader.readLine()) != null) {
        requestBodyBuilder.append(line);
      }
      String requestBody = requestBodyBuilder.toString().replaceAll(" ", "");
      // System.out.println(requestBody);
      // TODO: process request body somehow
      // processRequestBody(requestBody);
      res.getWriter().write(requestBody);
    }
  }

  /*
  Get Request
   */
  protected void doGet(HttpServletRequest req,
      HttpServletResponse res)
      throws ServletException, IOException {

    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("It works!");
    }
  }

  private boolean isUrlValid(String[] urlParts) {
    // TODO: validate the request url path according to the API spec
    // urlPath  = "/1/seasons/2019/day/1/skier/123"
    // urlParts = [, 1, seasons, 2019, day, 1, skier, 123]

    /*
    Validate if word parts are accurate in URL
     */
    if (!urlParts[2].equals("seasons") || !urlParts[4].equals("day") || !urlParts[6]
        .equals("skier")) {
      return false;
    }
    ;

    /*
    Validate if ids are integers
     */
    try {
      int resortId = Integer.parseInt(urlParts[1]);
      int seasonId = Integer.parseInt(urlParts[3]);
      int dayId = Integer.parseInt(urlParts[5]);
      int skierId = Integer.parseInt(urlParts[7]);
      if (resortId < 0 || seasonId < 0 || dayId < 0 || skierId < 0) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
