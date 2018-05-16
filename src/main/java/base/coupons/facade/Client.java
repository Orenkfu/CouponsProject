package base.coupons.facade;

import java.sql.SQLException;

import base.coupons.exceptions.CouponException;

/**
 * practical superclass of all Client Facades.
 * 
 * @author oren
 *
 */
public abstract class Client {

	public abstract Long login(String compName, String password) throws SQLException, CouponException;

}
