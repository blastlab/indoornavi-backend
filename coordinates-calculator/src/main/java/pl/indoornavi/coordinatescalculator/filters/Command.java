package pl.indoornavi.coordinatescalculator.filters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Command {
	private Type type;
	private String args;

	public enum Type {
		TOGGLE_TAG,
		SET_FLOOR,
		SET_TAGS
    }
}
