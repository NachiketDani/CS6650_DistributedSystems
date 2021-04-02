import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import com.google.gson.Gson;

public class Receiver2Runnable implements Runnable {
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private Connection connection;
  private String purchasesQueue;


  public Receiver2Runnable(Connection connection, String purchasesQueue) {
    this.connection = connection;
    this.purchasesQueue = purchasesQueue;
    Store store = Store.getInstance();
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
    try {
      Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE);
      channel.queueBind(this.purchasesQueue, EXCHANGE_NAME, "");
      // max one message per receiver
      channel.basicQos(1);
      System.out.println(" [*] Waiting for purchase data for Store. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String purchase = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + purchase + "'");
        storePurchase(purchase);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      };
      channel.basicConsume(purchasesQueue, true, deliverCallback, consumerTag -> { });

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void storePurchase(String purchase) {
    PurchaseModel purchaseModel = new Gson().fromJson(purchase, PurchaseModel.class);
  }
}
