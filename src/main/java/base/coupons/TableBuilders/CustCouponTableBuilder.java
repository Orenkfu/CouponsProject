package base.coupons.TableBuilders;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import base.coupons.dao.ConnectionPool;
import base.coupons.exceptions.CouponException;

/*private long id;
	private String compName;
	private String password;
	private String email;
	private List<Coupon> coupons;
	private Date signupDate;
	*/
/**
 * DEPRECATED
 *
 */
public class CustCouponTableBuilder {

	public static void main(String[] args) {
		Connection con = ConnectionPool.getInstance().getConnection();

		// sql EXCEPTION
		String sql = "CREATE TABLE Customer_Coupon(CUST_ID BIGINT, COUPON_ID BIGINT, PRIMARY KEY(CUST_ID, COUPON_ID))";
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

}
