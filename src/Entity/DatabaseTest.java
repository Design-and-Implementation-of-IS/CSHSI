package Entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Database connection test
            System.out.println("Trying to connect to database...");
            conn = DriverManager.getConnection(Consts.CONN_STR);
            System.out.println("SUCCESS: Database connection established!");
            
            // Create statement
            stmt = conn.createStatement();
            
            // Check existing tables in database
            System.out.println("\nExisting tables in database:");
            ResultSet tables = conn.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                System.out.println("- " + tables.getString("TABLE_NAME"));
            }
            tables.close();
            
            // Simple Items list - Serial Number and Name only
            System.out.println("\n" + "=".repeat(50));
            System.out.println("           DENTAL ITEMS LIST");
            System.out.println("=".repeat(50));
            
            // קודם כל בוא נבדוק מה השמות האמיתיים של העמודות
            System.out.println("Column names in Items table:");
            ResultSet columns = stmt.executeQuery("SELECT TOP 1 * FROM Items");
            var metaData = columns.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("- " + metaData.getColumnName(i));
            }
            columns.close();
            
            // עכשיו נשתמש בשאילתא עם * כדי להימנע מבעיות שמות
            ResultSet rs = stmt.executeQuery("SELECT * FROM Items ORDER BY [itemSerialNum]");  // תיקון השם!
            
            // Print header
            System.out.printf("%-8s | %-25s | %-12s%n", "Serial", "Item Name", "Category");
            System.out.println("-".repeat(48));
            
            // Print data rows using column index for safety
            int count = 0;
            while (rs.next()) {
                count++;
                // נשתמש באינדקסים של עמודות במקום בשמות
                System.out.printf("%-8s | %-25s | %-12s%n", 
                    rs.getString(1),   // מספר סידורי (עמודה ראשונה)
                    rs.getString(2),   // שם פריט (עמודה שניה)
                    rs.getString(5));  // קטגוריה (עמודה חמישית לפי הסדר בתמונה)
            }
            rs.close();
            
            System.out.println("-".repeat(48));
            System.out.println("Total: " + count + " items");
            System.out.println("=".repeat(50));
            
        } catch (SQLException e) {
            System.err.println("ERROR: Database connection failed:");
            System.err.println("Error code: " + e.getErrorCode());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connections
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("\nSUCCESS: Connection closed successfully");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}