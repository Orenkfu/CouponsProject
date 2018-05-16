package spring.coupons;

public class Message {
	private String message;
	private String cause;

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

	public Message(String message, String cause) {
		super();
		this.message = message;
		this.cause = cause;
	}

	public Message() {
	}

	public Message(String message) {
		super();
		this.message = message;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", cause=" + cause + "]";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
