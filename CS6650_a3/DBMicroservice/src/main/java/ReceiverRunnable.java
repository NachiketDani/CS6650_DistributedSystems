import com.google.gson.Gson;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;

public class ReceiverRunnable implements Runnable{
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private static final boolean DURABLE = false;
  private Connection connection;
  private String purchasesQueue;
  private PurchaseDao purchaseDao;

  public ReceiverRunnable (Connection connection, String purchasesQueue) {
    this.connection = connection;
    this.purchasesQueue = purchasesQueue;
    this.purchaseDao = new PurchaseDao();
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
      final Channel channel = connection.createChannel();
      channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, DURABLE);
      channel.queueBind(this.purchasesQueue, EXCHANGE_NAME, "");
      // max one message per receiver
      channel.basicQos(1);
//      System.out.println(" [*] Waiting for purchase data. To exit press CTRL+C");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String purchase = new String(delivery.getBody(), "UTF-8");
//        System.out.println(" [x] Received '" + purchase + "'");
        makePurchase(purchase);
        channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
      };
      channel.basicConsume(purchasesQueue, false, deliverCallback, consumerTag -> { });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void makePurchase(String purchase) {
    PurchaseModel newPurchase = new Gson().fromJson(purchase, PurchaseModel.class);
//    System.out.println("ReceiverRunnable line 57: attempt to db write");
    purchaseDao.createPurchase(newPurchase);
  }
}
