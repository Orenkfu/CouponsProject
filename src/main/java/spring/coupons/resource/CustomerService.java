package spring.coupons.resource;

import java.math.BigDecimal;

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

import base.coupons.beans.Coupon;
import base.coupons.beans.CouponType;
import base.coupons.couponSystemCore.CouponSystem;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.facade.ClientType;
import base.coupons.facade.CustomerFacade;
import spring.coupons.Message;
import spring.coupons.income.Income;
import spring.coupons.income.IncomeFacade;
import spring.coupons.income.IncomeType;

@RestController
public class CustomerService {

	@Autowired
	private IncomeFacade income;

	private CustomerFacade getFacadeFromSession(HttpServletRequest request) throws CouponException {
		HttpSession session = request.getSession();
		CustomerFacade customer = (CustomerFacade) session.getAttribute("customer");
		if (customer == null) {
			throw new CouponException("You are not logged in.");
		}
		return customer;

	}

	@RequestMapping(path = "/api/customer/login/{username}/{password}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Message login(HttpServletRequest request, @PathVariable("username") String name,
			@PathVariable("password") String password) throws CouponException {
		HttpSession session = request.getSession(true);
		// CHECK FOR WHITESPACE?
		if (name != null && password != null && !name.trim().isEmpty() && !password.trim().isEmpty()) {
			CustomerFacade customer = (CustomerFacade) CouponSystem.getInstance().login(name, password,
					ClientType.CUSTOMER);
			session.setAttribute("customer", customer);
			return new Message(session.getId());
		} else {
			throw new CouponException("Login failed, try again.");
		}

	}

	@RequestMapping(path = "/api/customer/login", method = RequestMethod.DELETE)
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.setAttribute("customer", null);
			session.invalidate();
			session = null;
		}
	}

	@RequestMapping(path = "/api/customer/purchase", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Coupon[] getAllExistingCoupons(HttpServletRequest request,
			@RequestParam(name = "byPage", value = "byPage", required = false, defaultValue = "0") Integer page,
			@RequestParam(name = "size", value = "size", required = false, defaultValue = "6") int size,
			@RequestParam(name = "byType", value = "byType", required = false) CouponType type,
			@RequestParam(name = "byPrice", value = "byPrice", required = false) Double price) throws CouponException {
		CustomerFacade customer = getFacadeFromSession(request);
		if (price != null) {
			return customer.getAllExistingCouponsByPrice(price, page, size).toArray(new Coupon[0]);
		} else if (type != null) {
			return customer.getAllExistingCouponsByType(type, page, size).toArray(new Coupon[0]);
		} else {
			return customer.getAllExistingCouponsByPage(page, size).toArray(new Coupon[0]);

		}
	}

	@RequestMapping(path = "/api/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Coupon[] getAllPurchasedCoupons(HttpServletRequest request,
			@RequestParam(name = "byPrice", value = "byPrice", required = false) Long price,
			@RequestParam(name = "byType", value = "byType", required = false) CouponType type,
			@RequestParam(name = "byPage", value = "byPage", required = false) Integer page,
			@RequestParam(name = "size", value = "size", required = false, defaultValue = "5") int size)
			throws CouponException {
		CustomerFacade customer = getFacadeFromSession(request);
		if (price != null) {
			return customer.getAllPurchasedCouponsByPrice(price).toArray(new Coupon[0]);
		} else if (type != null) {
			return customer.getAllPurchasedCouponsByType(type).toArray(new Coupon[0]);
		} else if (page != null) {
			return customer.getAllPurchasedCouponsByPage(page, size).toArray(new Coupon[0]);
		} else {
			return customer.getAllPurchasedCoupons().toArray(new Coupon[0]);
		}
	}

	@RequestMapping(path = "/api/customer/purchase/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Message purchaseCoupon(HttpServletRequest request, @RequestBody Coupon coupon, @PathVariable("id") Long id)
			throws CouponException {
		CustomerFacade customer = getFacadeFromSession(request);
		if (id.equals(coupon.getId())) {
			customer.purchaseCoupon(coupon);
			income.storeIncome(new Income(customer.getId(), IncomeType.PURCHASE, new BigDecimal(coupon.getPrice())));
		}
		return new Message("Succesfully Purchased Coupon!");
	}

	@RequestMapping(path = "/api/customer/income", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Income[] getIncomeRecords(HttpServletRequest request) throws CouponException {
		CustomerFacade customer = getFacadeFromSession(request);
		return income.getCustomerIncome(customer.getId()).toArray(new Income[0]);
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
