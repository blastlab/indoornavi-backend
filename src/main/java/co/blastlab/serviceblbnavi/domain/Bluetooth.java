package co.blastlab.serviceblbnavi.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bluetooth extends Device {
	@Column(unique = true)
	@Min(Short.MAX_VALUE)
	@Max(Integer.MAX_VALUE)
	private Integer minor;

	@Column(unique = true)
	@Min(Short.MAX_VALUE)
	@Max(Integer.MAX_VALUE)
	private Integer major;

	private Short power;
}
