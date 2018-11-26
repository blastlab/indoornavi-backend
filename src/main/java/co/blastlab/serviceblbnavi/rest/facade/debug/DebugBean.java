package co.blastlab.serviceblbnavi.rest.facade.debug;

import co.blastlab.serviceblbnavi.dao.repository.DebugReportRepository;
import co.blastlab.serviceblbnavi.domain.DebugReport;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.socket.measures.DistanceMessage;
import co.blastlab.serviceblbnavi.socket.measures.MeasuresWebSocket;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Stateless
public class DebugBean implements DebugFacade {

	@Inject
	private MeasuresWebSocket measuresWebSocket;

	@Inject
	private DebugReportRepository debugReportRepository;

	@Inject
	private ObjectMapper objectMapper;

	private Stream.Builder<String> coordinatesStreamBuilder;
	private Stream.Builder<String> rawMeasuresStreamBuilder;

	public void rawMeasureEndpoint(@Observes DistanceMessage distanceMessage) throws JsonProcessingException {
		rawMeasuresStreamBuilder.add(
			String.format("%s %s",
				new Date().getTime() / 1000,
				objectMapper.writeValueAsString(distanceMessage)
			)
		);
	}

	public void calculatedCoordinatesEndpoint(@Observes UwbCoordinatesDto coordinatesDto) {
		coordinatesStreamBuilder.add(
			String.format("%s; %s; %s; %s; %s",
				coordinatesDto.getDate().getTime() / 1000,
				Integer.toHexString(coordinatesDto.getTagShortId()),
				coordinatesDto.getPoint().getX(),
				coordinatesDto.getPoint().getY(),
				coordinatesDto.getPoint().getZ()
			)
		);
	}

	@Override
	public List<DebugReport> list() throws IOException {
		return debugReportRepository.findAll();
	}

	@Override
	public Response start() throws IOException {
		measuresWebSocket.setDebugMode(true);

		startCoordinatesFile();
		startRawMeasuresFile();

		return Response.ok().build();
	}

	@Override
	public Response stop() throws IOException {
		if (coordinatesStreamBuilder == null || rawMeasuresStreamBuilder == null) {
			return Response.status(HttpStatus.SC_BAD_REQUEST).build();
		}
		measuresWebSocket.setDebugMode(false);

		saveFile(coordinatesStreamBuilder, DebugReport.ReportType.COORDINATES);
		saveFile(rawMeasuresStreamBuilder, DebugReport.ReportType.RAW);

		coordinatesStreamBuilder = null;
		rawMeasuresStreamBuilder = null;

		return Response.ok().build();
	}

	private void startCoordinatesFile() {
		coordinatesStreamBuilder = Stream.builder();
		coordinatesStreamBuilder.add("Time; DID; X; Y; Z;\n");
	}

	private void startRawMeasuresFile() {
		rawMeasuresStreamBuilder = Stream.builder();
//		rawMeasuresStreamBuilder.add()
	}

	private void saveFile(Stream.Builder<String> streamBuilder, DebugReport.ReportType type) {
		Stream<String> stream = streamBuilder.build();
		byte[] bytes = stream.toString().getBytes();
		stream.close();
		debugReportRepository.save(new DebugReport(bytes, type));
	}
}
