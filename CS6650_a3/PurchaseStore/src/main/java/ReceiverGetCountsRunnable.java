import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runnable thread that takes individual purchases out of RabbitMQ channel and saves them into Store
 */
public class ReceiverGetCountsRunnable implements Runnable {
  private final String REQUEST_QUEUE_NAME = "rpc_queue";
  private Connection connection;
  private Store store;


  /**
   * Constructor for the Runnable
   * @param connection
   */
  public ReceiverGetCountsRunnable(Connection connection) {
    this.connection = connection;
    this.store = Store.getInstance();
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    try (Channel channel = connection.createChannel()) {
      channel.queueDeclare(REQUEST_QUEUE_NAME, false, false, false, null);
      channel.queuePurge(REQUEST_QUEUE_NAME);
      channel.basicQos(1);
      Object monitor = new Object();
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new AMQP.BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .contentType("application/json")
            .build();
        String response = "";
        try {
          String message = new String(delivery.getBody(), "UTF-8");
          String[] messageParts = message.split(",");

          // Query for top item sales for specific Store
          if (messageParts[0].equals("Store")) {
//            System.out.println("ReceiverGetCountsRunnable line60: Before calling function for heap for Store query");
            OutputItemCountsForStore results =
                store.getTopNItemsForStore(Integer.parseInt(messageParts[1]),
                    Integer.parseInt(messageParts[2]));
//            System.out.println("Result line64 = "+ results.getResult());
            response = new Gson().toJson(results);
//            System.out.println("String response line 65:" + response);

            // Query for specific item sales per Store
          } else if (messageParts[0].equals("Item")) {
//            System.out.println("ReceiverGetCountsRunnable line68: Before calling function for heap for Item query");
            OutputTopStoresForItem results =
                store.getTopNStoresForItem(messageParts[1], Integer.parseInt(messageParts[2]));
//            System.out.println("Result line72 = "+ results.getResult());
            response = new Gson().toJson(results);

          } else {
            response = "Query not supported";
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
//          System.out.println("Here is the response, Line79 GetRunnable" + response);
          channel.basicPublish("", delivery.getProperties().getReplyTo(),
              replyProps, response.getBytes("UTF-8"));
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          // RabbitMQ consumer worker thread notifies the RPC server owner thread
          synchronized (monitor) {
            monitor.notify();
          }
        }
      };
      channel.basicConsume(REQUEST_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
      }));
      // Wait and be prepared to consume the message from RPC client.
      while (true) {
        synchronized (monitor) {
          try {
//            System.out.println("Waiting at line 95");
            monitor.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (IOException | TimeoutException e) {
      Logger.getLogger(ReceiverGetCountsRunnable.class.getName()).log(Level.SEVERE, null, e);
    }
  }
}
