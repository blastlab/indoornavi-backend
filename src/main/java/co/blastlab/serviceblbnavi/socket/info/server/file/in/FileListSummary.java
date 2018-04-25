package co.blastlab.serviceblbnavi.socket.info.server.file.in;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileListSummary extends InfoCode {
	private int buffSize;
	private int count;
	private long freeSpace;
	private List<FileListDetails> files;
}
