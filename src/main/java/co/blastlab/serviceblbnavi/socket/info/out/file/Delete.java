package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.Info;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Delete extends Info {
	private String path;

	public Delete(String path) {
		super(FileArgsType.DELETE.getValue());
		this.path = path;
	}
}
