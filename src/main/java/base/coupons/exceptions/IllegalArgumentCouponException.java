package base.coupons.exceptions;

/**
 * thrown when an illegal argument was given. mostly thrown when a validation
 * test fails, or if given parameters make no sense.
 * 
 * @author oren
 *
 */
public class IllegalArgumentCouponException extends CouponException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1279713351440543536L;

	/**
	 * thrown when passed argument is illegal, e.g illogical.
	 * 
	 * @param message
	 */
	public IllegalArgumentCouponException(String message) {
		super(message);
	}

}
