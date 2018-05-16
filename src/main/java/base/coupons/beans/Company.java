package base.coupons.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * * Bean class representing coupons in this application.
 * 
 * @author oren
 *
 */
public class Company {
	private Long id;
	private String compName;
	private String password;
	private String email;
	private ArrayList<Coupon> coupons;
	private Date signupDate;

	public Company() {
		super();
	}

	public Company(String compName, String password, String email, List<Coupon> coupons) {
		super();
		this.compName = compName;
		this.password = password;
		this.email = email;
		this.coupons = (ArrayList<Coupon>) coupons;
		this.signupDate = new Date(System.currentTimeMillis());
	}

	public Company(String compName, String password, String email) {
		super();
		this.compName = compName;
		this.password = password;
		this.email = email;
	}

	public void setCoupons(ArrayList<Coupon> coupons) {
		this.coupons = coupons;
	}

	public Company(String compName) {
		super();
		this.compName = compName;
	}

	public Company(long id) {
		super();
		this.id = id;
	}

	public Date getSignupDate() {
		return signupDate;
	}

	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}

	@Override
	public String toString() {
		return ("|Company ID: |" + id + "| |Name: " + compName + "| |password: " + password + "| |email: " + email
				+ "|");
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ArrayList<Coupon> getCoupons() {
		return coupons;
	}

}
