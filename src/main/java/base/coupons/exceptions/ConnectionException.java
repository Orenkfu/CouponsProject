package base.coupons.exceptions;

public class ConnectionException extends Exception {

	private static final long serialVersionUID = -3372527732718553833L;

	/**
	 * thrown when Connection Pool cannot reach database.
	 * 
	 * @param message
	 */
	public ConnectionException(String message) {
		super(message);
	}
}
