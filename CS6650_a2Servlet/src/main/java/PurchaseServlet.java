import com.google.gson.Gson;
import java.io.BufferedReader;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {

//  @Override
//  protected void doGet(HttpServletRequest request, HttpServletResponse response)
//      throws ServletException, IOException {
//    response.setContentType("text/plain");
//    String urlPath = request.getPathInfo();
//
//    // check we have a URL!
//    if (urlPath == null || urlPath.isEmpty()) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      response.getWriter().write("missing parameters");
//      return;
//    }
//
//    String[] urlParts = urlPath.split("/");
//    if (!isUrlValid(urlParts)) {
//      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//      response.getWriter().write("Invalid request");
//    } else {
//      response.setStatus(HttpServletResponse.SC_OK);
//      // do any sophisticated processing with urlParts which contains all the url params
//      // TODO: process url params in `urlParts`
//      System.out.println("Worked through get request!!");
//      response.getWriter().write("It works!");
//    }
//  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/plain");
    String urlPath = request.getPathInfo();

    // check we have a URL
    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }

    // validate URL query params
    String[] urlParts = urlPath.split("/");
    if (!isUrlValid(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("Invalid parameters");
      return;
    }

    // validate request body
    String requestBody = parseRequestBody(request.getReader());
    try {
      Purchase purchase = new Gson().fromJson(requestBody, Purchase.class);
      if (!validatePurchase(purchase)) {
        response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
        response.getWriter().write("Invalid purchase");
        return;
      } else {
        // request body is properly formatted
        response.setStatus(HttpServletResponse.SC_CREATED);
        // System.out.println(requestBody);
        response.getWriter().write(requestBody);
        return;
      }
    } catch (Exception e) {
      response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
      response.getWriter().write("Invalid purchase");
      return;
    }
  }

  private String parseRequestBody(BufferedReader requestBodyReader) throws IOException {
    // read in request body
    StringBuilder requestBodyBuilder = new StringBuilder();
    String line;
    while ((line = requestBodyReader.readLine()) != null) {
      requestBodyBuilder.append(line);
    }
    return requestBodyBuilder.toString();
  }

  private boolean validatePurchase(Purchase purchase) {
    for (PurchaseItem item : purchase.getItems()) {
      if (item.getItemID() == null || item.getItemID().isEmpty()
          || item.getNumberOfItems() < 0) {
        return false;
      }
    }
    return true;
  }


  private boolean verifyDate(String dateString) {
    // verify date
    try {
      LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
    } catch (DateTimeException e) {
      return false;
    }
    return true;
  }

  private boolean verifyCustomerId(String customerIdString) {
    try {
      int customerId = Integer.parseInt(customerIdString);
      if (customerId < 0) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean verifyStoreId(String storeIdString) {
    try {
      int storeId = Integer.parseInt(storeIdString);
      if (storeId < 0) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean isUrlValid(String[] urlParts) {
    // urlPath  = "/purchase/store_id/customer/customer_id/date/YYYYMMDD"
    // urlParts = [ , 22, customer, 11, date, 20210101]
    final int STOREID_INDEX = 1;
    final int CUSTOMER_INDEX = 2;
    final int CUSTOMERID_INDEX = 3;
    final int DATE_INDEX = 4;
    final int DATE_VAL_INDEX = 5;

    if (!urlParts[CUSTOMER_INDEX].equals("customer")
        || !urlParts[DATE_INDEX].equals("date")) {
      return false;
    }

    return verifyDate(urlParts[DATE_VAL_INDEX])
        && verifyCustomerId(urlParts[CUSTOMERID_INDEX])
        && verifyStoreId(urlParts[STOREID_INDEX]);
  }
}