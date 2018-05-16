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
public class CustomerTableBuilder {
	public static void main(String[] args) {
		Connection con = ConnectionPool.getInstance().getConnection();

		String sql = "CREATE TABLE Customers (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name VARCHAR(75), password VARCHAR(75), PRIMARY KEY(id))";
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
