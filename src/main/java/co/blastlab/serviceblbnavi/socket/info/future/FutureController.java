package co.blastlab.serviceblbnavi.socket.info.future;

import co.blastlab.serviceblbnavi.socket.info.server.file.in.Acknowledge;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.Deleted;
import co.blastlab.serviceblbnavi.socket.info.server.file.in.FileListSummary;
import lombok.Getter;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class FutureController {
	private List<FutureWrapper> futures = new ArrayList<>();

	public void register(Session session, Integer sinkShortId) {
		this.futures.add(new FutureWrapper(session, sinkShortId));
	}

	public void unregister(Session session) {
		this.futures.removeIf(futureWrapper -> futureWrapper.getSession().getId().equals(session.getId()));
	}

	public Optional<FutureWrapper> getBySinkShortId(Integer sinkShortId) {
		return this.futures.stream().filter(futureWrapper -> sinkShortId.equals(futureWrapper.getSinkShortId())).findFirst();
	}

	public List<Integer> getAllSinksIds() {
		return this.futures.stream().map(FutureWrapper::getSinkShortId).collect(Collectors.toList());
	}

	public void resolve(Session session, FileListSummary fileListSummary) {
		Optional<FutureWrapper> first = this.futures.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileListSummaryFuture().complete(fileListSummary));
	}

	public void resolve(Session session, Deleted deleted) {
		Optional<FutureWrapper> first = this.futures.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileDeletionStatus().complete(deleted));
	}

	public void resolve(Session session, Acknowledge ack) {
		Optional<FutureWrapper> first = this.futures.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileUploadFuture().complete(ack));
	}

}
