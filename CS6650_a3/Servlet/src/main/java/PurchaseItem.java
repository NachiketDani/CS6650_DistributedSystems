import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * PurchaseItem class
 */
public class PurchaseItem implements Serializable {
  @SerializedName("ItemID")
  private String itemID;
  @SerializedName("numberOfItems")
  private int numberOfItems;

  /**
   * Empty constructor
   */
  public PurchaseItem(){ }

  /**
   * Constructor
   * @param itemID Item id being purchased
   * @param numberOfItems quantity of item
   */
  public PurchaseItem(String itemID, int numberOfItems) {
    this.itemID = itemID;
    this.numberOfItems = numberOfItems;
  }

  /**
   * Getter method for itemId
   * @return Itemid
   */
  public String getItemID() {
    return itemID;
  }

  /**
   * Setter method for itemId
   * @param itemID itemId
   */
  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  /**
   * Getter method for number of Items
   * @return number of items
   */
  public int getNumberOfItems() {
    return numberOfItems;
  }

  /**
   * Setter method for number of items
   * @param numberOfItems number of items being purchased
   */
  public void setNumberOfItems(int numberOfItems) {
    this.numberOfItems = numberOfItems;
  }

  /**
   * To string method for the item being purchased
   * @return String representation of item being purchased
   */
  @Override
  public String toString() {
    return "PurchaseItem{" +
        "itemID='" + itemID + '\'' +
        ", numberOfItems=" + numberOfItems +
        '}';
  }
}
