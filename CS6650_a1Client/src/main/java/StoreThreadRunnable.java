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

//  private static String shopPath = "http://localhost:8080/CS6650_a1_war_exploded/";

  private CountDownLatch phase2Latch;
  private CountDownLatch phase3Latch;
  private PurchaseTracker purchaseTracker;
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
   * @param purchaseTracker Helper class uses Producer-Consumer to tracker completed server requests
   */
  public StoreThreadRunnable(int storeID, String shopPath, int date, int customersPerStore, int maxItemId,
      int numPurchases, int itemsPerPurchase, CountDownLatch phase2Latch,
      CountDownLatch phase3Latch, PurchaseTracker purchaseTracker) {
    this.storeID = storeID;
    this.date = date;
    this.strDate = String.valueOf(date);
    this.customersPerStore = customersPerStore;
    this.maxItemId = maxItemId;
    this.numPurchases = numPurchases;
    this.itemsPerPurchase = itemsPerPurchase;
    this.phase2Latch = phase2Latch;
    this.phase3Latch = phase3Latch;

    this.purchaseTracker = purchaseTracker;

    this.shop = new ApiClient();
    shop.setBasePath(shopPath);
    apiInstance = new PurchaseApi(shop);
  }

  /**
   * Make purchase HTTP request
   * @param customerId
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
      ApiResponse<Void> resp = apiInstance.newPurchaseWithHttpInfo(purchaseInstance,
          storeID, customerId, strDate);
//      System.out.println(resp.getStatusCode());
      if (resp.getStatusCode() != 201 && resp.getStatusCode() != 200) {
        System.err.println(resp.getStatusCode());
      }
      purchaseTracker.queueToBuffer(resp.getStatusCode());
    } catch (
        ApiException e) {
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