import java.io.Serializable;

public class PurchaseModel implements Serializable {
  private int storeID;
  private int customerID;
  private String date;
  private String purchase;

  public PurchaseModel(){ }

  public int getStoreID() {
    return storeID;
  }

  public void setStoreID(int storeID) {
    this.storeID = storeID;
  }

  public int getCustomerID() {
    return customerID;
  }

  public void setCustomerID(int customerID) {
    this.customerID = customerID;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getPurchase() {
    return purchase;
  }

  public void setPurchase(String purchase) {
    this.purchase = purchase;
  }
}
