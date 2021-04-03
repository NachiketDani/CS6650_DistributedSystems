import java.io.Serializable;
import java.util.List;

/**
 * Purchase Class consisting of purchased items
 */
public class Purchase implements Serializable{
  private List<PurchaseItem> items;

  /**
   * Empty constructor
   */
  public Purchase(){ }

  /**
   * Class constructor
   * @param items items being purchased
   */
  public Purchase(List<PurchaseItem>items) {
    this.items = items;
  }

  /**
   * Getter method for Items list within purchase
   * @return List of items being purchased
   */
  public List<PurchaseItem> getItems() {
    return items;
  }

  /**
   * Setter method for items being purchased
   * @param items
   */
  public void setItems(List<PurchaseItem> items) {
    this.items = items;
  }

  /**
   * To string method for Items being purchased
   * @return String representation for the items being purchased
   */
  @Override
  public String toString() {
    return "Purchase{" +
        "items=" + items +
        '}';
  }
}
