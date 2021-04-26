import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.PurchaseApi;
import io.swagger.client.model.Purchase;
import io.swagger.client.model.PurchaseItems;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Store (thread) Runnable Class
 */
public class StoreThreadRunnable implements Runnable {
  private final int STOREHOURS = 9;
  private final int PHASE2TRIGGER = 2;
  private final int PHASE3TRIGGER = 5;
  private int storeID;
  private int customersPerStore;
  private int maxItemId;
  private int numPurchases;
  private int itemsPerPurchase;
  private int date;
  private String strDate;


  private CountDownLatch phase2Latch;
  private CountDownLatch phase3Latch;
  private LatencyConsumer latencyConsumer;
  private FileWriterConsumer fileWriterConsumer;
  private ApiClient shop;
  private PurchaseApi apiInstance;

  /**
   * Constructor for the Runnable thread
   * @param storeID Thread count/ Store number being processed
   * @param date Date of purchase
   * @param customersPerStore Number of customers per store, also forms customerId
   * @param maxItemId Total number of items available
   * @param numPurchases number of purchases per store per hour
   * @param itemsPerPurchase Number of items per purchase
   * @param phase2Latch latch trigger for central stores to begin processing
   * @param phase3Latch latch trigger for west stores to begin processing
   * @param latencyConsumer Helper class uses Producer-Consumer to tracker completed server requests
   */
  public StoreThreadRunnable(int storeID, String shopPath, int date, int customersPerStore, int maxItemId,
      int numPurchases, int itemsPerPurchase, CountDownLatch phase2Latch,
      CountDownLatch phase3Latch, LatencyConsumer latencyConsumer,
      FileWriterConsumer fileWriterConsumer) {
    this.storeID = storeID;
    this.date = date;
    this.strDate = String.valueOf(date);
    this.customersPerStore = customersPerStore;
    this.maxItemId = maxItemId;
    this.numPurchases = numPurchases;
    this.itemsPerPurchase = itemsPerPurchase;
    this.phase2Latch = phase2Latch;
    this.phase3Latch = phase3Latch;

    this.latencyConsumer = latencyConsumer;
    this.fileWriterConsumer = fileWriterConsumer;

    this.shop = new ApiClient();
    shop.setBasePath(shopPath);
    shop.setConnectTimeout(1000000);
    apiInstance = new PurchaseApi(shop);
  }

  /**
   * Make purchase HTTP request
   * @param customerId Id of the customer
   */
  public void makePurchasePost(int customerId) {
    Purchase purchaseInstance = new Purchase();

    for (int i = 0; i < itemsPerPurchase; i++) {
      String itemId = String.valueOf(ThreadLocalRandom.current().nextInt(this.maxItemId) + 1);
      PurchaseItems purchaseItems = new PurchaseItems();
      purchaseItems.setItemID(itemId);
      purchaseItems.setNumberOfItems(1);
      purchaseInstance.addItemsItem(purchaseItems);
    }

    try {
      ResponseObject responseObject;
      // Record start time
      long reqStart = System.currentTimeMillis();
      ApiResponse<Void> resp = apiInstance.newPurchaseWithHttpInfo(purchaseInstance,
          this.storeID, customerId, this.strDate);
      // Record end time
      long reqEnd = System.currentTimeMillis();
      // Create a response object for Consumer threads
      responseObject = new ResponseObject(reqStart, reqEnd, resp.getStatusCode(),
          "POST");
      if (resp.getStatusCode() != 201 && resp.getStatusCode() != 200) {
        System.err.println(resp.getStatusCode());
      }
      // Add response object to the buffer feeding into consumer threads
      latencyConsumer.queueToBuffer(responseObject);
      fileWriterConsumer.queueToWritingBuffer(responseObject);
    } catch (
        ApiException | InterruptedException e) {
      System.err.println("Exception when calling PurchaseApi#newPurchase");
      e.printStackTrace();
    }
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
    for (int hour = 0; hour < STOREHOURS; hour++) {
      for (int j = 0; j < numPurchases; j++) {
        // Create CustomerId, pass to purchase posts

        int customerId = ThreadLocalRandom.current().nextInt(this.customersPerStore) + storeID*1000;
        makePurchasePost(customerId);
      }
      if (hour == PHASE2TRIGGER) {
        phase2Latch.countDown();
      }
      if (hour == PHASE3TRIGGER) {
        phase3Latch.countDown();
      }
    }
  }
}