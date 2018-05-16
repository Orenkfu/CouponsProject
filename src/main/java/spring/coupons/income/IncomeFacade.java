package spring.coupons.income;

import java.util.Collection;

public interface IncomeFacade {
	// dao income store
	public Income storeIncome(Income income);

	// jms message sender
	public void sendIncome(Income income);

	public Collection<Income> getAllIncome();

	public Collection<Income> getCompanyIncome(long companyId);

	public Collection<Income> getCustomerIncome(long customerId);
}
