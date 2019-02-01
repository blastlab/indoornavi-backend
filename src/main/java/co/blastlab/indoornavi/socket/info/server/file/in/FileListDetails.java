package co.blastlab.indoornavi.socket.info.server.file.in;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListDetails {
	private String path;
	private Integer fileSize;
	private String md5;
	private Integer crc;
	private Long createdUTC;
}
