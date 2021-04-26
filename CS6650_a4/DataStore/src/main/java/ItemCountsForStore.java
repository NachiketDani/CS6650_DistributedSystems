/**
 * ItemCountsForStore pair class
 * Represents a pair of ItemId and its count purchased within a specific store
 */
public class ItemCountsForStore implements Comparable<ItemCountsForStore>{
  private String itemId;
  private Integer count;

  /**
   * Constructor for Item counts under each store
   * @param itemId : ItemId
   * @param count : count of the specific item
   */
  public ItemCountsForStore(String itemId, Integer count) {
    this.itemId = itemId;
    this.count = count;
  }

  /**
   * Getter method for itemId
   * @return ItemId
   */
  public String getItemId() {
    return itemId;
  }

  /**
   * Getter method for the count
   * @return count of the item
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
  public int compareTo(ItemCountsForStore o) {
    return this.getCount() - o.getCount();
  }
}
