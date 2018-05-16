package spring.coupons.income;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class IncomeListener {
	@Autowired
	private IncomeFacade incomeFacade;

	@JmsListener(destination = "WinnieThePoohQueue")
	public void readIncome(String xml) {
		Income income = Income.fromXml(xml);
		incomeFacade.storeIncome(income);

	}
}
