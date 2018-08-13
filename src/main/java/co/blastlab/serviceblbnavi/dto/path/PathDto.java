package co.blastlab.serviceblbnavi.dto.path;

import co.blastlab.serviceblbnavi.dto.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PathDto {
	private Point startPoint;
	private Point endPoint;
}
