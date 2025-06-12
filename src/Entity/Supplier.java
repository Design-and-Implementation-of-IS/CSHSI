package Entity;

import java.util.Objects;

public class Supplier {
	private int supplierId;
	private String name;
	private String contactPerson;
	private String phone;
	private String email;
	private String adress;
	
	public Supplier(int supplierId, String name, String contactPerson, String phone, String email, String adress) {
		super();
		this.supplierId = supplierId;
		this.name = name;
		this.contactPerson = contactPerson;
		this.phone = phone;
		this.email = email;
		this.adress = adress;
	}
	
	public int getSupplierId() {
		return supplierId;
	}
	
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}
	
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAdress() {
		return adress;
	}
	
	public void setAdress(String adress) {
		this.adress = adress;
	}
	
	@Override
	public String toString() {
		return "Supplier [supplierId=" + supplierId + ", name=" + name + ", contactPerson=" + contactPerson + ", phone="
				+ phone + ", email=" + email + ", adress=" + adress + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(adress, contactPerson, email, name, phone, supplierId);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supplier other = (Supplier) obj;
		return Objects.equals(adress, other.adress) && Objects.equals(contactPerson, other.contactPerson)
				&& Objects.equals(email, other.email) && Objects.equals(name, other.name)
				&& Objects.equals(phone, other.phone) && supplierId == other.supplierId;
	}
	
}
