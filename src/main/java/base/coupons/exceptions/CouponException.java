package base.coupons.exceptions;

public class CouponException extends Exception {

	private static final long serialVersionUID = -6281404748082006327L;

	/**
	 * general application exception, mainly thrown when a database error occurs, or
	 * if a general illegal request has been given.
	 * 
	 * @param message
	 */
	public CouponException(String message) {
		super(message);
	}

}
