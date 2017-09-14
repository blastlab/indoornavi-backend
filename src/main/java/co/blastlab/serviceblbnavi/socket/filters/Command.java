package co.blastlab.serviceblbnavi.socket.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Command {
	private Type type;
	private String args;

	public enum Type {
		TOGGLE_TAG,
		SET_FLOOR,
		SET_TAGS
	}
}
