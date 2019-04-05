package co.blastlab.indoornavi.dto.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PrePublishReportItem {
	private PrePublishReportItemCode code;
	private String message;
	private Map<String, Object> params = new HashMap<>();

	public PrePublishReportItem(PrePublishReportItemCode code, Map<String, Object> params) {
		this.code = code;
		this.params = params;
		this.message = code.getMessage();
	}
}
