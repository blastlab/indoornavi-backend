package co.blastlab.serviceblbnavi.socket.info.future;

import co.blastlab.serviceblbnavi.socket.info.server.file.in.Acknowledge;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.Deleted;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListSummary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.websocket.Session;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
@NoArgsConstructor
public class FutureWrapper {
	private Session session;
	private Integer sinkShortId;
	private CompletableFuture<FileListSummary> fileListSummaryFuture = new CompletableFuture<>();
	private CompletableFuture<Deleted> fileDeletionStatus = new CompletableFuture<>();
	private CompletableFuture<Acknowledge> fileUploadFuture = new CompletableFuture<>();

	FutureWrapper(Session session, Integer sinkShortId) {
		this.session = session;
		this.sinkShortId = sinkShortId;
	}
}
