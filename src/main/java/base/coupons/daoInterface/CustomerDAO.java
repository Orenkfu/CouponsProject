package base.coupons.daoInterface;

import java.util.ArrayList;
import java.util.List;

import base.coupons.beans.Coupon;
import base.coupons.beans.Customer;
import base.coupons.exceptions.CouponException;

public interface CustomerDAO {
	public Customer createCustomer(Customer customer) throws CouponException;

	public boolean removeCustomer(Customer customer) throws CouponException;

	public void updateCustomer(Customer customer) throws CouponException;

	public ArrayList<Coupon> getCoupons(Customer customer) throws CouponException;

	public List<Customer> getAllCustomers() throws CouponException;

	public long login(String custName, String password) throws CouponException;

	public Customer getCustomer(Long id) throws CouponException;
}
