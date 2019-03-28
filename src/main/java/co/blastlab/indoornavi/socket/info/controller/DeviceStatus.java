package co.blastlab.indoornavi.socket.info.controller;

import co.blastlab.indoornavi.dto.uwb.UwbDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Setter
@Getter
@NoArgsConstructor
public class DeviceStatus {
	private UwbDto device;
	private Status status;
	private Date lastTimeUpdated;
	private Date restartingStartedTime;
	private Integer checkVersionAfterRestartCount = 0;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private CompletableFuture<Void> updateFinished = new CompletableFuture<>();
	private Integer restartCount = 0;

	public DeviceStatus(UwbDto device, Status status) {
		this.device = device;
		this.status = status;
		this.lastTimeUpdated = new Date();
	}

	public enum Status {
		OFFLINE,
		ONLINE,
		UPDATING,
		UPDATED,
		RESTARTING
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DeviceStatus that = (DeviceStatus) o;
		return Objects.equal(device.getShortId(), that.device.getShortId());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(device.getShortId());
	}
}
