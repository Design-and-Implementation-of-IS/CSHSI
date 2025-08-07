package Control;

import Entity.Consts;
import Entity.Item;
import Entity.ItemCategory;
import Entity.Supplier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InventoryManager {

    /**
     * Inserts a new item into the database.
     * @param item The item to insert.
     * @return true if successful, false otherwise.
     */
    public boolean insertItem(Item item) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_INSERT_ITEM)) {
                
                stmt.setString(1, item.getSerialNumber());
                stmt.setString(2, item.getName());
                stmt.setString(3, item.getDescription());
                stmt.setDate(4, new java.sql.Date(item.getExpirationDate().getTime()));
                stmt.setString(5, item.getCategory());
                stmt.setInt(6, item.getSupplierId());
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing item in the database.
     * @param item The item with updated information.
     * @return true if successful, false otherwise.
     */
    public boolean updateItem(Item item) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_UPDATE_ITEM)) {
                
                stmt.setString(1, item.getName());
                stmt.setString(2, item.getDescription());
                stmt.setDate(3, new java.sql.Date(item.getExpirationDate().getTime()));
                stmt.setString(4, item.getCategory());
                stmt.setInt(5, item.getSupplierId());
                stmt.setString(6, item.getSerialNumber()); // WHERE clause parameter
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an item from the database by its serial number.
     * @param serialNumber The serial number of the item to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteItem(String serialNumber) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_DELETE_ITEM_BY_SERIAL)) {
                
                stmt.setString(1, serialNumber);
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inserts a new supplier into the database.
     * @param supplier The supplier to insert.
     * @return true if successful, false otherwise.
     */
    public boolean insertSupplier(Supplier supplier) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_INSERT_SUPPLIER)) {
                
                stmt.setInt(1, supplier.getSupplierId());
                stmt.setString(2, supplier.getName());
                stmt.setString(3, supplier.getContactPerson());
                stmt.setString(4, supplier.getPhone());
                stmt.setString(5, supplier.getEmail());
                stmt.setString(6, supplier.getAddress());
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing supplier in the database.
     * @param supplier The supplier with updated information.
     * @return true if successful, false otherwise.
     */
    public boolean updateSupplier(Supplier supplier) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_UPDATE_SUPPLIER)) {
                
                stmt.setString(1, supplier.getName());
                stmt.setString(2, supplier.getContactPerson());
                stmt.setString(3, supplier.getPhone());
                stmt.setString(4, supplier.getEmail());
                stmt.setString(5, supplier.getAddress());
                stmt.setInt(6, supplier.getSupplierId()); // WHERE clause parameter
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a supplier from the database by its ID.
     * @param supplierId The ID of the supplier to delete.
     * @return true if successful, false otherwise.
     */
    public boolean deleteSupplier(int supplierId) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement stmt = conn.prepareCall(Consts.QUERY_DELETE_SUPPLIER)) {
                
                stmt.setInt(1, supplierId);
                
                stmt.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Main method for testing the InventoryManager functionality.
     */
    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        // Clean up before test
        manager.deleteItem("S123");
        manager.deleteSupplier(123);

        Supplier s = new Supplier(123, "Copilot Test", "Contact", "050-0000000", "copilot@example.com", "Tel Aviv");
        Item i = new Item("S123", "Toothbrush", "Desc", new java.util.Date(), ItemCategory.Consumables, 123);

        System.out.println("Insert Supplier: " + (manager.insertSupplier(s) ? "SUCCESS" : "FAIL"));
        s.setName("Updated Copilot");
        System.out.println("Update Supplier: " + (manager.updateSupplier(s) ? "SUCCESS" : "FAIL"));

        System.out.println("Insert Item: " + (manager.insertItem(i) ? "SUCCESS" : "FAIL"));
        i.setName("Updated Toothbrush");
        System.out.println("Update Item: " + (manager.updateItem(i) ? "SUCCESS" : "FAIL"));
        System.out.println("Delete Item: " + (manager.deleteItem(i.getSerialNumber()) ? "SUCCESS" : "FAIL"));

        System.out.println("Delete Supplier: " + (manager.deleteSupplier(s.getSupplierId()) ? "SUCCESS" : "FAIL"));
    }
}