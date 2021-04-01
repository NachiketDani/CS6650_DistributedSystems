import java.sql.*;

public class PurchaseDao {
  public boolean createPurchase(PurchaseModel newPurchase) {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    String insertQueryStatement = "INSERT INTO Purchases (storeID, customerID, purchaseDate, items) VALUES (?,?,?,?)";

    try {
      conn = DBConnector.getConnection();
      preparedStatement = conn.prepareStatement(insertQueryStatement);
      preparedStatement.setInt(1, newPurchase.getStoreID());
      preparedStatement.setInt(2, newPurchase.getCustomerID());
      preparedStatement.setString(3, newPurchase.getDate());
      preparedStatement.setString(4, newPurchase.getPurchase());

      // execute insert SQL statement
      preparedStatement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (conn != null) {
          conn.close();
        }
        if (preparedStatement != null) {
          preparedStatement.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    return true;
  }
}
