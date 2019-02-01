package co.blastlab.indoornavi.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Properties {

	private Integer maxFileSize;

	private String[] allowedTypes;

}
