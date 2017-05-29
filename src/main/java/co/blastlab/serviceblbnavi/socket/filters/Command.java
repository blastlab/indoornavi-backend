package co.blastlab.serviceblbnavi.socket.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
	private FilterType filterType;
	private String args;
}
