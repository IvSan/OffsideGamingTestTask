package xyz.hardliner.offside.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Client {
	@Id
	@GeneratedValue
	private Long id;
}
