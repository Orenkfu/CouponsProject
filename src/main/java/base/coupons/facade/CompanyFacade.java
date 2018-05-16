package base.coupons.facade;

import java.util.ArrayList;
import java.util.List;

import base.coupons.beans.Company;
import base.coupons.beans.Coupon;
import base.coupons.beans.CouponType;
import base.coupons.dao.CompanyDBDAO;
import base.coupons.dao.CouponDBDAO;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.DoesntExistException;
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.exceptions.NullException;
import base.coupons.validators.Validator;

public class CompanyFacade extends Client {
	// title VARCHAR(75) UNIQUE, startdate DATE, enddate DATE, amount INT, type
	// VARCHAR(50),

	// message VARCHAR(150), price FLOAT, image VARCHAR(100),

	// PRIMARY KEY(id))";
	private final static byte MAX_TITLE = 75;
	private final static short MAX_MESSAGE = 150;

	private CompanyDBDAO companyDAO;
	private CouponDBDAO couponDAO;
	private Validator validator;
	private Long id;

	public CompanyFacade() {
		super();

	}

	/**
	 * This is the constructor used by the Coupon System when legally logging in to
	 * the system.
	 * 
	 * @param companyDAO
	 * @param couponDAO
	 * @param id
	 */
	public CompanyFacade(CompanyDBDAO companyDAO, CouponDBDAO couponDAO, Validator validator, Long id) {
		super();
		this.validator = validator;
		this.companyDAO = companyDAO;
		this.couponDAO = couponDAO;
		this.id = id;
	}

	public CompanyFacade(Long id) {
		super();
		this.id = id;
	}

	public CompanyFacade(CompanyDBDAO companyDAO) {
		this.companyDAO = companyDAO;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	/**
	 * creates a new Coupon in the Coupons table and links the coupon to this
	 * company's id in the company_coupon table.<br>
	 * Before creating the coupon, performs various validation checks: startdate
	 * (today or forward), enddate (after startdate), price (not null), unique (must
	 * not exist in database).
	 * 
	 * @param coupon
	 * @return Same coupon bean objecct, but with a set id value given by database.
	 * @throws IllegalArgumentCouponException
	 *             - if validation failed. Validated fields: startdate, enddate,
	 *             price, type.
	 * @throws CouponException
	 * @throws NullException
	 *             - if given coupon or mandatory fields are null.
	 */
	public Coupon createCoupon(Coupon coupon) throws IllegalArgumentCouponException, CouponException, NullException {
		coupon.setTitle(coupon.getTitle().trim());
		// coupon.setCompanyId(this.id);
		if (coupon == null || coupon.getPrice() == null || coupon.getType() == null || coupon.getTitle() == null)
			throw new NullException("Cannot accept null coupon, nor null Price, Type, or Title.");
		if (coupon.getTitle().length() > MAX_TITLE) {
			throw new IllegalArgumentCouponException("Unable to create coupon. Maximum title length: 75 characters.");
		}

		if (coupon.getMessage().length() > MAX_MESSAGE) {
			throw new IllegalArgumentCouponException(
					"Unable to create coupon. Maximum message length: 150 characters.");
		}
		if (coupon.getId() != null)
			throw new IllegalArgumentCouponException("Coupon ID is automatically generated, do not manually fill.");

		if (!validator.validateStartDate(coupon))
			throw new IllegalArgumentCouponException(
					"Coupon's start date is invalid. please make sure coupon's start date is today or forward.");

		if (!validator.validateEndDate(coupon))
			throw new IllegalArgumentCouponException(
					"Coupon's end date is invalid. please make sure coupon's end date is after the start date.");

		if (validator.isUnique(coupon)) {
			couponDAO.createCoupon(coupon);
			companyDAO.linkCouponToCompany(coupon, id);

		} else
			throw new CouponException("Coupon name already exists.");

		return coupon;
	}

	/**
	 * Takes a Coupon bean object, searches for its ID in the Coupons table, and
	 * attempts to delete it from all tables: Coupons, Company_Coupon, and
	 * Customer_Coupon.
	 * 
	 * @param coupon
	 *            to be deleted from database.
	 * @throws CouponException
	 *             if coupon could not be found in Coupons Table, or if it wasnt
	 *             found in Companies Table (SHOULD NEVER HAPPEN). also, if database
	 *             threw an exception.
	 * @throws NullException
	 *             - if given coupon is null.
	 */
	public void removeCoupon(Coupon coupon) throws CouponException {
		if (coupon == null)
			throw new NullException("cannot accept null coupon.");
		if (coupon.getId() == null)
			throw new NullException("Cannot accept coupon with null ID.");

		if (!validator.belongsTo(coupon, this.id)) {
			throw new CouponException("Cannot remove " + coupon + " it does not belong to your company.");
		}
		if (!couponDAO.removeCoupon(coupon))
			throw new CouponException("Could not find coupon " + coupon);
		if (!couponDAO.removeCouponFromCompany(coupon))
			throw new CouponException("Could not find coupon in Company Table - THIS SHOULD NEVER HAPPEN");
		couponDAO.removeCouponFromCustomer(coupon);
	}

	/**
	 * Takes a Coupon bean object, searches for its ID in the Coupons table, and
	 * updates database with this coupon's enddate and and price.<br>
	 * Validates given coupon belongs to this company, if it's null and if its end
	 * date is valid.
	 * 
	 * @param coupon
	 *            will use this coupon's ID to find a match in the database.
	 * @throws CouponException
	 *             if coupon does not belong to this company or if database threw an
	 *             exception.
	 * @throws NullException
	 *             if given coupon is null.
	 * @throws IllegalArgumentCouponException
	 *             coupon's end date is invalid (before the start date or null)
	 */
	public void updateCoupon(Coupon coupon) throws CouponException, NullException, IllegalArgumentCouponException {
		if (coupon == null)
			throw new NullException("cannot update coupon to null. to remove coupon please use remove option.");

		if (id == null) {
			couponDAO.getCouponId(coupon);
			if (id == null) {
				throw new DoesntExistException("Could not find coupon in database.");
			}
		}

		if (!validator.belongsTo(coupon, this.id))
			throw new CouponException("This Coupon does not belong to your company.");

		if (!validator.validateEndDate(coupon))
			throw new IllegalArgumentCouponException(
					"Coupon date is illegal. please make sure end date is not before start date.");

		couponDAO.updateCoupon(coupon);
	}

	/**
	 * Searches for coupon in Coupons Table, if it belongs to this company, returns
	 * it.
	 * 
	 * @param id
	 * @return Coupon bean object representing this coupon.
	 * @throws CouponException
	 *             if a database error has occurred
	 * @throws IllegalArgumentCouponException
	 *             - if this coupon does not belong to your company.
	 * @throws NullException
	 *             - if given null id.
	 * @throws DoesntExistException
	 *             - if coupon could not be found.
	 */
	public Coupon getCoupon(Long id) throws CouponException, NullException, IllegalArgumentCouponException {
		if (id == null) {
			throw new NullException("Must specify coupon ID.");
		}
		Coupon coupon = couponDAO.getCoupon(id);
		if (!validator.belongsTo(coupon, this.id)) {
			throw new IllegalArgumentCouponException("This Coupon does not belong to your company.");
		}
		return coupon;
	}

	/**
	 * gets all coupons belonging to this company.
	 * 
	 * @return an ArrayList holding all Coupons belonging to this company.
	 * @throws CouponException
	 */
	public ArrayList<Coupon> getAllCoupons() throws CouponException {
		Company company = companyDAO.getCompany(this.id);
		ArrayList<Coupon> coupons = companyDAO.getCoupons(company);

		return coupons;

	}

	/**
	 * takes all coupons belonging to this company from Coupons Table, and gets the
	 * ones belonging to this CouponType.
	 * 
	 * @return an ArrayList holding all Coupons of given type belonging to this
	 *         company.
	 * @throws CouponException
	 */
	public ArrayList<Coupon> getCouponByType(CouponType type) throws CouponException {
		Company company = companyDAO.getCompany(this.id);
		ArrayList<Coupon> coupons = companyDAO.getCoupons(company);
		ArrayList<Coupon> typeCoupons = new ArrayList<Coupon>();
		for (Coupon coupon : coupons) {
			if (coupon.getType().equals(type)) {
				typeCoupons.add(coupon);
			}

		}

		return typeCoupons;
	}

	public List<Coupon> getCouponByPage(int page, int size) throws CouponException {
		Company company = companyDAO.getCompany(this.id);
		if (page < 0) {
			page = 0;
		}
		List<Coupon> coupons = companyDAO.getCouponsByPage(company, page, size);
		if (coupons.isEmpty()) {
			throw new LastPageException("Last page reached.");
		}
		return coupons;
	}

	/**
	 * Method called by the login method in CouponSystem Singleton if clientType is
	 * company.<br>
	 * Calls the login method from customer DAO and throws an exception if login
	 * details are incorrect.
	 * 
	 * @param custName
	 *            passed by CouponSystem method
	 * @param password
	 *            passed by CouponSystem method
	 * @return Id value of this company, to be saved in facade.
	 * @throws CouponException
	 *             parameters are incorrect.
	 */
	@Override
	public Long login(String compName, String password) throws CouponException {

		Long id = companyDAO.login(compName, password);
		if (id == -1) {
			throw new DoesntExistException("Name or password are incorrect. Please try again.");
		}
		return id;
	}

	public ArrayList<Coupon> getCouponByPrice(Double price) throws CouponException {
		Company company = companyDAO.getCompany(this.id);
		ArrayList<Coupon> coupons = companyDAO.getCoupons(company);
		ArrayList<Coupon> priceCoupons = new ArrayList<Coupon>();
		for (Coupon coupon : coupons) {
			if (coupon.getPrice() < price) {
				priceCoupons.add(coupon);
			}

		}

		return priceCoupons;
	}
}

// public Client signup(String compName, String password, String email) throws
// CouponException {
// Company tempCompany = new Company(compName, password, email);
// if (!validator.isUnique(tempCompany)) {
// throw new CouponException("company name is already taken, please try
// again.");
// }
// Company company = companyDAO.createCompany(tempCompany);
// CompanyFacade facade = new CompanyFacade(company.getId());
// return facade;
//
// }

// @Override
// public Client login(String compName, String password) throws CouponException
// {
//
// long id = companyDAO.login(compName, password);
// if (id == -1) {
// throw new DoesntExistException("Name or password are incorrect. Please try
// again.");
// }
// CompanyFacade facade = new CompanyFacade(id);
// return facade;
// }