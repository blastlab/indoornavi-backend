package co.blastlab.serviceblbnavi.socket.info.server.file.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileListDetails {
	private String path;
	@JsonProperty("fsize")
	private Integer fileSize;
	private String md5;
	@JsonProperty("fCRC")
	private Integer crc;
	private Long createdUTC;
}
