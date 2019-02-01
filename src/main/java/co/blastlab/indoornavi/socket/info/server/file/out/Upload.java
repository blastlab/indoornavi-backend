package co.blastlab.indoornavi.socket.info.server.file.out;

import co.blastlab.indoornavi.socket.info.server.InfoCode;
import lombok.Getter;
import lombok.Setter;

import static co.blastlab.indoornavi.socket.info.server.file.FileInfo.FileInfoType;

@Getter
@Setter
public class Upload extends InfoCode {
	private String fileName;
	private int fileSize;
	private int offset;
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
