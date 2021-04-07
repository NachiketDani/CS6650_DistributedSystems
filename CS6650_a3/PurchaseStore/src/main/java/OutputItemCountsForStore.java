import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that defines output structure for the Items sold in specific Store query
 */
public class OutputItemCountsForStore implements Serializable {
  private List<ItemCountsForStore> result;

  /**
   * Constructor
   */
  public OutputItemCountsForStore() {
    this.result = new ArrayList<>();
  }

  /**
   * Getter method for the store attribute
   * @return
   */
  public List<ItemCountsForStore> getResult() {
    return result;
  }

  /**
   * Setter method for the store attribute
   * @param result store list of ItemCountsForStore
   */
  public void setStore(List<ItemCountsForStore> result) {
    this.result = result;
  }
}
