package Entity;

import java.util.Objects;

public class ItemType {
	private int itemtTypeId;
	private String name;
	private String description;
	private ItemCategory category;
	private int minThreshold;
	private Supplier supplier;
	
	public ItemType(int itemtTypeId, String name, String description, ItemCategory category, int minThreshold,
			Supplier supplier) {
		super();
		this.itemtTypeId = itemtTypeId;
		this.name = name;
		this.description = description;
		this.category = category;
		this.minThreshold = minThreshold;
		this.supplier = supplier;
	}

	public int getItemtTypeId() {
		return itemtTypeId;
	}


	public void setItemtTypeId(int itemtTypeId) {
		this.itemtTypeId = itemtTypeId;
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


	public ItemCategory getCategory() {
		return category;
	}


	public void setCategory(ItemCategory category) {
		this.category = category;
	}


	public int getMinThreshold() {
		return minThreshold;
	}


	public void setMinThreshold(int minThreshold) {
		this.minThreshold = minThreshold;
	}


	public Supplier getSupplier() {
		return supplier;
	}


	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}


	@Override
	public String toString() {
		return "ItemType [itemtTypeId=" + itemtTypeId + ", name=" + name + ", description=" + description
				+ ", category=" + category + ", minThreshold=" + minThreshold + ", supplier=" + supplier + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, description, itemtTypeId, minThreshold, name, supplier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemType other = (ItemType) obj;
		return category == other.category && Objects.equals(description, other.description)
				&& itemtTypeId == other.itemtTypeId && minThreshold == other.minThreshold
				&& Objects.equals(name, other.name) && Objects.equals(supplier, other.supplier);
	}
	
}
