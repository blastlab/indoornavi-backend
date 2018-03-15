package co.blastlab.serviceblbnavi.socket.info.server.file.out;

import co.blastlab.serviceblbnavi.socket.info.server.InfoCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import static co.blastlab.serviceblbnavi.socket.info.server.file.FileInfo.FileInfoType;

@Getter
@Setter
public class Upload extends InfoCode {
	@JsonProperty("file")
	private String fileName;
	@JsonProperty("fsize")
	private int fileSize;
	private int offset;
	@JsonProperty("dsize")
	private int dataSize;
	private String data;

	public Upload(String fileName, int fileSize, int offset, int dataSize, String data) {
		super(FileInfoType.UPLOAD.getValue());
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.offset = offset;
		this.dataSize = dataSize;
		this.data = data;
	}
}
