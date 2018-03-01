package co.blastlab.serviceblbnavi.socket.info.out.file;

import co.blastlab.serviceblbnavi.socket.info.Info;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Delete extends Info {
	String path;
}
