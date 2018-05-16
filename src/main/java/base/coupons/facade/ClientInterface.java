package base.coupons.facade;

import java.sql.SQLException;

import base.coupons.exceptions.CouponException;

/**
 * INTERFACE NOT IN USE- I thought abstract class makes more sense for this
 * use.. but this interface can be used and is interchangeable with the abstract
 * Client class.
 * 
 *
 */
public interface ClientInterface {

	public Client login(String name, String password) throws SQLException, CouponException;;

	public abstract void dispose();
}
