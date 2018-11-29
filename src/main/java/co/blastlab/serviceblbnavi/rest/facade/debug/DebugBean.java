package co.blastlab.serviceblbnavi.rest.facade.debug;

import co.blastlab.serviceblbnavi.dao.repository.DebugReportRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
import co.blastlab.serviceblbnavi.domain.DebugReport;
import co.blastlab.serviceblbnavi.dto.report.UwbCoordinatesDto;
import co.blastlab.serviceblbnavi.socket.measures.DistanceMessage;
import co.blastlab.serviceblbnavi.socket.measures.MeasuresWebSocket;
import co.blastlab.serviceblbnavi.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class DebugBean implements DebugFacade {

	@Inject
	private MeasuresWebSocket measuresWebSocket;

	@Inject
	private DebugReportRepository debugReportRepository;

	@Inject
	private ObjectMapper objectMapper;

	@Inject
	private SinkRepository sinkRepository;

	@Inject
	private Logger logger;

	private Stream.Builder<String> coordinatesStreamBuilder;
	private Stream.Builder<String> rawMeasuresStreamBuilder;

	public void rawMeasureEndpoint(@Observes DistanceMessage distanceMessage) throws JsonProcessingException {
		logger.trace("Adding raw measure to file: {}", distanceMessage.toString());
		rawMeasuresStreamBuilder.add(
			String.format("%s %s\n",
				getCurrentTimeInSeconds(),
				objectMapper.writeValueAsString(distanceMessage)
			)
		);
	}

	public void calculatedCoordinatesEndpoint(@Observes UwbCoordinatesDto coordinatesDto) {
		logger.trace("Adding coordinates to file: {}", coordinatesDto.toString());
		coordinatesStreamBuilder.add(
			String.format("%s; %s; %s; %s; %s\n",
				convertToSeconds(coordinatesDto.getDate().getTime()),
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
	public Response download(Long id) {
		logger.trace("Trying to download debug report file: {}", id);
		Optional<DebugReport> debugReportOptional = debugReportRepository.findOptionalById(id);
		if (debugReportOptional.isPresent()) {
			DebugReport debugReport = debugReportOptional.get();

			StreamingOutput stream = outputStream -> {
				try {
					outputStream.write(debugReport.getData());
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			};

			logger.trace("Streaming debug report file: {}", debugReport.getName());
			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", String.format("attachment;filename=%s", debugReport.getName())).build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response start(Long sinkId) throws IOException {
		logger.trace("Starting to collect debug files for sink {}", sinkId);
		measuresWebSocket.setDebugMode(true);

		startCoordinatesFile();
		startRawMeasuresFile(sinkId);

		return Response.ok().build();
	}

	@Override
	public Response stop() throws IOException {
		if (coordinatesStreamBuilder == null || rawMeasuresStreamBuilder == null) {
			logger.trace("You have to run start endpoint first.");
			return Response.status(HttpStatus.SC_BAD_REQUEST).build();
		}
		measuresWebSocket.setDebugMode(false);

		logger.trace("Save debug report files");

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

	private void startRawMeasuresFile(Long sinkId) {
		rawMeasuresStreamBuilder = Stream.builder();
		sinkRepository.findOptionalById(sinkId).ifPresent(sink -> {
			rawMeasuresStreamBuilder.add(
				String.format("%s sink %s %s\n",
					getCurrentTimeInSeconds(),
					Integer.toHexString(sink.getShortId()),
					sink.getMac() == null ? 0 : sink.getMac()
				)
			);
			sink.getAnchors().forEach((anchor -> {
				rawMeasuresStreamBuilder.add(
					String.format("%s anchor %s %s %s %s\n",
						getCurrentTimeInSeconds(),
						Integer.toHexString(anchor.getShortId()),
						anchor.getX(),
						anchor.getY(),
						anchor.getZ()
					)
				);
			}));
		});
	}

	private void saveFile(Stream.Builder<String> streamBuilder, DebugReport.ReportType type) {
		Stream<String> stream = streamBuilder.build();
		String stringData = stream.collect(Collectors.joining());
		stream.close();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		debugReportRepository.save(
			new DebugReport(
				stringData.getBytes(),
				String.format("%s-%s.txt", type.toString(), formatter.format(new Date())),
				type
			)
		);
	}

	private String convertToSeconds(long milis) {
		DecimalFormat df = new DecimalFormat("0.0000");
		return df.format((double) milis / 1000);
	}

	private String getCurrentTimeInSeconds() {
		return convertToSeconds(new Date().getTime());
	}

}
