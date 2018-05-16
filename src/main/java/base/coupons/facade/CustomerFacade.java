package base.coupons.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.coupons.beans.Coupon;
import base.coupons.beans.CouponType;
import base.coupons.beans.Customer;
import base.coupons.dao.CouponDBDAO;
import base.coupons.dao.CustomerDBDAO;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.DoesntExistException;
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.exceptions.NullException;

public class CustomerFacade extends Client {
	private CustomerDBDAO customerDAO;
	private CouponDBDAO couponDAO;
	private Long id;

	public CustomerFacade() {
		super();
		this.couponDAO = new CouponDBDAO();
		this.customerDAO = new CustomerDBDAO();
	}

	/**
	 * This is the constructor used by the Coupon System when legally logging in to
	 * the system.
	 * 
	 * @param customerDAO
	 * @param couponDAO
	 * @param id
	 */
	public CustomerFacade(CustomerDBDAO customerDAO, CouponDBDAO couponDAO, long id) {
		super();
		this.customerDAO = customerDAO;
		this.couponDAO = couponDAO;
		this.id = id;

	}

	public long getId() {
		return id;
	}

	/**
	 * creates a link between this customer to given coupon, if customer is not
	 * linked to it already, coupon amount attribute is above 0, and coupon enddate
	 * is valid. can accept coupons with only id set.
	 * 
	 * @param coupon
	 * @throws IllegalArgumentCouponException
	 *             if coupon is out of stock, or date is invalid.
	 * @throws CouponException
	 *             - customer already purchased this coupon (already exists in
	 *             customer_coupon).
	 * @throws CouponException
	 *             if database query failed.
	 * @throws NullException
	 *             - if coupon or coupon ID are null.
	 */
	public void purchaseCoupon(Coupon tempCoupon)
			throws IllegalArgumentCouponException, NullException, CouponException {
		if (tempCoupon == null || tempCoupon.getId() == null)
			throw new NullException("Cannot accept null coupon or coupon ID.");
		Coupon coupon = couponDAO.getCoupon(tempCoupon.getId());
		int amount = coupon.getAmount();
		Date enddate = coupon.getEndDate();
		Date now = new Date(System.currentTimeMillis());

		// checking if there are coupons left & if they are valid
		if (amount < 1) {
			throw new IllegalArgumentCouponException("Coupon out of stock.");
		}
		if (enddate.before(now)) {
			throw new IllegalArgumentCouponException("Coupon expired.");
		}
		customerDAO.linkCouponToCustomer(coupon, this.id);
		coupon.setAmount(amount--);
		couponDAO.updateAmount(coupon);

	}

	/**
	 * searches Coupons table for all coupons belonging to this customer. gets an
	 * ArrayList containing those coupons.
	 * 
	 * @return ArrayList containing coupons belonging to this customer.
	 * @throws CouponException
	 *             - if customer does not have any coupons, or if a database error
	 *             occurred.
	 */
	public ArrayList<Coupon> getAllPurchasedCoupons() throws CouponException {
		List<Coupon> coupons = new ArrayList<>();
		Customer customer = customerDAO.getCustomer(this.id);
		coupons = customerDAO.getCoupons(customer);

		return (ArrayList<Coupon>) coupons;
	}

	/**
	 * gets all coupons of this company and returns those of given CouponType.
	 * 
	 * @param type
	 *            of coupon.
	 * @return ArrayList<Coupon> of Coupons of this type.
	 * @throws CouponException
	 *             no coupons of this type belong to this company, or database query
	 *             failed.
	 * @throws NullException
	 *             if given null type.
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByType(CouponType type) throws CouponException, NullException {
		if (type.equals(null)) {
			throw new NullException("please specify a coupon type.");
		}
		List<Coupon> tempCoupons = new ArrayList<>();
		List<Coupon> coupons = new ArrayList<Coupon>();
		Customer customer = customerDAO.getCustomer(id);
		tempCoupons = customerDAO.getCoupons(customer);
		for (Coupon coupon : tempCoupons) {
			if (type.equals(coupon.getType())) {
				coupons.add(coupon);
			}

		}
		return (ArrayList<Coupon>) coupons;
	}

	/**
	 * 
	 * @return ArrayList of all coupons in the database.
	 * @throws CouponException
	 *             - database error has occurred.
	 */
	public List<Coupon> getAllExistingCoupons() throws CouponException {

		return couponDAO.getAllCoupons();

	}

	/**
	 * gets all coupons of this company and returns those under given price.
	 * 
	 * @param type
	 *            of coupon.
	 * @return ArrayList<Coupon> of Coupons under given price.
	 * @throws CouponException
	 *             no coupons under this price belong to this company, or database
	 *             query failed.
	 */
	public ArrayList<Coupon> getAllPurchasedCouponsByPrice(double price) throws CouponException {

		List<Coupon> tempCoupons = new ArrayList<>();
		List<Coupon> coupons = new ArrayList<Coupon>();
		Customer customer = customerDAO.getCustomer(id);
		tempCoupons = customerDAO.getCoupons(customer);
		for (Coupon coupon : tempCoupons) {
			if (coupon.getPrice() <= price) {
				coupons.add(coupon);
			}

		}

		return (ArrayList<Coupon>) coupons;

	}

	public ArrayList<Coupon> getAllExistingCouponsByPage(int page, int page_size) throws CouponException {
		if (page < 0) {
			throw new IllegalArgumentCouponException("Cannot leaf to this page.");
		}
		ArrayList<Coupon> coupons = couponDAO.getCouponsByPage(page, page_size);
		if (coupons.isEmpty()) {
			throw new LastPageException("Last Page Reached.");
		}
		return coupons;

	}

	public List<Coupon> getAllExistingCouponsByType(CouponType type, int page, int page_size) throws CouponException {
		if (page < 0) {
			throw new IllegalArgumentCouponException("Cannot leaf to this page.");
		}
		List<Coupon> coupons = couponDAO.getCouponsByType(type, page, page_size);
		if (coupons.isEmpty()) {
			throw new LastPageException("Last Page Reached.");
		}
		return coupons;

	}

	/**
	 * method called by the login method in CouponSystem Singleton if clientType is
	 * customer. calls the login method from customer DAO and throws an exception if
	 * login details are incorrect.
	 * 
	 * @param custName
	 *            passed by CouponSystem method
	 * @param password
	 *            passed by CouponSystem method
	 * @return Id value of this customer, to be saved in facade.
	 * @throws CouponException
	 *             parameters are incorrect.
	 */
	@Override
	public Long login(String custName, String password) throws CouponException {
		Long id = customerDAO.login(custName, password);
		if (id == -1) {
			throw new DoesntExistException("Name or password are incorrect. Please try again.");
		}

		return id;

	}

	public List<Coupon> getAllPurchasedCouponsByPage(Integer page, int page_size) throws CouponException {
		Customer customer = customerDAO.getCustomer(this.id);
		if (page < 0) {
			throw new IllegalArgumentCouponException("Cannot leaf to this page.");
		}
		List<Coupon> coupons = customerDAO.getPurchasedCouponsByPage(customer, page, page_size);
		if (coupons.isEmpty()) {
			throw new LastPageException("Last Page Reached.");
		}
		return coupons;
	}

	public List<Coupon> getAllExistingCouponsByPrice(double price, Integer page, int size) throws CouponException {
		if (page < 0) {
			throw new IllegalArgumentCouponException("Cannot leaf to this page.");
		}
		List<Coupon> coupons = couponDAO.getCouponsByPrice(price, page, size);
		if (coupons.isEmpty()) {
			throw new LastPageException("Last Page Reached.");
		}
		return coupons;
	}

	// public Client login(String custName, String password) throws CouponException
	// {
	// long id = customerDAO.login(custName, password);
	// if (id == -1) {
	// throw new DoesntExistException("Name or password are incorrect. Please try
	// again.");
	// }
	// CustomerFacade customerFacade = new CustomerFacade(id);
	// return customerFacade;
	//
	// }

	// /**
	// * signup for new Customer. performs check that this customer's name is
	// unique,
	// * adds details to database and if successful returns customerfacade.
	// *
	// * @param compName
	// * @param password
	// * @return facade belonging to this new Customer.
	// * @throws CouponException
	// * if customer details are invalid.
	// */
	// public Client signup(String compName, String password) throws CouponException
	// {
	// Customer tempCustomer = new Customer(compName, password);
	// if (!validator.isUnique(tempCustomer)) {
	// throw new CouponException("customer name already taken, please try again.");
	// }
	// long id = customerDAO.createCustomer(tempCustomer).getId();
	// CustomerFacade facade = new CustomerFacade(id);
	// return facade;
	//
	// }
}
