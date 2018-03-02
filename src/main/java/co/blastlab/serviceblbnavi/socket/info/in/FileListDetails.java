package co.blastlab.serviceblbnavi.socket.info.in;

import co.blastlab.serviceblbnavi.socket.info.Info;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListDetails extends Info {
	public String path;
	public int fsize;
	public byte[] md5;
	public int fCRC;
	public long createdUTC;
}
