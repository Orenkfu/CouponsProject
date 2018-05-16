package base.coupons.exceptions;

/**
 * thrown when reaching for data/objects that could not be found.
 * 
 * @author oren
 *
 */
public class DoesntExistException extends CouponException {

	private static final long serialVersionUID = -1244892444424273322L;

	public DoesntExistException(String message) {
		super(message);
	}

}
