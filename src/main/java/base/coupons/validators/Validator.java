package base.coupons.validators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import base.coupons.beans.Company;
import base.coupons.beans.Coupon;
import base.coupons.beans.Customer;
import base.coupons.dao.ConnectionPool;
import base.coupons.exceptions.CouponException;

public class Validator {
	/**
	 * @param coupon
	 *            whose start date is to be validated
	 * @return true if coupon's start date is valid (now and forward); false
	 *         otherwise
	 * 
	 * @throws CouponException
	 **/
	public boolean validateStartDate(Coupon coupon) {
		if (coupon.getStartDate() == null)
			return false;

		Date startDate = coupon.getStartDate();
		long currentTimeYesterday = System.currentTimeMillis() - (1000 * 60 * 60 * 24);
		Date Yesterday = new Date(currentTimeYesterday);

		if (startDate.after(Yesterday) || startDate.equals(Yesterday))
			return true;

		return false;

	}

	/**
	 * checks if end date is before start date.
	 * 
	 * @param coupon
	 *            whose end date is to be validated
	 * @return true if coupon's end date is after it's start date; false otherwise
	 * 
	 */
	public boolean validateEndDate(Coupon coupon) {
		if (coupon.getStartDate() == null || coupon.getEndDate() == null)
			return false;

		Date startTime = coupon.getStartDate();
		Date endTime = coupon.getEndDate();

		if (endTime.after(startTime))
			return true;

		return false;

	}

	/**
	 * Takes a Coupon object and performs a database check in Coupons Table to see
	 * if this coupon's title or ID are already taken.
	 * 
	 * @param coupon
	 *            To be checked for uniqueness.
	 * @return true if coupon does not exist in the database; false otherwise.
	 * @throws CouponException
	 */
	public boolean isUnique(Coupon coupon) throws CouponException {
		Connection con = ConnectionPool.getInstance().getConnection();
		String sql = "SELECT * FROM Coupons";
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Coupon currentCoupon = new Coupon();
				currentCoupon.setId(rs.getLong(1));
				currentCoupon.setTitle(rs.getString(2));
				if (currentCoupon.getTitle().equals(coupon.getTitle()) || coupon.getId() == currentCoupon.getId()) {
					return false;
				}

			}
			return true;
		} catch (SQLException e) {
			throw new CouponException("failed to perform database query.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}
	}

	/**
	 * Takes a Company object and performs a database check in Companies Table to
	 * see if this company's name or ID are already taken.
	 * 
	 * @param company
	 *            To be checked for uniqueness
	 * @return true if company is unique; otherwise false.
	 * @throws CouponException
	 *             if database query failed.
	 */
	public boolean isUnique(Company company) throws CouponException {
		Connection con = ConnectionPool.getInstance().getConnection();
		String sql = "SELECT * FROM Companies";
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Company currentCompany = new Company();
				currentCompany.setId(rs.getLong(1));
				currentCompany.setCompName(rs.getString(2));
				if (currentCompany.getCompName().equals(company.getCompName())) {
					ConnectionPool.getInstance().returnConnection(con);
					return false;
				}
			}
			return true;
		} catch (SQLException e) {
			throw new CouponException("failed to perform database query.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * Takes a Customer object and performs a database check in Customers Table to
	 * see if this customer's name or ID are already taken.
	 * 
	 * @param company
	 *            To be checked for uniqueness
	 * @return true if company is unique; otherwise false.
	 * @throws CouponException
	 *             if database query failed.
	 */
	public boolean isUnique(Customer customer) throws CouponException {
		Connection con = ConnectionPool.getInstance().getConnection();
		String sql = "SELECT * FROM Customers";
		PreparedStatement pstmt;
		try {
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Customer currentCustomer = new Customer();
				currentCustomer.setId(rs.getLong(1));
				currentCustomer.setCustName(rs.getString(2));
				if (currentCustomer.getCustName().equals(customer.getCustName())) {
					return false;
				}
			}
			return true;
		} catch (SQLException e) {
			throw new CouponException("could not perform unique check.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * A basic validation check that takes given coupon and queries the
	 * Company_Coupon to check whether it belongs to given company ID. <br>
	 * Called by CompanyFacade methods that get or change existing coupons, to check
	 * whether they have permission to do it.
	 * 
	 * 
	 * @param coupon
	 * @param id
	 * @return
	 * @throws CouponException
	 */
	public boolean belongsTo(Coupon coupon, Long id) throws CouponException {
		// System.out.println("==== TEST ==== BEGINNING VALIDATION BELONGS TO THIS
		// COMPANY");
		Connection con = ConnectionPool.getInstance().getConnection();
		String sql = "SELECT * FROM COMPANY_COUPON WHERE companyid = ?";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				long currentCouponId = rs.getLong(2);
				if (currentCouponId == coupon.getId()) {
					// System.out.println(
					// "==== TEST ==== ==VALIDATION COMPLETE, COUPON BELONGS TO THIS COMPANY==
					// BELONGS TO THIS COMPANY");
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new CouponException("Failed to validate Coupon owner.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

}
