# DentalCare Inventory Management System

A comprehensive Java desktop application for managing dental supplies inventory and supplier information.

## Project Structure

The application follows a three-tier architecture:

### Entity Package
Contains data models representing core business objects:
- `Item`: Represents inventory items with properties like serial number, name, description, expiration date, category, and supplier ID
- `ItemCategory`: Enum defining item categories (Tools, Materials, Medications)
- `Supplier`: Represents suppliers with properties like ID, name, contact info, and address
- `Consts`: Contains system-wide constants including database connection strings

### Control Package
Implements business logic and data access:
- `InventoryManager`: Core controller managing all database operations for items and suppliers
- Contains methods for CRUD operations (Create, Read, Update, Delete) on both items and suppliers
- Includes a test harness in its main method to verify database operations

### Boundary Package
Implements the user interface:
- `MainFrame`: Central navigation hub for accessing different system modules
- `InventoryManagementFrame`: Complete interface for item management (add, update, delete)
- `SupplierManagementFrame`: Complete interface for supplier management (add, update, delete)
- All UI components use Swing with intuitive workflows and consistent design

## Database Connection
The system connects to a Microsoft Access database. Connection testing can be performed using:
- `DatabaseTest.java`: Verifies successful connection to the Access database
- The database schema includes tables for Items and Suppliers with appropriate relationships

## Testing
The `InventoryManager` class contains a main method with toggleable test operations to verify:
- Creating new suppliers and items
- Updating existing records
- Deleting records
- Each operation can be individually enabled/disabled for targeted testing

## Getting Started
1. Ensure you have Java 8 or higher installed
2. Configure MS Access and the JDBC drivers according to project requirements
3. Run MainFrame to launch the application
4. Use the intuitive navigation to manage your dental inventory and suppliers

## Features
- Complete inventory and supplier management
- Intuitive user interface with clear instructions
- Robust error handling and validation
- Dynamic data loading with real-time user feedback
