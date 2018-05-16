package base.coupons.beans;

import java.util.Date;

/**
 * Bean class representing coupons in this application.
 * 
 */
public class Coupon {
	private Long id;
	private String title;
	private Date startDate;
	private Date endDate;
	private int amount;
	private CouponType type;
	private String message;
	private Double price;
	private String image;
	private Long companyId;

	public Coupon(String title, Date startDate, Date endDate, int amount, CouponType type, String message, Double price,
			String image, Long companyId) {
		super();
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
		this.companyId = companyId;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Coupon() {
		super();
	}

	// (int, String, Date, Date, int, CouponType, String, double, String)
	public Coupon(String title, Date startDate, Date endDate, int amount, CouponType type, String message, Double price,
			String image) {
		super();
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
		this.amount = amount;
		this.type = type;
		this.message = message;
		this.price = price;
		this.image = image;
	}

	public Coupon(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public CouponType getType() {
		return type;
	}

	public void setType(CouponType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String toString() {

		return ("|Coupon ID: " + id + "| |Title: " + title + "| |Start Date: " + startDate + "| |End Date: " + endDate
				+ "| |Amount: " + amount + "| |Type: " + type + "| |message: " + message + "| |price: " + price
				+ "| |image: " + image + "|");
	}

}
