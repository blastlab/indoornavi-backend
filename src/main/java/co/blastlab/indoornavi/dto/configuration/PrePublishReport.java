package co.blastlab.indoornavi.dto.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrePublishReport {
	private List<PrePublishReportItem> items = new ArrayList<>();
}
