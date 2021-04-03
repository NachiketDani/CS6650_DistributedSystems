import java.io.Serializable;

/**
 * Class that represents separate Purchases for specific stores and customers
 * Representative of data as stored in the NoSQL storage
 */
public class PurchaseModel implements Serializable {
  private int storeID;
  private int customerID;
  private String date;
  private String purchase;

  /**
   * Empty constructor for the PurchaseModel
   */
  public PurchaseModel(){ }

  /**
   * Getter method for the storeId
   * @return StoreId for the purchase instance
   */
  public int getStoreID() {
    return storeID;
  }

  /**
   * Setter method for the store id
   * @param storeID For the purchase instance
   */
  public void setStoreID(int storeID) {
    this.storeID = storeID;
  }

  /**
   * Getter method for the CustomerId
   * @return CustomerID for the purchase instance
   */
  public int getCustomerID() {
    return customerID;
  }

  /**
   * Setter method for the CustomerId
   * @param customerID for the purchase instance
   */
  public void setCustomerID(int customerID) {
    this.customerID = customerID;
  }

  /**
   * Getter method for the purchase date
   * @return date of purchase instance
   */
  public String getDate() {
    return date;
  }

  /**
   * Setter method for the date
   * @param date Date for the purchase instance
   */
  public void setDate(String date) {
    this.date = date;
  }

  /**
   * Getter method for the Purchased items
   * @return JSON string representation of the purchased items
   */
  public String getPurchase() {
    return purchase;
  }

  /**
   * Setter method for the Purchased items
   * @param purchase items purchased in the purchase instance
   */
  public void setPurchase(String purchase) {
    this.purchase = purchase;
  }
}
