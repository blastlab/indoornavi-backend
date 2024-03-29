package co.blastlab.indoornavi.socket.measures;

import co.blastlab.indoornavi.ext.deserializer.JsonTimestampDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class DistanceMessage {
	private Integer did1;
	private Integer did2;
	private Integer dist;
	private Double signal;
	@JsonDeserialize(using = JsonTimestampDeserializer.class)
	private Timestamp time;
	private Double fpp;
}
