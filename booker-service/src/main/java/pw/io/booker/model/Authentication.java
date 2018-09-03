package pw.io.booker.model;

import javax.persistence.OneToOne;

public class Authentication {
	private String token;
	@OneToOne
	private Customer customer;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
}
