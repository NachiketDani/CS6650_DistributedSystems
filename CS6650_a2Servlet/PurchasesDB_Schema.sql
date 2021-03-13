CREATE SCHEMA IF NOT EXISTS purchasesdb;
Use purchasesdb;

DROP TABLE IF EXISTS Purchases;

CREATE TABLE Purchases (
	storeID INT NOT NULL,
    customerID INT NOT NULL,
    purchaseDate VARCHAR(255),
    items JSON
    # purchaseTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    # CONSTRAINT pk_Purchases_StoreIdCustomerIdTimestamp 
		# PRIMARY KEY (storeID, customerID, purchaseTimestamp)
);
    