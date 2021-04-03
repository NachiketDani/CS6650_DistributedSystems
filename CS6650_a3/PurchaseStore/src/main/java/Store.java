import java.util.HashMap;
import java.util.Map;
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
  }

}
