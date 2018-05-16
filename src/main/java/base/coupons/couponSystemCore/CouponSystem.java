package base.coupons.couponSystemCore;

import base.coupons.dao.CompanyDBDAO;
import base.coupons.dao.ConnectionPool;
import base.coupons.dao.CouponDBDAO;
import base.coupons.dao.CustomerDBDAO;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.facade.AdminFacade;
import base.coupons.facade.Client;
import base.coupons.facade.ClientType;
import base.coupons.facade.CompanyFacade;
import base.coupons.facade.CustomerFacade;
import base.coupons.validators.EmailValidator;
import base.coupons.validators.Validator;

public class CouponSystem {
	private CouponDBDAO couponDAO;
	private CompanyDBDAO companyDAO;
	private CustomerDBDAO customerDAO;
	private Validator validator;
	private EmailValidator emailValidator;
	private static final CouponSystem instance = new CouponSystem();

	private CouponSystem() {
		super();
		InitializeSystem();
	}

	public static CouponSystem getInstance() {
		return instance;
	}

	/**
	 * private method called on CouponSystem's constructor. will start up all
	 * relevant data accessors and tasks neccessary for running the Coupon System.
	 */
	private void InitializeSystem() {
		ConnectionPool.getInstance();

		customerDAO = new CustomerDBDAO();
		companyDAO = new CompanyDBDAO();
		couponDAO = new CouponDBDAO();
		validator = new Validator();
		emailValidator = new EmailValidator();

	}

	/**
	 * @param String
	 *            name - Client's user name.
	 * @param String
	 *            password - Client password.
	 * @param ClientType
	 *            specifying which type of Client.
	 * @return subclass of Client - Admin, Customer, or Company Facade.
	 * @throws CouponException
	 *             if input did not match existing Client in Database.
	 * 
	 **/
	public Client login(String name, String password, ClientType type) throws CouponException {
		name = name.trim();
		password = password.trim();

		if (ClientType.ADMIN.equals(type)) {
			AdminFacade facade = new AdminFacade(this.companyDAO, this.customerDAO, this.couponDAO, this.validator,
					this.emailValidator);
			facade.login(name, password);
			return facade;
		}
		if (ClientType.COMPANY.equals(type)) {
			CompanyFacade tempFacade = new CompanyFacade(this.companyDAO);
			Long id = tempFacade.login(name, password);
			CompanyFacade facade = new CompanyFacade(this.companyDAO, this.couponDAO, this.validator, id);
			return facade;
		}
		if (ClientType.CUSTOMER.equals(type)) {
			CustomerFacade tempFacade = new CustomerFacade();
			Long id = tempFacade.login(name, password);
			CustomerFacade facade = new CustomerFacade(this.customerDAO, this.couponDAO, id);
			return facade;
		}
		throw new IllegalArgumentCouponException("Wrong input, please try again. make sure client type is valid.");
	}

	public void shutdown() throws InterruptedException, CouponException {
		ConnectionPool.getInstance().closeAllConnections();

	}
}

// public Client signup(String name, String password, String email, ClientType
// type) throws CouponException {
// name = name.trim();
// password = password.trim();
// email = email.trim();
//
// if (ClientType.ADMIN.equals(type)) {
// throw new IllegalArgumentCouponException("Signup unavailable for Admin.");
// }
//
// if (ClientType.COMPANY.equals(type)) {
// CompanyFacade facade = new CompanyFacade();
// return facade;
// }
// if (ClientType.CUSTOMER.equals(type)) {
// CustomerFacade facade = new CustomerFacade();
// return facade;
// }
// // FILL THIS EXCEPTION TEXT
// throw new CouponException("unnamed exception");
// }