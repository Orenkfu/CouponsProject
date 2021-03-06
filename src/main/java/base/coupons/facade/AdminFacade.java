package base.coupons.facade;

import java.util.ArrayList;
import java.util.List;

import base.coupons.beans.Company;
import base.coupons.beans.Coupon;
import base.coupons.beans.Customer;
import base.coupons.dao.CompanyDBDAO;
import base.coupons.dao.CouponDBDAO;
import base.coupons.dao.CustomerDBDAO;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.DoesntExistException;
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.exceptions.NullException;
import base.coupons.validators.EmailValidator;
import base.coupons.validators.Validator;

public class AdminFacade extends Client {
	private final static byte COMPANY_DB_LIMIT = 50;
	private final static byte CUSTOMER_DB_LIMIT = 75;
	private final static String ADMIN_USERNAME = "admin";
	private final static String ADMIN_PASSWORD = "1234";

	private CompanyDBDAO companyDAO;
	private CustomerDBDAO customerDAO;
	private CouponDBDAO couponDAO;
	private EmailValidator emailValidator;
	private Validator validator;

	/**
	 * This is the constructor used by the Coupon System when legally logging in to
	 * the system.
	 * 
	 * @param validator
	 * @param companyDAO
	 * @param customerDAO
	 * @param couponDAO
	 * @param emailValidator2
	 */
	public AdminFacade(CompanyDBDAO companyDAO, CustomerDBDAO customerDAO, CouponDBDAO couponDAO, Validator validator,
			EmailValidator emailValidator) {
		super();
		this.companyDAO = companyDAO;
		this.customerDAO = customerDAO;
		this.couponDAO = couponDAO;
		this.validator = validator;
		this.emailValidator = emailValidator;
	}

	public AdminFacade() {
		super();
		this.companyDAO = new CompanyDBDAO();
		this.customerDAO = new CustomerDBDAO();
		this.couponDAO = new CouponDBDAO();
		this.emailValidator = new EmailValidator();
		this.validator = new Validator();

	}

	/**
	 * Admin method for creating a new company in Companies table. before calling
	 * create method, trims name & email, validates email address and checks
	 * database for duplicate keys.<br>
	 * When creating a company, ID is generated by database and thus must be null.
	 * 
	 * @param company
	 *            to be created.
	 * @return Company with a database-generated ID attribute.
	 * 
	 * @throws CouponException
	 *             if company name already exists (or another SQL error).
	 * @throws IllegalArgumentCouponException
	 *             if email address is invalid.
	 * @throws NullException
	 *             if a mandatory field is null.
	 */
	public Company createCompany(Company company)
			throws CouponException, IllegalArgumentCouponException, NullException {
		if (company == null || company.getCompName() == null || company.getPassword() == null
				|| company.getEmail() == null) {
			throw new NullException("Cannot accept null company values (except for ID).");
		}

		company.setCompName(company.getCompName().trim());
		company.setEmail(company.getEmail().trim());
		company.setPassword(company.getPassword().trim());
		if (company.getId() != null) {
			throw new CouponException(
					"ID attribute is generated by database. to create new company, ID value must be null");
		}
		if (company.getCompName().length() > COMPANY_DB_LIMIT || company.getEmail().length() > COMPANY_DB_LIMIT
				|| company.getPassword().length() > COMPANY_DB_LIMIT) {
			throw new IllegalArgumentCouponException("Unable to create company. Maximum Characters Allowed: 50.");
		}
		if (!emailValidator.validate(company.getEmail()))
			throw new IllegalArgumentCouponException("please insert a valid Email address.");

		if (validator.isUnique(company)) {
			companyDAO.createCompany(company);
		} else {
			throw new CouponException("Company name already exists");
		}

		return company;
	}

	/**
	 * Admin method for removing a company from Companies table. before calling
	 * removeCompany DAO method, removes all coupons belonging to company from
	 * Coupons, Company_Coupon and Customer_coupon tables.
	 * 
	 * @param company
	 *            to be created.
	 * @return Company with a database-generated ID attribute.
	 * 
	 * @throws CouponException
	 *             if company name already exists (or another SQL error).
	 * @throws IllegalArgumentCouponException
	 *             if email address is invalid.
	 * @throws NullException
	 *             if given object is null.
	 */
	public void removeCompany(Company company) throws CouponException {
		if (company == null) {
			throw new NullException("cannot accept null company.");
		}

		if (company.getId() == null) {
			company.setId(companyDAO.getCompanyId(company));
		}
		ArrayList<Coupon> companyCoupons = companyDAO.getCoupons(company);
		for (Coupon coupon : companyCoupons) {
			couponDAO.removeCouponFromCompany(coupon);
			couponDAO.removeCouponFromCustomer(coupon);
			couponDAO.removeCoupon(coupon);

		}
		if (companyDAO.removeCompany(company)) {
			company.setId(null);
			// System.out.println("Company successfuly removed");
		} else {
			// System.out.println("Company not removed, not found.");
		}

	}

	/**
	 * takes Company object and updates company with corresponding ID in Companies
	 * table. returns the updated Company object. Validates updated email address
	 * before performing update. does not accept null object or null updated values.
	 * 
	 * @param company
	 * @return Company
	 * @throws IllegalArgumentCouponException
	 *             updated email address is invalid.
	 * @throws CouponException
	 *             - data error has occurred.
	 * @throws NullException
	 *             - if given null values.
	 */
	public void updateCompany(Company company) throws CouponException, IllegalArgumentCouponException {
		if (company == null || company.getEmail() == null || company.getPassword() == null)
			throw new NullException("Cannot update to null. to remove company, use delete method.");
		Long id = company.getId();
		if (id == null) {
			id = companyDAO.getCompanyId(company);
		}
		String email = company.getEmail();
		company.setId(id);
		if (company.getPassword().length() > COMPANY_DB_LIMIT) {
			throw new IllegalArgumentCouponException("Unable to create customer. Maximum characters allowed: 75.");
		}
		if (emailValidator.validate(email)) {
			companyDAO.updateCompany(company);
		} else {
			throw new IllegalArgumentCouponException("email address is invalid. please enter a valid email adress.");
		}
	}

	/**
	 * Getter method for getting a single company's values from the database.<br>
	 * When returning a company, will return all of it's coupons as well.
	 * 
	 * @param id
	 * @return
	 * @throws CouponException
	 *             - if database error has occured
	 * @throws NullException
	 *             - if given id is null.
	 * @throws IllegalArgumentCouponException
	 *             - if id value is illegal.
	 * @throws DoesntExistException
	 *             - if company was not found.
	 */
	public Company getCompany(Long id) throws CouponException {
		if (id == null) {
			throw new NullException("cannot accept null id value.");
		}
		if (id < 1) {
			throw new IllegalArgumentCouponException("id value cannot be 0 or negative.");
		}
		Company company = companyDAO.getCompany(id);
		if (company == null)
			throw new DoesntExistException("Could not find company with ID :" + id);
		companyDAO.getCoupons(company);
		return company;
	}

	/**
	 * method for getting all companies currently in Companies table.
	 * 
	 * @return an ArrayList containing all companies in Companies table.
	 * @throws CouponException
	 *             - if database error has occurred.
	 */
	public ArrayList<Company> getAllCompanies() throws CouponException {
		ArrayList<Company> companies = new ArrayList<>();
		companies = (ArrayList<Company>) companyDAO.getAllCompanies();
		// if (companies.isEmpty())
		// throw new CouponException("There are currently no companies.");

		return companies;
	}

	public List<Company> getAllCompaniesByPage(int page, int size) throws CouponException {
		if (page < 0) {
			throw new IllegalArgumentCouponException("First page reached, cannot leaf backwards.");
		}
		List<Company> companies = companyDAO.getAllCompaniesByPage(page, size);
		if (companies.isEmpty()) {
			throw new LastPageException("Cannot leaf forward, last page reached.");
		}
		return companies;
	}

	/**
	 * Admin method for creating new customers in customer table. before calling
	 * create method, trims name & password, and checks database for duplicate keys.
	 * When creating a customer, ID is generated by database and thus must be null.
	 * 
	 * @param customer
	 *            to be created.
	 * @return Customer with a database-generated ID attribute.
	 * 
	 * @throws CouponException
	 *             if customer name already exists (or another SQL error).
	 * @throws IllegalArgumentCouponException
	 *             if email address is invalid.
	 * @throws NullException
	 *             if a mandatory field is null.
	 */
	public Customer createCustomer(Customer customer) throws CouponException {

		if (customer == null || customer.getCustName() == null || customer.getPassword() == null) {
			throw new NullException("Cannot except null customer values (except for id).");
		}
		customer.setCustName(customer.getCustName().trim());
		customer.setPassword(customer.getPassword().trim());

		if (customer.getId() != null) {
			throw new CouponException(
					"ID attribute is generated by the database, to create new customer, id must be null. ");
		}
		if (customer.getCustName().length() > CUSTOMER_DB_LIMIT
				|| customer.getPassword().length() > CUSTOMER_DB_LIMIT) {
			throw new IllegalArgumentCouponException("Unable to create customer. Maximum characters allowed: 75.");
		}
		if (!validator.isUnique(customer)) {
			throw new CouponException("customer name already exists.");
		}
		customerDAO.createCustomer(customer);

		return customer;

	}

	/**
	 * Admin method for removing a customer from the system. <br>
	 * Removes all customer's purchased coupons from linking table customer_coupons.
	 * if bean object is missing ID, method will automatically search for it by
	 * name. <br>
	 * if both ID and name are missing, exception will be thrown..<br>
	 * when done, method will set bean object's ID attribute to null to avoid error
	 * if trying to re-add customer.
	 * 
	 * @param customer
	 *            bean object to be deleted.
	 * 
	 * @throws DoesntExistException
	 *             if customer could not be found.
	 * @throws CouponException
	 *             database error has occurred.
	 * @throws NullException
	 *             - if given customer is null.
	 */
	public void removeCustomer(Customer customer) throws CouponException {
		if (customer == null) {
			throw new NullException("cannot accept null customer.");
		}
		if (customer.getId() == null) {
			customer.setId(customerDAO.getCustomerId(customer));
		}
		ArrayList<Coupon> customerCoupons = customerDAO.getCoupons(customer);
		for (Coupon coupon : customerCoupons) {
			couponDAO.removeCouponFromCustomer(coupon);
			// couponDAO.removeCoupon(coupon);
		}
		if (!customerDAO.removeCustomer(customer)) {
			throw new DoesntExistException(
					"Customer " + customer.getCustName() + " could not be found, WAS NOT DELETED.");
		}
		customer.setId(null);
	}

	/**
	 * Updates customer password in Customer Table in the Database. if given
	 * customer with no ID attribute, will automatically search for ID in the
	 * database.
	 * 
	 * 
	 * @param customer
	 * @return
	 * @throws CouponException
	 * @throws DoesntExistException
	 *             - if customer could not be found in database. * @throws
	 * @throws NullException
	 *             - if given customer is null.
	 */
	public void updateCustomer(Customer customer) throws CouponException {
		if (customer == null)
			throw new NullException("Cannot accept null customer.");
		Long id = customer.getId();
		if (id == null) {
			id = customerDAO.getCustomerId(customer);
			customer.setId(id);
			if (id == null) {
				throw new DoesntExistException("could not find customer " + customer.getCustName() + ".");
			}
		}
		if (customer.getPassword().length() > CUSTOMER_DB_LIMIT) {
			throw new IllegalArgumentCouponException("Unable to update customer. Maximum characters allowed: 75.");
		}
		customerDAO.updateCustomer(customer);

	}

	public Customer getCustomer(Long id) throws CouponException {
		if (id == null)
			throw new NullException("cannot accept null id value.");

		if (id < 1)
			throw new IllegalArgumentCouponException("ID value cannot be 0 or negative.");

		Customer customer = customerDAO.getCustomer(id);
		customer.setCoupons(customerDAO.getCoupons(customer));
		return customer;
	}

	/**
	 * Getter for all customers currently existing in the system's database.
	 * 
	 * @return ArrayList containing Customer bean objects representing all
	 *         customers.
	 * @throws CouponException
	 *             - database error has occurred
	 */
	public ArrayList<Customer> getAllCustomers() throws CouponException {
		ArrayList<Customer> customers = new ArrayList<>();
		customers = (ArrayList<Customer>) customerDAO.getAllCustomers();
		return customers;
	}

	public List<Customer> getAllCustomersByPage(int page, int size) throws CouponException {
		if (page < 0) {
			throw new IllegalArgumentCouponException("First page reached, cannot leaf backwards.");
		}
		List<Customer> customers = customerDAO.getAllCustomersByPage(page, size);
		if (customers.isEmpty()) {
			throw new LastPageException("Cannot leaf forward, last page reached.");
		}
		return customers;
	}

	/**
	 * this method exists to help with testing and keeping track of the system. if
	 * it is unwanted, it will be removed.
	 * 
	 * @return ArrayList of all coupons in the database.
	 * @throws CouponException
	 *             - database error has occurred.
	 */
	public ArrayList<Coupon> getAllExistingCoupons() throws CouponException {
		ArrayList<Coupon> coupons = (ArrayList<Coupon>) couponDAO.getAllCoupons();

		return coupons;

	}

	/**
	 * standard login for admin, does not require a DAO call. will either return
	 * null (success) or throw an exception. must be called through the CouponSystem
	 * login method, otherwise does nothing.
	 */
	@Override
	public Long login(String compName, String password) throws CouponException {
		if (compName.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
			return null;
		}
		throw new CouponException("name or password are incorrect, unable to log in.");

	}
}