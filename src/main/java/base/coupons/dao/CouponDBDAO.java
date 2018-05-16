package base.coupons.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import base.coupons.beans.Coupon;
import base.coupons.beans.CouponType;
import base.coupons.daoInterface.CouponDAO;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.DoesntExistException;
import base.coupons.exceptions.NullException;

public class CouponDBDAO implements CouponDAO {
	private Connection con;
	private String sql;

	/**
	 * @param Coupon
	 * 
	 * @throws CouponException
	 *             - Coupon's ID already exists in Database.
	 * @throws NullException
	 *             - Tried to create Coupon with illegal null values.
	 */
	@Override
	public Coupon createCoupon(Coupon coupon) throws CouponException, NullException {
		sql = "INSERT INTO Coupons(title, startdate, enddate, amount, type, message, price, image) VALUES(?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt;
		con = ConnectionPool.getInstance().getConnection();

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			pstmt.setDate(2, new java.sql.Date(coupon.getStartDate().getTime()));
			pstmt.setDate(3, new java.sql.Date(coupon.getEndDate().getTime()));
			pstmt.setInt(4, coupon.getAmount());
			pstmt.setString(5, coupon.getType().name());
			pstmt.setString(6, coupon.getMessage());
			pstmt.setDouble(7, coupon.getPrice());
			pstmt.setString(8, coupon.getImage());

			pstmt.executeUpdate();

			sql = "SELECT * FROM Coupons WHERE(title = ?)";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			coupon.setId(rs.getLong(1));
			// System.out.println("TEST" + coupon);
			return coupon;
		} catch (SQLException e) {
			throw new CouponException("Coupon could not be created, already exists.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

	}

	/**
	 * * attempts to delete coupon from Coupons and returns whether or not it was
	 * successful
	 * 
	 * @param coupon
	 * @return boolean representing whether the coupon was deleted.
	 * @throws SQLException
	 * @throws CouponException
	 */
	@Override
	public boolean removeCoupon(Coupon coupon) throws CouponException {
		long id = coupon.getId();
		// System.out.println("====TEST=== STARTING DELETION PROCESS");
		con = ConnectionPool.getInstance().getConnection();
		sql = "DELETE FROM Coupons WHERE id = ?";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, id);
			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				return false;
			}
			// System.out.println("===TEST=== DELETED COUPON" + coupon);
			return true;
		} catch (SQLException e) {
			throw new CouponException("Failed to execute action.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}
	}

	/**
	 * getter for Coupons in COUPONS Table.
	 * 
	 * @param
	 * 
	 * 			id
	 *            representing Coupon in Coupons Table.
	 * @return Coupon bean Object
	 * @throws CouponException
	 *             if database threw an exception.
	 * @throws DoesntExistException
	 *             if given ID could not be found.
	 */
	@Override
	public Coupon getCoupon(Long id) throws CouponException {
		sql = "SELECT * FROM Coupons WHERE (ID = ?)";
		con = ConnectionPool.getInstance().getConnection();
		PreparedStatement pstmt;
		Coupon coupon = new Coupon();
		try {

			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getTimestamp(3));
				coupon.setEndDate(rs.getTimestamp(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				return coupon;
			} else {
				throw new DoesntExistException("Could not find Coupon with ID: " + id);
			}
		} catch (SQLException e) {
			throw new CouponException("Failed to get Coupon ID.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * getter for Coupons in COMPANY_COUPONS Table.
	 * 
	 * @param representing
	 *            Coupon in Coupons Table.
	 * @return Coupon Object
	 * @throws CouponException
	 *             if given ID could not be found.
	 * @throws NullException
	 *             if given null ID.
	 */
	public Coupon getCouponFromComp(long id) throws CouponException, NullException {

		sql = "SELECT * FROM Company_Coupon WHERE (COUPONID = ?)";
		con = ConnectionPool.getInstance().getConnection();
		PreparedStatement pstmt;
		Coupon coupon = new Coupon();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				coupon.setId(rs.getLong(2));

				return coupon;
			} else {
				throw new CouponException("Could not find Coupon with ID: " + id);
			}
		} catch (NullPointerException e) {
			throw new NullException("Must specify Coupon ID.");
		} catch (SQLException e) {
			throw new CouponException("Failed to get Coupon ID.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}
	}

	/**
	 * getter for all coupons in Coupon Table.
	 * 
	 * @return ArrayList<Coupon> containing all coupons in Coupons Table.
	 * @throws CouponException
	 *             if data could not be obtained.
	 */
	@Override
	public List<Coupon> getAllCoupons() throws CouponException {
		sql = "SELECT * FROM Coupons";
		PreparedStatement pstmt;
		con = ConnectionPool.getInstance().getConnection();
		List<Coupon> coupons = new ArrayList<>();

		try {
			pstmt = con.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getTimestamp(3));
				coupon.setEndDate(rs.getTimestamp(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				coupons.add(coupon);

			}
		} catch (SQLException e) {
			throw new CouponException("database error has occurred.");

		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

		return coupons;
	}

	// stupid derby
	// offset = skip // start from.....
	// fetch = bring me those // end after...
	public ArrayList<Coupon> getCouponsByPage(int page, int size) throws CouponException {
		sql = "SELECT * FROM Coupons OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
		PreparedStatement pstmt;
		con = ConnectionPool.getInstance().getConnection();
		List<Coupon> coupons = new ArrayList<>();

		try {
			pstmt = con.prepareStatement(sql);
			int currentPageStart = size * page;
			pstmt.setInt(1, currentPageStart);
			pstmt.setInt(2, size);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getTimestamp(3));
				coupon.setEndDate(rs.getTimestamp(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				coupons.add(coupon);

			}
		} catch (SQLException e) {
			throw new CouponException("database error has occurred.");

		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

		return (ArrayList<Coupon>) coupons;
	}

	/**
	 * Getter for all Coupons of certain CouponType from Coupons Table
	 * 
	 * @param CouponType
	 *            Enum containing all types of coupons
	 * @return ArrayList<Coupon> of all Coupons of couponType.
	 * @throws CouponException
	 *             no coupons of couponType could be found
	 * @throws CouponException
	 *             could not perform action.
	 */
	@Override
	public List<Coupon> getCouponsByType(CouponType couponType, int page, int size) throws CouponException {
		sql = "SELECT * FROM Coupons WHERE (type = ?) OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
		con = ConnectionPool.getInstance().getConnection();
		PreparedStatement pstmt;
		List<Coupon> coupons = new ArrayList<>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, couponType.name());
			int currentPageStart = size * page;
			pstmt.setInt(2, currentPageStart);
			pstmt.setInt(3, size);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getTimestamp(3));
				coupon.setEndDate(rs.getTimestamp(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				coupons.add(coupon);
			}

			return coupons;

		} catch (SQLException e) {
			throw new CouponException("database error has occurred.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * updates Coupon attributes: endDate, price IN Coupon Table
	 * 
	 * @param coupon
	 *            updated Coupon
	 * @return updated Coupon
	 */
	@Override
	public void updateCoupon(Coupon coupon) throws CouponException {
		sql = "UPDATE Coupons SET endDate = ?, price = ? WHERE (id = ?)";
		con = ConnectionPool.getInstance().getConnection();

		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(3, coupon.getId());
			pstmt.setDate(1, new java.sql.Date(coupon.getEndDate().getTime()));
			pstmt.setDouble(2, coupon.getPrice());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponException("Coupon Could not be updated.");

		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * updates coupon amount attribute, called by purchaseCoupon method of
	 * CustomerFacade.
	 * 
	 * @param coupon
	 * @throws CouponException
	 *             - if database threw an exception.
	 */
	public void updateAmount(Coupon coupon) throws CouponException {
		sql = "UPDATE Coupons SET amount = ? WHERE (id = ?)";
		con = ConnectionPool.getInstance().getConnection();
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, coupon.getAmount());
			pstmt.setLong(2, coupon.getId());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			throw new CouponException("database error has occurred.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

	/**
	 * getter for coupon ID. built for auto-generating purposes.
	 * 
	 * @param coupon
	 * @return long id attribute of given coupon.
	 * @throws SQLException
	 * @throws CouponException
	 */
	public long getCouponId(Coupon coupon) throws CouponException {
		con = ConnectionPool.getInstance().getConnection();
		sql = "SELECT * FROM Companies WHERE (title = ?)";
		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setString(1, coupon.getTitle());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Coupon currentCoupon = new Coupon();
				currentCoupon.setId(rs.getLong(1));
				currentCoupon.setTitle(rs.getString(2));

				// searching for coupon by Unique attribute - Title
				if (coupon.getTitle().equals(currentCoupon.getTitle())) {
					long id = currentCoupon.getId();
					return id;
				}
			}
			throw new CouponException("Coupon" + coupon + "was not found in database");

		} catch (SQLException e) {
			throw new CouponException("Could not get coupon " + coupon.getTitle() + "'s ID");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);

		}

	}

	/**
	 * * attempts to delete coupon from customer_coupon and returns whether or not
	 * it was successful
	 * 
	 * @param coupon
	 * @return boolean representing whether the coupon was deleted.
	 * @throws SQLException
	 * @throws CouponException
	 */
	public boolean removeCouponFromCustomer(Coupon coupon) throws CouponException {
		con = ConnectionPool.getInstance().getConnection();
		PreparedStatement pstmt;
		sql = "DELETE FROM Customer_Coupon WHERE coupon_id = ?";
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, coupon.getId());
			int rows = pstmt.executeUpdate();
			if (rows == 0) {
				return false;
			}
			return true;
		} catch (SQLException e) {
			throw new CouponException("failed to remove coupon from customer.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

	}

	/**
	 * attempts to delete coupon from company_coupon and returns whether or not it
	 * was successful
	 * 
	 * @param coupon
	 * @return boolean representing whether the coupon was deleted.
	 * @throws CouponException
	 *             if database update failed.
	 */
	public boolean removeCouponFromCompany(Coupon coupon) throws CouponException {
		con = ConnectionPool.getInstance().getConnection();
		sql = "DELETE FROM Company_Coupon WHERE couponid = ?";

		try {
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, coupon.getId());

			int affectedRows = pstmt.executeUpdate();
			if (affectedRows == 0) {
				return false;
			} else
				// System.out.println("Coupon " + coupon.getTitle() + " Removed from company.");
				return true;
		} catch (SQLException e) {
			throw new CouponException("Failed to delete coupon from company.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}

	}

	public List<Coupon> getCouponsByPrice(double price, Integer page, int size) throws CouponException {
		sql = "SELECT * FROM Coupons WHERE (price < ?) OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
		con = ConnectionPool.getInstance().getConnection();
		PreparedStatement pstmt;
		List<Coupon> coupons = new ArrayList<>();
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setDouble(1, price);
			int currentPageStart = size * page;
			pstmt.setInt(2, currentPageStart);
			pstmt.setInt(3, size);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Coupon coupon = new Coupon();
				coupon.setId(rs.getLong(1));
				coupon.setTitle(rs.getString(2));
				coupon.setStartDate(rs.getTimestamp(3));
				coupon.setEndDate(rs.getTimestamp(4));
				coupon.setAmount(rs.getInt(5));
				coupon.setType(CouponType.valueOf(rs.getString(6)));
				coupon.setMessage(rs.getString(7));
				coupon.setPrice(rs.getDouble(8));
				coupon.setImage(rs.getString(9));
				coupons.add(coupon);
			}

			return coupons;

		} catch (SQLException e) {
			throw new CouponException("database error has occurred.");
		} finally {
			ConnectionPool.getInstance().returnConnection(con);
		}
	}

}
