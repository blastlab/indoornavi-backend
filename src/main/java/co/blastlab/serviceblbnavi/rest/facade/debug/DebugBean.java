package co.blastlab.serviceblbnavi.rest.facade.debug;

import co.blastlab.serviceblbnavi.dao.repository.DebugReportRepository;
import co.blastlab.serviceblbnavi.dao.repository.SinkRepository;
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
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
	public Response download(Long id) {
//		logger.debug("Trying to download image id {}", id);
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

			return Response.ok(stream, MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition", "attachment;filename=classes.jar").build();
		}
		throw new EntityNotFoundException();
	}

	@Override
	public Response start(Long sinkId) throws IOException {
		measuresWebSocket.setDebugMode(true);

		startCoordinatesFile();
		startRawMeasuresFile(sinkId);

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

	private void startRawMeasuresFile(Long sinkId) {
		rawMeasuresStreamBuilder = Stream.builder();
		sinkRepository.findOptionalById(sinkId).ifPresent(sink -> {
			rawMeasuresStreamBuilder.add(
				String.format("%s sink %s %s",
					new Date().getTime() / 1000,
					Integer.toHexString(sink.getShortId()),
					sink.getMac() == null ? 0 : sink.getMac()
				)
			);
			sink.getAnchors().forEach((anchor -> {
				rawMeasuresStreamBuilder.add(
					String.format("%s anchor %s %s %s %s",
						new Date().getTime() / 1000,
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
		Byte[] bytes = stream.toArray(Byte[]::new);
		stream.close();
		byte[] data = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			data[i] = bytes[i];
		}
		debugReportRepository.save(new DebugReport(data, type));
	}
}
