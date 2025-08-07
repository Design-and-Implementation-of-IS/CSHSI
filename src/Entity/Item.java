package Entity;

import java.util.Date;
import java.util.Objects;

public class Item {
private String serialNumber;
private String name;
private String description;
private Date expirationDate;
private ItemCategory category;
private int supplierId;

public Item(String serialNumber, String name, String description, Date expirationDate, ItemCategory category,
int supplierId) {
super();
this.serialNumber = serialNumber;
this.name = name;
this.description = description;
this.expirationDate = expirationDate;
this.category = category;
this.supplierId = supplierId;
}

public String getSerialNumber() {
return serialNumber;
}
public void setSerialNumber(String serialNumber) {
this.serialNumber = serialNumber;
}
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
public String getDescription() {
return description;
}
public void setDescription(String description) {
this.description = description;
}
public Date getExpirationDate() {
return expirationDate;
}
public void setExpirationDate(Date expirationDate) {
this.expirationDate = expirationDate;
}
public String getCategory() {
return category.name();
}
public void setCategory(ItemCategory category) {
this.category = category;
}
public int getSupplierId() {
return supplierId;
}
public void setSupplierId(int supplierId) {
this.supplierId = supplierId;
}

@Override
public String toString() {
return "Item [serialNumber=" + serialNumber + ", name=" + name + ", description="
+ description + ", expirationDate=" + expirationDate + ", category=" + category + ", supplierId=" + supplierId + "]";
}


}
