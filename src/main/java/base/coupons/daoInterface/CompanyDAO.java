package base.coupons.daoInterface;

import java.util.List;

import base.coupons.beans.Company;
import base.coupons.beans.Coupon;
import base.coupons.exceptions.CouponException;
import base.coupons.exceptions.NullException;

public interface CompanyDAO {
	public Company createCompany(Company company) throws NullException, CouponException;

	public boolean removeCompany(Company company) throws CouponException;

	public void updateCompany(Company company) throws CouponException;

	public Company getCompany(Long id) throws CouponException;

	public List<Company> getAllCompanies() throws CouponException;

	public List<Coupon> getCoupons(Company company) throws CouponException;

	public long login(String compName, String password) throws CouponException;

}
