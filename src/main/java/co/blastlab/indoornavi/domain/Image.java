package co.blastlab.indoornavi.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Image extends TrackedEntity {

	@Lob
	@Column(columnDefinition="mediumblob")
	@Basic(fetch=FetchType.LAZY)
	private byte[] bitmap;

	private Integer bitmapWidth;

	private Integer bitmapHeight;

	@OneToOne(mappedBy = "image", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
	private Floor floor;
}
