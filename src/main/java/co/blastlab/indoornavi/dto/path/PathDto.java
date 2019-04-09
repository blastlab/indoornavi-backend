package co.blastlab.indoornavi.dto.path;

import co.blastlab.indoornavi.dto.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathDto {
	private Point startPoint;
	private Point endPoint;
}
