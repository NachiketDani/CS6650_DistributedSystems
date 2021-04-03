import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

/**
 * TopStoresForItemServlet: Servlet for get request for
 * top10 stores by sale count for specific ItemId
 */
@WebServlet(name = "TopStoresForItemServlet", value = "/TopStoresForItemServlet")
public class TopStoresForItemServlet extends HttpServlet {
  private static final String TOP_SELLING_STORES = "10";
  private static final String REQUEST_QUEUE_NAME = "rpc_queue";
  private final BlockingQueue<String> responses = new ArrayBlockingQueue<>(1);
  private Channel channel;
  private static final String HOST = "localhost";
//      System.getProperty("RABBITMQ_HOST");
//  private static final String USERNAME = System.getProperty("RABBITMQ_USERNAME");
//  private static final String PASSWORD = System.getProperty("RABBITMQ_PASSWORD");


  /**
   * Init method for the Servlet
   * @throws ServletException servlet init exception
   */
  @Override
  public void init() throws ServletException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(HOST);
//    factory.setUsername(USERNAME);
//    factory.setPassword(PASSWORD);
    try {
      Connection connection = factory.newConnection();
      channel = connection.createChannel();
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
      System.err.println("Unable to retrieve a channel!");
    }
  }

  /**
   * Get method
   * @param request Request HTTP parameters
   * @param response Response HTTP parameters
   * @throws ServletException Servlet init exception
   * @throws IOException I/O Exception
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
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
      response.getWriter().write("Invalid Item ID");
      return;
    }
    // put request on queue
    try {
      final String correlationId = UUID.randomUUID().toString();
      // TODO: Should this be in the init method and name constant for thread?
      String replyQueueName = channel.queueDeclare().getQueue();
      // build the message properties
      AMQP.BasicProperties props = new AMQP.BasicProperties
          .Builder()
          .correlationId(correlationId)
          .replyTo(replyQueueName)
          .contentType("application/json")
          .build();

      // build RPC request object, convert to GSON
      String queryItem = "Item"+ urlParts[1] + TOP_SELLING_STORES;
      String messageForItem = new Gson().toJson(queryItem);

      // enqueue message
      channel.basicPublish("", REQUEST_QUEUE_NAME, props,
          messageForItem.getBytes("UTF-8"));
      // wait for response
      String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
        if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
          responses.offer(new String(delivery.getBody(), "UTF-8"));
        }
      }, consumerTag -> {
      });
      // grab the response and return the result
      String result = responses.take();
      channel.basicCancel(ctag);
      response.setStatus((HttpServletResponse.SC_OK));
      response.getWriter().write(result);
    } catch (Exception e) {
      response.setStatus((HttpServletResponse.SC_NOT_FOUND));
      response.getWriter().write("Request was unable to be processed.");
    }
  }

  /**
   * ItemId validation helper method
   * @param itemIdString itemId
   * @return boolean based on whether ItemID is valid
   */
  private boolean verifyItemId(String itemIdString) {
    try {
      int itemId = Integer.parseInt(itemIdString);
      if (itemId < 0) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * URL Validation helper method
   * @param urlParts parts of the URL
   * @return boolean based on whether the URL is valid or not
   */
  private boolean isUrlValid(String[] urlParts) {
    // urlPath = "/items/top10/item_id
    // urlParts = [ , item_id]
    return verifyItemId(urlParts[1]);
  }

}
