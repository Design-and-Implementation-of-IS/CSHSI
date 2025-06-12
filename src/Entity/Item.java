package Entity;

import java.util.Date;
import java.util.Objects;

public class Item {	
	private int serialNumber;
	private Date expirationDate;
	private Date recivedDate;
	private ItemStatus status;
	private Date usageDate;
	private ItemType itemType;
	public static int numOfItems = 0;
	
	
	public Item(int serialNumber, Date expirationDate, Date recivedDate, ItemStatus status, Date usageDate,
			ItemType itemType) {
		super();
		this.serialNumber = ++numOfItems;
		this.expirationDate = expirationDate;
		this.recivedDate = recivedDate;
		this.status = status;
		this.usageDate = usageDate;
		this.itemType = itemType;
	}


	public int getSerialNumber() {
		return serialNumber;
	}


	public void setSerialNumber(int serialNumber) {
		this.serialNumber = serialNumber;
	}


	public Date getExpirationDate() {
		return expirationDate;
	}


	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}


	public Date getRecivedDate() {
		return recivedDate;
	}


	public void setRecivedDate(Date recivedDate) {
		this.recivedDate = recivedDate;
	}


	public ItemStatus getStatus() {
		return status;
	}


	public void setStatus(ItemStatus status) {
		this.status = status;
	}


	public Date getUsageDate() {
		return usageDate;
	}


	public void setUsageDate(Date usageDate) {
		this.usageDate = usageDate;
	}


	public ItemType getItemType() {
		return itemType;
	}


	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}


	public static int getNumOfItems() {
		return numOfItems;
	}


	public static void setNumOfItems(int numOfItems) {
		Item.numOfItems = numOfItems;
	}


	@Override
	public String toString() {
		return "Item [serialNumber=" + serialNumber + ", expirationDate=" + expirationDate + ", recivedDate="
				+ recivedDate + ", status=" + status + ", usageDate=" + usageDate + ", itemType=" + itemType + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(expirationDate, itemType, recivedDate, serialNumber, status, usageDate);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(expirationDate, other.expirationDate) && Objects.equals(itemType, other.itemType)
				&& Objects.equals(recivedDate, other.recivedDate) && serialNumber == other.serialNumber
				&& status == other.status && Objects.equals(usageDate, other.usageDate);
	}
	
	
}


