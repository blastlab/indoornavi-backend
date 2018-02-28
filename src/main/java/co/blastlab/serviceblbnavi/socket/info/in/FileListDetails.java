package co.blastlab.serviceblbnavi.socket.info.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListDetails {
	public String path;
	public int fsize;
	public byte[] md5;
	public short fCRC;
	public long createdUTC;
}
