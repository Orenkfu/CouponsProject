package base.coupons.exceptions;

/**
 * thrown when passed a null variable which cannot be null.
 * 
 * @param message
 */
public class NullException extends CouponException {

	private static final long serialVersionUID = 8893031773410100065L;

	public NullException(String message) {
		super(message);

	}

}
