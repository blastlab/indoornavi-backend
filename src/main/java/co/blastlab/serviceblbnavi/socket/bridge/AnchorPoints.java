package co.blastlab.serviceblbnavi.socket.bridge;

import co.blastlab.serviceblbnavi.dto.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnchorPoints {
	private Integer anchorId;
	private List<Point> points;
}
