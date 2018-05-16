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
import base.coupons.exceptions.IllegalArgumentCouponException;
import base.coupons.exceptions.LastPageException;
import base.coupons.facade.ClientType;
import base.coupons.facade.CompanyFacade;
import spring.coupons.Message;
import spring.coupons.income.Income;
import spring.coupons.income.IncomeFacade;
import spring.coupons.income.IncomeType;

@RestController
public class CompanyService {
	private static final String DEFAULT_IMAGE = "images/defaultCoupon.jpg";

	private static final double INCOME_UPDATE_PRICE = 30;
	private static final double INCOME_CREATE_PRICE = 100;

	@Autowired
	private IncomeFacade income;

	private CompanyFacade getFacadeFromSession(HttpServletRequest request) throws CouponException {
		CompanyFacade company = (CompanyFacade) request.getSession(false).getAttribute("company");
		if (company == null) {
			throw new CouponException("You are not logged in.");
		}
		return company;
	}

	@RequestMapping(path = "/api/company/login/{username}/{password}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Message login(@PathVariable("username") String name, @PathVariable("password") String password,
			HttpServletRequest request) throws CouponException {
		if (name != null && password != null) {
			CompanyFacade company = (CompanyFacade) CouponSystem.getInstance().login(name, password,
					ClientType.COMPANY);
			HttpSession session = request.getSession(true);
			session.setAttribute("company", company);
			return new Message(session.getId());
		} else {
			throw new CouponException("Login failed.");
		}
	}

	@RequestMapping(path = "/api/company/login", method = RequestMethod.DELETE)
	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
			System.out.println("Session invalidated");
		}

	}

	@RequestMapping(path = "/api/company", method = RequestMethod.GET)
	public Coupon[] getAllCoupons(HttpServletRequest request,
			@RequestParam(value = "byType", name = "byType", required = false) CouponType type,
			@RequestParam(value = "byPrice", name = "byPrice", required = false) Double price,
			@RequestParam(value = "byPage", name = "byPage", required = false) int page,
			@RequestParam(value = "size", name = "size", required = false) Integer size) throws CouponException {
		CompanyFacade company = getFacadeFromSession(request);
		if (type != null) {
			return company.getCouponByType(type).toArray(new Coupon[0]);
		}
		if (price != null) {
			return company.getCouponByPrice(price).toArray(new Coupon[0]);
		}
		return company.getCouponByPage(page, size).toArray(new Coupon[0]);
	}

	@RequestMapping(path = "/api/company", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Coupon createCoupon(HttpServletRequest request, @RequestBody Coupon coupon) throws CouponException {
		if (coupon.getImage() == null || coupon.getImage().trim().isEmpty()) {
			coupon.setImage(DEFAULT_IMAGE);

		}
		CompanyFacade company = getFacadeFromSession(request);
		company.createCoupon(coupon);
		income.storeIncome(new Income(company.getId(), IncomeType.CREATE, new BigDecimal(INCOME_CREATE_PRICE)));
		return coupon;
	}

	@RequestMapping(path = "/api/company/{id}", method = RequestMethod.DELETE)
	public void deleteCouponById(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		CompanyFacade facade = getFacadeFromSession(request);
		facade.removeCoupon(facade.getCoupon(id));
	}

	@RequestMapping(path = "/api/company/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateCoupon(@PathVariable("id") Long id, @RequestBody Coupon coupon, HttpServletRequest request)
			throws CouponException {
		CompanyFacade company = getFacadeFromSession(request);
		if (coupon.getTitle().trim().isEmpty() || coupon.getImage().trim().isEmpty()
				|| coupon.getMessage().trim().isEmpty()) {
			throw new IllegalArgumentCouponException(
					"Received empty field/s, unable to update coupon. please fill all mandatory fields.");
		}
		System.out.println("coupon id: " + coupon.getId() + "||" + "parameter received id: " + id);
		if (!coupon.getId().equals(id)) {
			throw new CouponException("id does not match coupon id.");
		}
		company.updateCoupon(coupon);
		income.storeIncome(new Income(company.getId(), IncomeType.UPDATE, new BigDecimal(INCOME_UPDATE_PRICE)));
	}

	@RequestMapping(path = "/api/company/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Coupon getCouponById(@PathVariable("id") Long id, HttpServletRequest request) throws CouponException {
		System.out.println("CompanyService.getCouponById()");
		Coupon coupon = getFacadeFromSession(request).getCoupon(id);
		System.out.println(coupon);
		return coupon;
	}

	@RequestMapping(path = "/api/company/income", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Income[] getIncomeRecords(HttpServletRequest request) throws CouponException {
		CompanyFacade company = getFacadeFromSession(request);
		return income.getCompanyIncome(company.getId()).toArray(new Income[0]);
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
