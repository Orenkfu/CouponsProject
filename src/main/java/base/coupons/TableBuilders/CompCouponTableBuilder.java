package base.coupons.TableBuilders;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import base.coupons.dao.ConnectionPool;
import base.coupons.exceptions.CouponException;

/**
 * DEPRECATED
 *
 */
public class CompCouponTableBuilder {

	public static void main(String[] args) {
		Connection con = ConnectionPool.getInstance().getConnection();
		String sql = "CREATE TABLE Company_Coupon (companyid BIGINT NOT NULL, couponid BIGINT NOT NULL, PRIMARY KEY(companyid, couponid))";
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
