package base.coupons.daoInterface;

import java.util.List;

import base.coupons.beans.Coupon;
import base.coupons.beans.CouponType;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.NullException;

public interface CouponDAO {
	public Coupon createCoupon(Coupon coupon) throws CouponException, NullException;

	public boolean removeCoupon(Coupon coupon) throws CouponException;

	public void updateCoupon(Coupon coupon) throws CouponException;

	public Coupon getCoupon(Long id) throws CouponException, NullException;

	public List<Coupon> getAllCoupons() throws CouponException;

	// public List<Coupon> getCouponByType(CouponType couponType) throws
	// CouponException;

	List<Coupon> getCouponsByType(CouponType couponType, int page, int size) throws CouponException;

}
