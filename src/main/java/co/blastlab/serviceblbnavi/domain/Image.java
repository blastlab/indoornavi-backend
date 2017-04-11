package co.blastlab.serviceblbnavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
public class Image extends TrackedEntity {

	@Lob
	private byte[] bitmap;

	private Integer bitmapWidth;

	private Integer bitmapHeight;

	@OneToOne
	private Floor floor;
}
