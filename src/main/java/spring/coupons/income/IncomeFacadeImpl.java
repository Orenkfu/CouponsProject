package spring.coupons.income;

import java.util.Collection;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class IncomeFacadeImpl implements IncomeFacade {
	@Autowired
	private JmsTemplate jms;
	@Autowired
	private IncomeDao incDao;

	@Override
	public Income storeIncome(Income income) {
		incDao.save(income);
		return income;
	}

	@Override
	public void sendIncome(Income income) {
		String xml = income.toXml();
		jms.send("WinnieThePoohQueue", new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(xml);
			}
		});

	}

	@Override
	public Collection<Income> getAllIncome() {
		return incDao.findAll();

	}

	@Override
	public Collection<Income> getCompanyIncome(long companyId) {
		return incDao.getByCompany(companyId, IncomeType.PURCHASE);
	}

	@Override
	public Collection<Income> getCustomerIncome(long customerId) {
		return incDao.getByCustomer(customerId, IncomeType.PURCHASE);
	}

}
