package Entity;

public class Consts {
    public static final String CONN_STR =
        "jdbc:ucanaccess://C:\\Users\\Daniel\\Desktop\\מאור\\מערכות מידע\\שנה ב\\תכן\\שיעורי בית\\2\\HW2\\DentalCare\\src\\Entity\\DentalCare_HW2.accdb";
    
    // Item Queries 
    public static final String QUERY_INSERT_ITEM = "{call Q_insert_item(?, ?, ?, ?, ?, ?)}";
    public static final String QUERY_DELETE_ITEM_BY_SERIAL = "{call Q_delete_item_by_serial(?)}";
    public static final String QUERY_UPDATE_ITEM = "{call Q_update_item(?, ?, ?, ?, ?, ?)}";

    // Supplier Queries 
    public static final String QUERY_INSERT_SUPPLIER = "{call Q_insert_supplier(?, ?, ?, ?, ?, ?)}";
    public static final String QUERY_DELETE_SUPPLIER = "{call Q_delete_supplier(?)}";
    public static final String QUERY_UPDATE_SUPPLIER = "{call Q_update_supplier(?, ?, ?, ?, ?, ?)}";
    
}