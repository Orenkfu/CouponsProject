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
public class CouponTableBuilder {

	public static void main(String[] args) {
		Connection con = ConnectionPool.getInstance().getConnection();

		String sql = "CREATE TABLE Coupons (id BIGINT NOT NULL, title VARCHAR(75) UNIQUE, startdate DATE, enddate DATE, amount INT, type VARCHAR(50), message VARCHAR(150), price FLOAT, image VARCHAR(100), PRIMARY KEY(id))";
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