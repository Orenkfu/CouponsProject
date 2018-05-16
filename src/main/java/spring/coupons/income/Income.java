package spring.coupons.income;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "INCOME_RECORDS")
public class Income {
	private Long id;
	private long invokerId;
	private IncomeType type;
	private Date timestamp;
	private BigDecimal amount;

	public Income(long invokerId, IncomeType type, BigDecimal amount) {
		super();
		this.invokerId = invokerId;
		this.type = type;
		this.amount = amount;
		this.timestamp = new Date();
	}

	public Income() {
		super();
	}

	public Income(long invokerId, IncomeType type, Date timestamp, BigDecimal amount) {
		super();
		this.invokerId = invokerId;
		this.type = type;
		this.timestamp = timestamp;
		this.amount = amount;
	}

	public static Income fromXml(String xml) {
		return JAXB.unmarshal(new StringReader(xml), Income.class);
	}

	public String toXml() {
		StringWriter writer = new StringWriter();

		JAXB.marshal(this, writer);
		return writer.toString();

	}

	@GeneratedValue
	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getInvokerId() {
		return invokerId;
	}

	public void setInvokerId(long invokerId) {
		this.invokerId = invokerId;
	}

	@Enumerated(EnumType.STRING)
	public IncomeType getType() {
		return type;
	}

	public void setType(IncomeType type) {
		this.type = type;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
