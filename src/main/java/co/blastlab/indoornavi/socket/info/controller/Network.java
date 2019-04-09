package co.blastlab.indoornavi.socket.info.controller;

import co.blastlab.indoornavi.socket.info.server.file.in.Deleted;
import co.blastlab.indoornavi.socket.info.server.file.in.FileAcknowledge;
import co.blastlab.indoornavi.socket.info.server.file.in.FileListSummary;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.websocket.Session;
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;

@Getter
@Setter
@NoArgsConstructor
public class Network {
	private Session session;

	private DeviceStatus sink;
	private Set<DeviceStatus> anchors = new HashSet<>();
	private Set<DeviceStatus> tags = new HashSet<>();

	private CompletableFuture<FileListSummary> fileListSummaryFuture = new CompletableFuture<>();
	private CompletableFuture<Deleted> fileDeletionStatus = new CompletableFuture<>();
	private CompletableFuture<FileAcknowledge> fileUploadFuture = new CompletableFuture<>();

	private String fileName = null;
	private byte[] file;
	private Queue<Integer> toUpdateIds = new LinkedTransferQueue<>();

	Network(Session session, DeviceStatus deviceStatus) {
		this.session = session;
		this.sink = deviceStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Network that = (Network) o;
		return Objects.equal(session.getId(), that.session.getId()) && Objects.equal(sink.getDevice().getShortId(), that.sink.getDevice().getShortId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(session.getId(), sink.getDevice().getShortId());
	}

	public void checkIfLastThenClear() {
		Optional<DeviceStatus> updatingAnchor = this.getAnchors().stream().filter(anchor -> anchor.getStatus().equals(DeviceStatus.Status.UPDATING)).findAny();
		Optional<DeviceStatus> updatingTag = this.getTags().stream().filter(anchor -> anchor.getStatus().equals(DeviceStatus.Status.UPDATING)).findAny();

		if (!updatingAnchor.isPresent() && !updatingTag.isPresent() && !this.sink.getStatus().equals(DeviceStatus.Status.UPDATING)) {
			this.fileName = null;
			this.file = null;
			this.toUpdateIds.clear();
		}
	}
}
