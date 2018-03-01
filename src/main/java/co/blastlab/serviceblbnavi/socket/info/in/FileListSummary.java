package co.blastlab.serviceblbnavi.socket.info.in;

import co.blastlab.serviceblbnavi.socket.info.InfoCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileListSummary extends InfoCode {
	public int count;
	@JsonProperty("free_space")
	public long freeSpace;
	public List<FileListDetails> files;
}
