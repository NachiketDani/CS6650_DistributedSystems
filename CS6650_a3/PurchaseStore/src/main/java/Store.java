import java.util.Map;

public class Store {
  private static Store store = new Store();
  private Map<Integer, Map<String, Integer>> countTopItemsAtStore;
  private Map<String, Map<Integer, Integer>> countStoresByItem;

  /**
   * Private Constructor
   */
  private Store() { }

  public static Store getInstance(){
    return store;
  }



}
