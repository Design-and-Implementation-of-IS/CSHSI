package Control;

import Entity.Consts;
import Entity.Item;
import Entity.ItemCategory;
import Entity.Supplier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    // החלפה ל-SQL ישיר: Insert Item
    public boolean insertItem(Item item) {
        String sql = "INSERT INTO Items (itemSerialNum,itemName,itemDescription,expirationDate,category,supplierId) VALUES (?,?,?,?,?,?)";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, item.getSerialNumber());
                ps.setString(2, item.getName());
                ps.setString(3, item.getDescription());
                ps.setDate(4, new java.sql.Date(item.getExpirationDate().getTime()));
                ps.setString(5, item.getCategory());
                ps.setInt(6, item.getSupplierId());
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    // החלפה ל-SQL ישיר: Update Item
    public boolean updateItem(Item item) {
        String sql = "UPDATE Items SET itemName=?, itemDescription=?, expirationDate=?, category=?, supplierId=? WHERE itemSerialNum=?";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, item.getName());
                ps.setString(2, item.getDescription());
                ps.setDate(3, new java.sql.Date(item.getExpirationDate().getTime()));
                ps.setString(4, item.getCategory());
                ps.setInt(5, item.getSupplierId());
                ps.setString(6, item.getSerialNumber());
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    // החלפה ל-SQL ישיר: Delete Item
    public boolean deleteItem(String serialNumber) {
        String sql = "DELETE FROM Items WHERE itemSerialNum=?";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, serialNumber);
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    // החלפה ל-SQL ישיר: Insert Supplier
    public boolean insertSupplier(Supplier supplier) {
        String sql = "INSERT INTO Suppliers (supplierId,name,contactPerson,phone,email,address) VALUES (?,?,?,?,?,?)";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, supplier.getSupplierId());
                ps.setString(2, supplier.getName());
                ps.setString(3, supplier.getContactPerson());
                ps.setString(4, supplier.getPhone());
                ps.setString(5, supplier.getEmail());
                ps.setString(6, supplier.getAddress());
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    // נשאר כפי שביקשת: Update Supplier (SQL ישיר) – אין שינוי
    public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE Suppliers SET name=?, contactPerson=?, phone=?, email=?, address=? WHERE supplierId=?";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, supplier.getName());
                ps.setString(2, supplier.getContactPerson());
                ps.setString(3, supplier.getPhone());
                ps.setString(4, supplier.getEmail());
                ps.setString(5, supplier.getAddress());
                ps.setInt(6, supplier.getSupplierId());
                return ps.executeUpdate() == 1;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    // שימוש ב-Q_delete_supplier (CALLABLE) בלבד לפי בקשתך
    public boolean deleteSupplier(int supplierId) {
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 CallableStatement cs = c.prepareCall(Consts.QUERY_DELETE_SUPPLIER)) {
                cs.setInt(1, supplierId);
                cs.execute();
                return true;
            } catch (SQLException e) { e.printStackTrace(); return false; }
        } catch (ClassNotFoundException e) { e.printStackTrace(); return false; }
    }

    /**
     * מחזיר רשימת מספרים סידוריים של כל הפריטים (עבור קומבו בוקס)
     * SELECT itemSerialNum FROM Items ORDER BY itemSerialNum
     */
    public List<String> getAllItemSerials() {
        List<String> result = new ArrayList<>();
        String sql = "SELECT itemSerialNum FROM Items ORDER BY itemSerialNum";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String s = rs.getString(1);
                    if (s != null) result.add(s);
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return result;
    }

    /**
     * מחזיר פריט לפי מספר סידורי או null אם לא נמצא.
     * SELECT itemSerialNum,itemName,itemDescription,expirationDate,category,supplierId FROM Items WHERE itemSerialNum=?
     */
    public Item getItemBySerial(String serial) {
        if (serial == null || serial.isEmpty()) return null;
        String sql = "SELECT itemSerialNum,itemName,itemDescription,expirationDate,category,supplierId FROM Items WHERE itemSerialNum=?";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, serial);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String itemSerial = rs.getString("itemSerialNum");
                        String name = rs.getString("itemName");
                        String desc = rs.getString("itemDescription");
                        java.util.Date exp = rs.getDate("expirationDate");
                        String catStr = rs.getString("category");
                        int supplierId = rs.getInt("supplierId");
                        ItemCategory cat = null;
                        if (catStr != null) {
                            try { cat = ItemCategory.valueOf(catStr); }
                            catch (IllegalArgumentException ex) { /* TODO: אם שמות הקטגוריות בבסיס הנתונים שונים מה-enum */ }
                        }
                        if (cat == null) cat = ItemCategory.Tools; // ברירת מחדל זהירה
                        return new Item(itemSerial, name, desc, exp, cat, supplierId);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return null;
    }

    /**
     * מחזיר רשימת מזהי ספקים (עבור קומבו בוקס במסך ספקים)
     * SELECT supplierId FROM Suppliers ORDER BY supplierId
     */
    public List<Integer> getAllSupplierIds() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT supplierId FROM Suppliers ORDER BY supplierId";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt(1));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return list;
    }

    /**
     * מחזיר אובייקט Supplier לפי מזהה או null אם לא נמצא.
     * SELECT supplierId,name,contactPerson,phone,email,address FROM Suppliers WHERE supplierId=?
     */
    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT supplierId,name,contactPerson,phone,email,address FROM Suppliers WHERE supplierId=?";
        try {
            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            try (Connection c = DriverManager.getConnection(Consts.CONN_STR);
                 PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, supplierId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("supplierId");
                        String name = rs.getString("name");
                        String contact = rs.getString("contactPerson");
                        String phone = rs.getString("phone");
                        String email = rs.getString("email");
                        String address = rs.getString("address");
                        return new Supplier(id, name, contact, phone, email, address);
                    }
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return null;
    }

    // הדפסת פרטי ספק (לבדיקות)
    private void printSupplier(int id) {
        String sql = "SELECT supplierId,name,contactPerson,phone,email,address FROM Suppliers WHERE supplierId=?";
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Supplier[" + rs.getInt(1) + "] -> " +
                            rs.getString(2) + " | " + rs.getString(3) + " | " +
                            rs.getString(4) + " | " + rs.getString(5) + " | " + rs.getString(6));
                } else System.out.println("Supplier " + id + " not found.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // הדפסת פרטי פריט (לבדיקות)
    private void printItem(String serial) {
        String sql = "SELECT itemSerialNum,itemName,itemDescription,expirationDate,category,supplierId FROM Items WHERE itemSerialNum=?";
        try (Connection conn = DriverManager.getConnection(Consts.CONN_STR);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Item[" + rs.getString(1) + "] -> " +
                            rs.getString(2) + " | " + rs.getString(3) + " | " +
                            rs.getDate(4) + " | " + rs.getString(5) + " | supp=" + rs.getInt(6));
                } else System.out.println("Item " + serial + " not found.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // MAIN בדיקות נפרדות – הפעל TRUE רק למה שרוצים לבדוק
    public static void main(String[] args) {
        InventoryManager m = new InventoryManager();

        boolean TEST_INSERT_SUPPLIER = true;
        boolean TEST_UPDATE_SUPPLIER = true;      
        boolean TEST_DELETE_SUPPLIER = true;
        boolean TEST_INSERT_ITEM = true;
        boolean TEST_UPDATE_ITEM = true;
        boolean TEST_DELETE_ITEM = true;

        // 1. Insert Supplier
        if (TEST_INSERT_SUPPLIER) {
            Supplier ns = new Supplier(99, "Temp Supplier", "Temp Person", "050-1111111",
                    "temp@supp.com", "Temp Address 1");
            System.out.println("Insert Supplier(99): " + (m.insertSupplier(ns) ? "SUCCESS" : "FAIL"));
            m.printSupplier(99);
        }

         // 2. Update Supplier (id=2)
        if (TEST_UPDATE_SUPPLIER) {
            int id = 2;
            System.out.println("Before update supplier " + id + ":");
            m.printSupplier(id);
            Supplier updated = new Supplier(id,
                    "SmileTools International",
                    "Alice Levi",
                    "234-5678901",
                    "alice@smiletools.com",
                    "456 Park Ave, Suite 10");
            System.out.println("Update Supplier ID=" + id + ": " + (m.updateSupplier(updated) ? "SUCCESS" : "FAIL"));
            System.out.println("After update supplier " + id + ":");
            m.printSupplier(id);
        }

         // 3. Delete Supplier (יש להשתמש ב-Q_delete_supplier)
        if (TEST_DELETE_SUPPLIER) {
            System.out.println("Delete Supplier(99): " + (m.deleteSupplier(99) ? "SUCCESS" : "FAIL"));
        }

        // 4. Insert Item
        if (TEST_INSERT_ITEM) {
            Item it = new Item("1016", "Test Item", "Temporary item", new java.util.Date(),
                    ItemCategory.Tools, 1);
            System.out.println("Insert Item 1016: " + (m.insertItem(it) ? "SUCCESS" : "FAIL"));
            m.printItem("1016");
        }

        // 5. Update Item
        if (TEST_UPDATE_ITEM) {
            // נניח שקיים פריט 1001
            m.printItem("1001");
            Item up = new Item("1001", "Dental Mirror PRO", "Mirror used in oral examination (PRO)",
                    new java.util.Date(), ItemCategory.Tools, 1);
            System.out.println("Update Item 1001: " + (m.updateItem(up) ? "SUCCESS" : "FAIL"));
            m.printItem("1001");
        }

        // 6. Delete Item
        if (TEST_DELETE_ITEM) {
            System.out.println("Delete Item 1016: " + (m.deleteItem("1016") ? "SUCCESS" : "FAIL"));
            m.printItem("1016");
        }
    }
}