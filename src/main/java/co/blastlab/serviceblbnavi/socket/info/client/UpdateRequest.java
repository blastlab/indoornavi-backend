package co.blastlab.serviceblbnavi.socket.info.client;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UpdateRequest {
	private List<Integer> devicesShortIds = new ArrayList<>();
	private String base64file;
}
