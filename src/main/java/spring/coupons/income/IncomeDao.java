package spring.coupons.income;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncomeDao extends JpaRepository<Income, Long> {

	@Query("SELECT i FROM Income AS i WHERE i.invokerId = :companyId AND i.type != :customerType ORDER BY i.timestamp DESC")
	public List<Income> getByCompany(@Param("companyId") long customerId,
			@Param("customerType") IncomeType customerType);

	@Query("SELECT i FROM Income AS i WHERE i.invokerId = :customerId AND i.type=:customerType ORDER BY i.timestamp DESC")
	public List<Income> getByCustomer(@Param("customerId") long customerId,
			@Param("customerType") IncomeType customerType);

}
