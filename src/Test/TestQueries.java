package Test;

import java.util.Date;
import Control.InventoryManager;
import Entity.Item;
import Entity.ItemCategory;
import Entity.Supplier;

public class TestQueries {

    public static void main(String[] args) {
        InventoryManager manager = new InventoryManager();

        // Test Supplier methods
        System.out.println("--- Testing Supplier Methods ---");
        Supplier supplier = new Supplier(901, "Test Supplier Inc.", "John Doe", "123-456-7890", "john.doe@test.com", "123 Test St, Testville");
        
        // 1. Test insertSupplier
        boolean insertSupplierSuccess = manager.insertSupplier(supplier);
        System.out.println("1. Insert Supplier Success: " + insertSupplierSuccess);

        // 2. Test updateSupplier
        supplier.setName("Updated Test Supplier Inc.");
        supplier.setContactPerson("Jane Doe");
        boolean updateSupplierSuccess = manager.updateSupplier(supplier);
        System.out.println("2. Update Supplier Success: " + updateSupplierSuccess);

        // Test Item methods
        System.out.println("\n--- Testing Item Methods ---");
        // Assuming a supplier with ID 901 now exists for this test
        Item item = new Item("SN-TEST-001", "Test Dental Tool", "A tool for testing.", new Date(), ItemCategory.Tools, 901);

        // 3. Test insertItem
        boolean insertItemSuccess = manager.insertItem(item);
        System.out.println("3. Insert Item Success: " + insertItemSuccess);

        // 4. Test updateItem
        item.setName("Updated Test Dental Tool");
        item.setDescription("An updated tool for testing.");
        boolean updateItemSuccess = manager.updateItem(item);
        System.out.println("4. Update Item Success: " + updateItemSuccess);

        // 5. Test deleteItem
        boolean deleteItemSuccess = manager.deleteItem(item.getSerialNumber());
        System.out.println("5. Delete Item Success: " + deleteItemSuccess);
        
        // 6. Test deleteSupplier
        boolean deleteSupplierSuccess = manager.deleteSupplier(supplier.getSupplierId());
        System.out.println("6. Delete Supplier Success: " + deleteSupplierSuccess);
    }
}
