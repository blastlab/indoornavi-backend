package co.blastlab.serviceblbnavi.rest.facade.util.violation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DbViolationResponse {

	private String error;
	private String message;
}
