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

  public String getItemID() {
    return itemID;
  }

  public void setItemID(String itemID) {
    this.itemID = itemID;
  }

  public int getNumberOfItems() {
    return numberOfItems;
  }

  public void setNumberOfItems(int numberOfItems) {
    this.numberOfItems = numberOfItems;
  }
}
