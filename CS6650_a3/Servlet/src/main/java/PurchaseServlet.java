import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {
  final private int STOREID_INDEX = 1;
  final private int CUSTOMER_INDEX = 2;
  final private int CUSTOMERID_INDEX = 3;
  final private int DATE_INDEX = 4;
  final private int DATE_VAL_INDEX = 5;
  private static final String EXCHANGE_NAME = "supermarket";
  private Channel channel;

  @Override
  public void init() throws ServletException {
    try {
      channel = ChannelPool.getChannelPoolInstance().borrowObject();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("Unable to retrieve a channel!!");
    }
  }

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
        response.getWriter().write("Invalid purchaseItems");
      } else {
        // request body is properly formatted, insert into the DB
        makePurchase(urlParts, requestBody);
      }
    } catch (Exception e) {
      response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
      response.getWriter().write("Invalid purchase");
    }
  }
  private void makePurchase(String[] urlParts, String purchase)
      throws IOException {
    int storeId = Integer.parseInt((urlParts[STOREID_INDEX]));
    int customerId = Integer.parseInt((urlParts[CUSTOMERID_INDEX]));

    PurchaseModel newPurchase = new PurchaseModel();
    newPurchase.setStoreID(storeId);
    newPurchase.setCustomerID(customerId);
    newPurchase.setDate(urlParts[DATE_VAL_INDEX]);
    newPurchase.setPurchase(purchase);
    // insert purchase into the DB
    String purchaseString = new Gson().toJson(newPurchase);
    channel.basicPublish(EXCHANGE_NAME,"", null,
        purchaseString.getBytes("UTF-8"));
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

    if (!urlParts[CUSTOMER_INDEX].equals("customer")
        || !urlParts[DATE_INDEX].equals("date")) {
      return false;
    }

    return verifyDate(urlParts[DATE_VAL_INDEX])
        && verifyCustomerId(urlParts[CUSTOMERID_INDEX])
        && verifyStoreId(urlParts[STOREID_INDEX]);
  }
}