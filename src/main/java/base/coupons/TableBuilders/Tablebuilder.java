package base.coupons.TableBuilders;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import base.coupons.dao.ConnectionPool;
import base.coupons.exceptions.CouponException;

public class Tablebuilder {
	private enum TableName {
		Coupons, Companies, Customers, Company_Coupon, Customer_Coupon;
	}

	private static final String BUILD_COUPON_SQL = "CREATE TABLE Coupons (id BIGINT NOT NULL, title VARCHAR(75) UNIQUE, startdate DATE, enddate DATE, amount INT, type VARCHAR(50), message VARCHAR(150), price FLOAT, image VARCHAR(100), PRIMARY KEY(id))";
	private static final String BUILD_COMPANY_SQL = "CREATE TABLE Companies (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name VARCHAR(75) UNIQUE, password VARCHAR(50), email VARCHAR(50), PRIMARY KEY(id))";
	private static final String BUILD_CUSTOMER_SQL = "CREATE TABLE Customers (id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name VARCHAR(75), password VARCHAR(75), PRIMARY KEY(id))";
	private static final String BUILD_COMPCOUPON_SQL = "CREATE TABLE Company_Coupon (companyid BIGINT NOT NULL, couponid BIGINT NOT NULL, PRIMARY KEY(companyid, couponid))";
	private static final String BUILD_CUSTCOUPON_SQL = "CREATE TABLE Customer_Coupon(CUST_ID BIGINT, COUPON_ID BIGINT, PRIMARY KEY(CUST_ID, COUPON_ID))";
	private static final String DROP_BASE_SQL = "DROP TABLE ";

	public void buildCompTable() {
		Connection con = ConnectionPool.getInstance().getConnection();

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(BUILD_COMPANY_SQL);
			System.out.println(BUILD_COMPANY_SQL);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}

	}

	public void buildCompanyCouponTable() {
		Connection con = ConnectionPool.getInstance().getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(BUILD_COMPCOUPON_SQL);
			System.out.println(BUILD_COMPCOUPON_SQL);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

	}

	public void buildCouponTable() {
		Connection con = ConnectionPool.getInstance().getConnection();

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(BUILD_COUPON_SQL);
			System.out.println(BUILD_COUPON_SQL);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

	}

	public void buildCustCouponTable() {
		Connection con = ConnectionPool.getInstance().getConnection();
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(BUILD_CUSTCOUPON_SQL);
			System.out.println(BUILD_CUSTCOUPON_SQL);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}
	}

	public void buildCustomerTable() {
		Connection con = ConnectionPool.getInstance().getConnection();

		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(BUILD_CUSTOMER_SQL);
			System.out.println(BUILD_CUSTOMER_SQL);
		} catch (SQLException e) {
			CouponException ex = new CouponException("Error: Could not connect to the database");
			System.out.println(ex.getMessage());
			e.printStackTrace();
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	public void buildAllTables() {
		buildCompTable();
		buildCouponTable();
		buildCustomerTable();
		buildCustCouponTable();
		buildCompanyCouponTable();
	}

	public void dropTable(TableName name) {
		Connection con = ConnectionPool.getInstance().getConnection();
		// TODO Impossible to use pstmt for creating/dropping tables, SOLUTION?
		String sql = DROP_BASE_SQL + name;
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

	public void dropAllTables() {
		Arrays.asList(TableName.values()).forEach(name -> dropTable(name));

	}

	public static void main(String[] args) {
		// new Tablebuilder().buildAllTables();
		// new Tablebuilder().dropAllTables();
		// RUN METHOD TO BUILD/DROP TABLES...
	}
}
