package co.blastlab.indoornavi.socket.info.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RawCommand {
	private Integer sinkShortId;
	private String value;
}
