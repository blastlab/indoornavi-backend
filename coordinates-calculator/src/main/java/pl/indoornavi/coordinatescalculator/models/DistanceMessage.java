package pl.indoornavi.coordinatescalculator.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DistanceMessage {
    private Integer did1;
    private Integer did2;
    private Integer dist;
    private Long time;

    private static Logger logger = LoggerFactory.getLogger(DistanceMessage.class);

    @JsonSetter("time")
    public void setTime(Double timeInSeconds) {
        this.time = (long)(timeInSeconds * 1000);
    }
}
