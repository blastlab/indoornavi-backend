package co.blastlab.indoornavi.rest.facade.util.violation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExtViolationResponse {

	private String error;
	private String message;
	private String code;
}
