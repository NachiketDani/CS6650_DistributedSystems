/**
 * StoreCountsForItem pair class
 * Represents a pair of StoreId and count of a specific ItemId that were purchased at that store
 */
public class StoreCountsForItem implements Comparable<StoreCountsForItem>{
  private Integer storeId;
  private Integer count;

  /**
   * Constructor for Item counts under each store
   * @param storeId : StoreId
   * @param count : count of the specific item sold at that storeId
   */
  public StoreCountsForItem(Integer storeId, Integer count) {
    this.storeId = storeId;
    this.count = count;
  }

  /**
   * Getter method for storeId
   * @return StoreId
   */
  public Integer getStoreId() {
    return storeId;
  }

  /**
   * Getter method for the count
   * @return count of the item sold at the specific store with the storeId
   */
  public Integer getCount() {
    return count;
  }

  /**
   * CompareTo method
   * @param o
   * @return +ve: Former greater than later(o)
   *    * -ve: Later (o) greater than former
   *    * 0: Equal
   */
  @Override
  public int compareTo(StoreCountsForItem o) {
    return this.getCount() - o.getCount();
  }
}
