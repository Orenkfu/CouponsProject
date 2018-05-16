package base.coupons.beans;

import java.util.Date;
import java.util.List;

/**
 * * Bean class representing coupons in this application.
 * 
 * @author oren
 *
 */
public class Customer {
	private Long id;
	private String custName;
	private String password;
	private String email;

	public String getEmail() {
		return email;
	}

	public Customer(Long id) {
		super();
		this.id = id;
	}

	public Customer(String custName, String password, String email) {
		super();
		this.custName = custName;
		this.password = password;
		this.email = email;
	}

	public Customer(String custName, String password) {
		super();
		this.custName = custName;
		this.password = password;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	private List<Coupon> coupons;
	private Date signupDate;

	private Date birthDate;

	public Customer() {
		super();
	}

	public Customer(Long id, String custName, String password) {
		super();
		this.id = id;
		this.custName = custName;
		this.password = password;
		this.signupDate = new Date(System.currentTimeMillis());

	}

	public Date getSignupDate() {
		return signupDate;
	}

	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return ("|Customer ID: " + id + "| |Name: " + custName + "| |password: " + password + "|");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Coupon> getCoupons() {
		return coupons;
	}

	public void setCoupons(List<Coupon> coupons) {
		this.coupons = coupons;
	}
}
