package xyz.hardliner.offside.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@SuppressWarnings("WeakerAccess")
public class ConsumptionRecord {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne
	private Client client;
	private ConsumptionType type;
	private BigDecimal value;
	private LocalDateTime date;

	public ConsumptionRecord(Client client, ConsumptionType type, BigDecimal value) {
		this.client = client;
		this.type = type;
		this.value = value;
		date = LocalDateTime.now();
	}

	public ConsumptionRecord(Client client, ConsumptionType type) {
		this(client, type, new BigDecimal(0));
	}
}
