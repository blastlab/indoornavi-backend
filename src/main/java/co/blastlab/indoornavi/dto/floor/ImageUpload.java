package co.blastlab.indoornavi.dto.floor;

import lombok.Getter;
import lombok.Setter;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;

@Getter
@Setter
public class ImageUpload {

	@PartType("application/octet-stream")
	@FormParam("image")
	private byte[] image;
}
