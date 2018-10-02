package co.blastlab.serviceblbnavi.socket.temp;

import co.blastlab.serviceblbnavi.socket.measures.DistanceMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TxtParser {
	public void parse() throws IOException, URISyntaxException {
		URI uri = this.getClass().getClassLoader().getResource("file.txt").toURI();
		List<DistanceMessage> distanceMessageList = new ArrayList<>();
		try (Stream<String> lines = Files.lines(Paths.get(uri))) {

			lines.forEach(line -> {
				String[] words = line.split("\\s+");
				if (words.length > 1 && !words[1].equals("m")) {
					String[] devices = words[1].split(">");
					int d1 = Integer.parseInt(devices[0], 16);
					int d2 = Integer.parseInt(devices[1], 16);
					if (d2 < Short.MAX_VALUE) {
						DistanceMessage distanceMessage = new DistanceMessage();
						distanceMessage.setDid1(d1);
						distanceMessage.setDid2(d2);
						distanceMessage.setDist(Integer.parseInt(words[2]));
						distanceMessageList.add(distanceMessage);
					}
				}
			});

		}

		ObjectMapper objectMapper = new ObjectMapper();
		String value = objectMapper.writeValueAsString(distanceMessageList);
		Point lastAnchorPosition = getLastAnchorPosition();
	}

	public Point getLastAnchorPosition() {
		Point point = new Point();
		float L10 = 785;
		float L20 = 627;
		float L21 = 497;
		float X2 = L10 != 0 ? (int)((L10 * L10 - L21 * L21 + L20 * L20) / (2 * L10)) : L20;
		point.x = (int)X2;
		point.y = (int)Math.sqrt(Math.abs(L20 * L20 - X2 * X2));
		return point;
	}
}
