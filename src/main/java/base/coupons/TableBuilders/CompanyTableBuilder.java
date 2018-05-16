package base.coupons.TableBuilders;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import base.coupons.dao.ConnectionPool;
import base.coupons.exceptions.CouponException;

/**
 * DEPRECATED
 */
public class CompanyTableBuilder {

	public static void main(String[] args) {
		Connection con = ConnectionPool.getInstance().getConnection();

		// sql EXCEPTION
		String sql = "CREATE TABLE Companies (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name VARCHAR(75) UNIQUE, password VARCHAR(50), email VARCHAR(50), PRIMARY KEY(id))";
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			System.out.println(sql);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}
	}

	// public class Customer {
	// private long id;
	// private String custName;
	// private String password;
	// private List<Coupon> coupons;
	// private Date signupDate;
	//

}
