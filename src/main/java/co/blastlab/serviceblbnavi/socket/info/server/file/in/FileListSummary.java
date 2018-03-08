package co.blastlab.serviceblbnavi.socket.info.server.file.in;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileListSummary extends InfoCode {
	@JsonProperty("buff_size")
	private int buffSize;
	private int count;
	@JsonProperty("free_space")
	private long freeSpace;
	private List<FileListDetails> files;
}
