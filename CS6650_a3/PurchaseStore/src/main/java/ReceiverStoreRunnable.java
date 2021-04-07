import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import com.google.gson.Gson;

/**
 * Runnable thread that takes individual purchases out of RabbitMQ channel and saves them into Store
 */
public class ReceiverStoreRunnable implements Runnable {
  private static final String EXCHANGE_NAME = "supermarket";
  private static final String EXCHANGE_TYPE = "fanout";
  private Connection connection;
  private String purchasesQueue;
  private Store store;


  /**
   * Constructor for the Runnable
   * @param connection
   * @param purchasesQueue
   */
  public ReceiverStoreRunnable(Connection connection, String purchasesQueue) {
    this.connection = connection;
    this.purchasesQueue = purchasesQueue;
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

  /**
   * Method to process JSON purchase string to purchase items and pass them into the Store
   * @param purchase : purchase in JSON string format
   */
  private void storePurchase(String purchase) {
//    System.out.println(purchase);
    PurchaseModel purchaseModel = new Gson().fromJson(purchase, PurchaseModel.class);
//    System.out.println("line 69");
//    System.out.println(purchaseModel);

    String purchaseString = purchaseModel.getPurchase();
//    System.out.println("line 73");
//    System.out.println(purchaseString);

    Purchase purchaseInstance = new Gson().fromJson(purchaseString, Purchase.class);
//    System.out.println("line 77");
//    System.out.println(purchaseInstance);


    Integer storeID = purchaseModel.getStoreID();

    // Add to hashmap that will help tally popular items within each Store
    for (int i = 0; i < purchaseInstance.getItems().size(); i++) {
      PurchaseItem purchaseItem = purchaseInstance.getItems().get(i);
//      System.out.println("line74 Receiver StoreRunnable");
//      System.out.println(purchaseItem);
      this.store.addToCountTopItemsAtStore(storeID, purchaseItem.getItemID(),
          purchaseItem.getNumberOfItems());
//      System.out.println("Line78 ReceiverStoreRunnable");
//      System.out.println(purchaseItem.getNumberOfItems());
      // Add to hashmap that help tally top stores for each Item
      this.store.addToCountStoresByItem(purchaseItem.getItemID(),
          storeID, purchaseItem.getNumberOfItems());

    }
  }
}
