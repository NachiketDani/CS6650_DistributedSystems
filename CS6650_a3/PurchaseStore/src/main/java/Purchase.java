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

  public List<PurchaseItem> getItems() {
    return items;
  }

  public void setItems(List<PurchaseItem> items) {
    this.items = items;
  }

  @Override
  public String toString() {
    return "Purchase{" +
        "items=" + items +
        '}';
  }
}
