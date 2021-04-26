import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines output structure for the top Stores where the specific item is sold
 */
public class OutputTopStoresForItem implements Serializable {
  private List<StoreCountsForItem> result;

  /**
   * Constructor
   */
  public OutputTopStoresForItem() {
    this.result = new ArrayList<>();
  }

  /**
   * Getter method for the store attribute
   * @return
   */
  public List<StoreCountsForItem> getResult() {
    return this.result;
  }

  /**
   * Setter method for the store attribute
   * @param result store list of StoreCountsForItem
   */
  public void setResult(List<StoreCountsForItem> result) {
    this.result = result;
  }
}
