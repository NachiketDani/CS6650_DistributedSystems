import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class Store
 */
public class Store {
  private static Store store = new Store();
  private Map<Integer, Map<String, Integer>> countTopItemsAtStore = new ConcurrentHashMap<>();
  private Map<String, Map<Integer, Integer>> countStoresByItem = new ConcurrentHashMap<>();

  /**
   * Private Constructor
   */
  private Store() { }

  /**
   * Getter method for Store instance
   * @return
   */
  public static Store getInstance(){
    return store;
  }

  /**
   * Add items to a Hashmap that has StoreID as key, and a Item,Quantity map as value
   * @param storeId store at which purchase is being made
   * @param itemId item identifier
   * @param count quantity of items purchased
   */
  public void addToCountTopItemsAtStore(Integer storeId, String itemId, Integer count) {
    if (countTopItemsAtStore.containsKey(storeId)) {
      if (countTopItemsAtStore.get(storeId).containsKey(itemId)) {
        Integer oldCount = countTopItemsAtStore.get(storeId).get(itemId);
        countTopItemsAtStore.get(storeId).put(itemId, oldCount+count);
      } else {
        countTopItemsAtStore.get(storeId).put(itemId,count);
      }
    } else {
      HashMap<String, Integer> newItem = new HashMap<>();
      newItem.put(itemId, count);
      countTopItemsAtStore.put(storeId, newItem);
    }
//    System.out.println(countTopItemsAtStore);
  }

  public void addToCountStoresByItem(String itemId, Integer storeId, Integer count) {
    if (countStoresByItem.containsKey(itemId)) {
      if (countStoresByItem.get(itemId).containsKey(storeId)) {
        Integer oldCount = countStoresByItem.get(itemId).get(storeId);
        countStoresByItem.get(itemId).put(storeId, oldCount+count);
      } else {
        countStoresByItem.get(itemId).put(storeId,count);
      }
    } else {
      HashMap<Integer, Integer> newStoreCounts = new HashMap<>();
      newStoreCounts.put(storeId, count);
      countStoresByItem.put(itemId, newStoreCounts);
    }
//    System.out.println(countStoresByItem);
  }

  public OutputItemCountsForStore getTopNItemsForStore(int storeId, int n) {
    PriorityQueue<ItemCountsForStore> sortedItemCountForStores = new PriorityQueue<>();
    OutputItemCountsForStore result = new OutputItemCountsForStore();

    if (!countTopItemsAtStore.containsKey(storeId)) {
      System.out.println("StoreId not found");
      return result;
    }

    Map<String, Integer> itemsPurchasedAtStore = countTopItemsAtStore.get(storeId);
//    System.out.println(itemsPurchasedAtStore);
//    System.out.println("line 75 Store.java, Within the function to get Items in store");
    for (String itemId : itemsPurchasedAtStore.keySet()) {
      ItemCountsForStore itemCount = new ItemCountsForStore
          (itemId, itemsPurchasedAtStore.get(itemId));
      sortedItemCountForStores.add(itemCount);

      // Keep heap size n
      if (sortedItemCountForStores.size() > n) {
        sortedItemCountForStores.poll();
      }
    }

    // Pop pairs into the result to sort in descending
    while (!sortedItemCountForStores.isEmpty()) {
      result.getResult().add(sortedItemCountForStores.poll());
    }
//    System.out.println("Line 123: Store- Result for get Stores for Item query" + result);
    return result;
  }

  public OutputTopStoresForItem getTopNStoresForItem(String itemId, int n) {
    PriorityQueue<StoreCountsForItem> sortedStoreCountsForItem = new PriorityQueue<>();
    OutputTopStoresForItem result = new OutputTopStoresForItem();

    if (!countStoresByItem.containsKey(itemId)) {
      System.out.println("ItemId not found");
      return result;

    }

    Map<Integer, Integer> storeCountsForItem = countStoresByItem.get(itemId);
//    System.out.println(storeCountsForItem);
//    System.out.println("line 106 Store.java, in function to get N stores for item");
    for (Integer storeId : storeCountsForItem.keySet()) {
      StoreCountsForItem storeSalesCount = new StoreCountsForItem
          (storeId, storeCountsForItem.get(storeId));
      sortedStoreCountsForItem.add(storeSalesCount);

      // Keep heap size n
      if (sortedStoreCountsForItem.size() > n) {
        sortedStoreCountsForItem.poll();
      }
    }

    // Pop pairs into the result to sort in descending
    while (!sortedStoreCountsForItem.isEmpty()) {
      result.getResult().add(sortedStoreCountsForItem.poll());
    }
//    System.out.println("Line 123: Store- Result for get Stores for Item query" + result);
    return result;
  }


}
