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
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * Post Purchases Servlet
 */
@WebServlet(name = "PurchaseServlet", value = "/PurchaseServlet")
public class PurchaseServlet extends HttpServlet {
  final private int STOREID_INDEX = 1;
  final private int CUSTOMER_INDEX = 2;
  final private int CUSTOMERID_INDEX = 3;
  final private int DATE_INDEX = 4;
  final private int DATE_VAL_INDEX = 5;
  private static final String TOPIC = "purchases";
  private static final String EXCHANGE_NAME = "supermarket";
  private Channel channel;


  /**
   * Post Purchase Servlet method
   * @param request HTTP Incoming Request object
   * @param response HTTP Response object
   * @throws ServletException Servlet exception
   * @throws IOException
   */
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
      response.getWriter().write("Invalid URL parameters");
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
        // request body is properly formatted, insert into the DB through Kafka
        channel = ChannelPool.getChannelPoolInstance().borrowObject();
        System.out.println("Making purchase now!; Line 67 Purchase Servlet");
        makePurchase(urlParts, requestBody);
        ChannelPool.getChannelPoolInstance().returnObject(channel);
        response.setStatus((HttpServletResponse.SC_CREATED));
      }
    } catch (Exception e) {
      response.setStatus((HttpServletResponse.SC_BAD_REQUEST));
      response.getWriter().write("Bad purchase request");
    }
  }

  /**
   * Make Purchase helper method
   * @param urlParts URL flags
   * @param purchase purchase string body
   * @throws IOException I/O exception
   */
  private void makePurchase(String[] urlParts, String purchase)
      throws IOException {
    int storeId = Integer.parseInt((urlParts[STOREID_INDEX]));
    int customerId = Integer.parseInt((urlParts[CUSTOMERID_INDEX]));

    PurchaseModel newPurchase = new PurchaseModel();
    newPurchase.setStoreID(storeId);
    newPurchase.setCustomerID(customerId);
    newPurchase.setDate(urlParts[DATE_VAL_INDEX]);
    newPurchase.setPurchase(purchase);
    System.out.println("PurchaseModel made; line93 PurchaseServlet");
    // insert purchase into the DB
    String purchaseString = new Gson().toJson(newPurchase);
    KafkaPublisher kafkaPublisher = KafkaPublisher.getInstance();
    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, purchaseString);
    System.out.println("Sending through Kafka!; line98 PurchaseServlet");
    kafkaPublisher.getProducer().send(record);
    System.out.println("Kafka message sent! line 97 PurchaseServlet");
    channel.basicPublish(EXCHANGE_NAME,"", null,
        purchaseString.getBytes("UTF-8"));
  }

  /**
   * Parse request body helper method
   * @param requestBodyReader HTTP request body
   * @return toString representation of request body
   * @throws IOException I/O exception
   */
  private String parseRequestBody(BufferedReader requestBodyReader) throws IOException {
    // read in request body
    StringBuilder requestBodyBuilder = new StringBuilder();
    String line;
    while ((line = requestBodyReader.readLine()) != null) {
      requestBodyBuilder.append(line);
    }
    return requestBodyBuilder.toString();
  }

  /**
   * Validate purchase data helper method
   * @param purchase PurchaseModel object
   * @return boolean if purchase data is valid or not
   */
  private boolean validatePurchase(Purchase purchase) {
    for (PurchaseItem item : purchase.getItems()) {
      if (item.getItemID() == null || item.getItemID().isEmpty()
          || item.getNumberOfItems() < 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Verify Date helper method
   * @param dateString String date object
   * @return boolean if date data is valid or not
   */
  private boolean verifyDate(String dateString) {
    // verify date
    try {
      LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.BASIC_ISO_DATE);
    } catch (DateTimeException e) {
      return false;
    }
    return true;
  }

  /**
   * Verify CustomerId helper method
   * @param customerIdString CustomerId
   * @return boolean whether customer id is valid
   */
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

  /**
   * Verify store id helper method
   * @param storeIdString store id string
   * @return boolean if store id is valid
   */
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

  /**
   * URL validation helper method
   * @param urlParts Parts of the URL
   * @return boolean based on if URL is valid
   */
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