package spring.coupons.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import base.coupons.beans.Company;
import base.coupons.beans.Customer;
import base.coupons.couponSystemCore.CouponSystem;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.facade.AdminFacade;
import base.coupons.facade.ClientType;
import spring.coupons.Message;
import spring.coupons.income.Income;
import spring.coupons.income.IncomeFacade;

@RestController
public class AdminService {

	@Autowired
	private IncomeFacade income;

	private AdminFacade getFacadeFromSession(HttpServletRequest request) throws CouponException {
		HttpSession session = request.getSession(false);
		AdminFacade admin = (AdminFacade) session.getAttribute("admin");
		if (admin == null) {
			throw new CouponException("You are not logged in.");
		}
		return admin;
	}

	@RequestMapping(path = "/api/admin/login/{username}/{password}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Message login(@PathVariable("username") String name, @PathVariable("password") String password,
			HttpServletRequest request) throws CouponException {
		if (name != null && password != null) {
			AdminFacade admin = (AdminFacade) CouponSystem.getInstance().login(name, password, ClientType.ADMIN);
			HttpSession session = request.getSession(true);
			session.setAttribute("admin", admin);
			return new Message(session.getId());
		} else {
			throw new CouponException("Login failed, try again.");
		}

	}

	@RequestMapping(path = "/api/admin/companies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Company[] getAllCompanies(HttpServletRequest request,
			@RequestParam(name = "byPage", value = "byPage", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", value = "size", required = false, defaultValue = "5") int size)
			throws CouponException {
		return getFacadeFromSession(request).getAllCompaniesByPage(page, size).toArray(new Company[0]);
	}

	@RequestMapping(path = "/api/admin/customers/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteCustomerById(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		admin.removeCustomer(new Customer(id));
	}

	@RequestMapping(path = "/api/admin/companies", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Company createCompany(@RequestBody Company company, HttpServletRequest request) throws CouponException {
		if (company.getCompName().trim().isEmpty() || company.getPassword().trim().isEmpty()
				|| company.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentCouponException(
					"Received empty field/s, unable to create company. please enter valid information.");
		}
		getFacadeFromSession(request).createCompany(company);
		return company;
	}

	@RequestMapping(path = "/api/admin/customers", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Customer createCustomer(@RequestBody Customer customer, HttpServletRequest request) throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		if (customer.getCustName().trim().isEmpty() || customer.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentCouponException(
					"Received empty field/s, unable to create customer. please enter valid information.");
		}
		admin.createCustomer(customer);

		return customer;
	}

	@RequestMapping(path = "/api/admin/login", method = RequestMethod.DELETE)
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		session.setAttribute("admin", null);
		session.invalidate();
	}

	@RequestMapping(path = "/api/admin/companies/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateCompany(@PathVariable("id") Long id, @RequestBody Company company, HttpServletRequest request)
			throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		if (company.getPassword().trim().isEmpty() || company.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentCouponException(
					"Received empty field/s, unable to update company. please enter valid information.");
		}
		if (!company.getId().equals(id)) {
			throw new CouponException("id does not match company id");
		}
		admin.updateCompany(company);
	}

	@RequestMapping(path = "/api/admin/customers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Customer[] getAllCustomers(HttpServletRequest request,
			@RequestParam(name = "byPage", value = "byPage", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", value = "size", required = false, defaultValue = "5") int size)
			throws CouponException {
		return getFacadeFromSession(request).getAllCustomersByPage(page, size).toArray(new Customer[0]);

	}

	@RequestMapping(path = "/api/admin/customers", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void patchCustomer(@PathVariable("id") Long id, @RequestBody Customer customer, HttpServletRequest request)
			throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		if (customer.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentCouponException(
					"Received empty field/s, unable to update customer. please enter valid information.");
		}
		if (!customer.getId().equals(id)) {
			throw new CouponException("id does not match customer id");
		}
		admin.updateCustomer(customer);

	}

	@RequestMapping(path = "/api/admin/companies/{id}", method = RequestMethod.DELETE)
	public void removeCompanyById(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		getFacadeFromSession(request).removeCompany(new Company(id));

	}

	@RequestMapping(path = "/api/admin/companies/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Company getCompany(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		return admin.getCompany(id);

	}

	@RequestMapping(path = "/api/admin/customers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Customer getCustomer(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		AdminFacade admin = getFacadeFromSession(request);
		Customer customer = admin.getCustomer(id);

		return customer;

	}

	@RequestMapping(path = "/api/admin/income", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Income[] getIncome(@RequestParam(value = "byCompany", required = false) Long companyId,
			@RequestParam(value = "byCustomer", required = false) Long customerId) throws CouponException {

		List<Income> incomelist;
		if (customerId != null && companyId == null) {
			incomelist = (List<Income>) income.getCustomerIncome(customerId);
		} else if (companyId != null && customerId == null) {
			incomelist = (List<Income>) income.getCompanyIncome(companyId);
		} else if (companyId == null && customerId == null) {
			incomelist = (List<Income>) income.getAllIncome();
		} else {
			throw new CouponException("failed to retrieve income.");
		}

		return incomelist.toArray(new Income[0]);

	}

	@ExceptionHandler
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Message handleException(Exception e) {
		if (e instanceof LastPageException) {
			Message message = new Message(e.getMessage(), "LASTPAGE");
			return message;
		}
		String message = e.getMessage();
		if (message == null) {
			message = "Internal server error.";
		}

		return new Message(message);
	}
}
