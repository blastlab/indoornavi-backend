package co.blastlab.indoornavi.rest.facade.util.violation;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SpecificViolation {

	private String path;
	private List<String> messages;
}
