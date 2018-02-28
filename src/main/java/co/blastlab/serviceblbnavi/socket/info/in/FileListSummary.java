package co.blastlab.serviceblbnavi.socket.info.in;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileListSummary {
	public int count;
	public long freeSpace;
	public List<FileListDetails> files;
}
