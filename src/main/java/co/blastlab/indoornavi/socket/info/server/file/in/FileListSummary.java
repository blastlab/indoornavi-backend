package co.blastlab.indoornavi.socket.info.server.file.in;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
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
