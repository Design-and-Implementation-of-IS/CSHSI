package Entity;

import java.util.Date;
import java.util.HashMap;

public class DailyInventorySnapshot {
	private Date date;
	private HashMap<Integer, Integer> serialNumberToQuantity;
	
	public DailyInventorySnapshot(Date date, HashMap<Integer, Integer> serialNumberToQuantity) {
		super();
		this.date = date;
		this.serialNumberToQuantity = serialNumberToQuantity;
	}
	
	public DailyInventorySnapshot(Date date) {
		super();
		this.date = date;
		this.serialNumberToQuantity = new HashMap<Integer, Integer>();
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public HashMap<Integer, Integer> getserialNumberToQuantity() {
		return serialNumberToQuantity;
	}

	public void setserialNumberToQuantity(HashMap<Integer, Integer> serialNumberToQuantity) {
		this.serialNumberToQuantity = serialNumberToQuantity;
	}
	
	public void updateItemCuantity (int serialNumber, int quantity) {
		serialNumberToQuantity.put(serialNumber, quantity);
	}

}
