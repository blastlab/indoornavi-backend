package co.blastlab.indoornavi.dto.configuration;

import lombok.Getter;

@Getter
public enum PrePublishReportItemCode {
	PPRC_001("The device is published on the other floor"),
	;

	private String message;

	PrePublishReportItemCode(String message) {
		this.message = message;
	}

}
