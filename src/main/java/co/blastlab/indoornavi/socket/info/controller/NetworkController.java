package co.blastlab.indoornavi.socket.info.controller;

import co.blastlab.indoornavi.socket.info.server.file.in.Deleted;
import co.blastlab.indoornavi.socket.info.server.file.in.FileAcknowledge;
import co.blastlab.indoornavi.socket.info.server.file.in.FileListSummary;
import lombok.Getter;

import javax.ejb.Singleton;
import javax.websocket.Session;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Singleton
public class NetworkController {
	private Set<Network> networks = new HashSet<>();

	public void registerSink(Session session, DeviceStatus deviceStatus) {
		this.networks.add(new Network(session, deviceStatus));
	}

	public void unregister(Session session) {
		this.networks.removeIf(network -> network.getSession().getId().equals(session.getId()));
	}

	public Optional<Network> getBySinkShortId(Integer sinkShortId) {
		return this.networks.stream().filter(futureWrapper -> sinkShortId.equals(futureWrapper.getSink().getDevice().getShortId())).findFirst();
	}

	public Optional<Network> getBySession(Session session) {
		return this.networks.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
	}

	public Optional<Network> getByTagShortId(Integer tagShortId) {
		return this.networks.stream().filter(futureWrapper -> futureWrapper.getTags().stream().map(deviceStatus -> deviceStatus.getDevice().getShortId()).collect(Collectors.toList()).contains(tagShortId)).findFirst();
	}

	public void resolve(Session session, FileListSummary fileListSummary) {
		Optional<Network> first = this.networks.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileListSummaryFuture().complete(fileListSummary));
	}

	public void resolve(Session session, Deleted deleted) {
		Optional<Network> first = this.networks.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileDeletionStatus().complete(deleted));
	}

	public void resolve(Session session, FileAcknowledge ack) {
		Optional<Network> first = this.networks.stream().filter(futureWrapper -> session.getId().equals(futureWrapper.getSession().getId())).findFirst();
		first.ifPresent(futureWrapper -> futureWrapper.getFileUploadFuture().complete(ack));
	}

	public Optional<DeviceStatus> getDeviceStatus(Integer shortId) {
		Optional<Network> bySinkShortId = getBySinkShortId(shortId);
		if (bySinkShortId.isPresent()) {
			return Optional.of(bySinkShortId.get().getSink());
		}
		Optional<Network> byAnchorShortId = getByAnchorShortId(shortId);
		if (byAnchorShortId.isPresent()) {
			return byAnchorShortId.get().getAnchors().stream().filter(deviceStatus -> deviceStatus.getDevice().getShortId().equals(shortId)).findFirst();
		}
		Optional<Network> byTagShortId = getByTagShortId(shortId);
		if (byTagShortId.isPresent()) {
			return byTagShortId.get().getTags().stream().filter(deviceStatus -> deviceStatus.getDevice().getShortId().equals(shortId)).findFirst();
		}
		return Optional.empty();
	}

	public void updateLastTimeUpdated(Integer shortId) {
		Optional<DeviceStatus> deviceStatusOptional = this.getDeviceStatus(shortId);
		deviceStatusOptional.ifPresent((deviceStatus -> {
			deviceStatus.setLastTimeUpdated(new Date());
		}));
	}

	private Optional<Network> getByAnchorShortId(Integer anchorShortId) {
		return this.networks.stream().filter(futureWrapper -> futureWrapper.getAnchors().stream().map(deviceStatus -> deviceStatus.getDevice().getShortId()).collect(Collectors.toList()).contains(anchorShortId)).findFirst();
	}
}
