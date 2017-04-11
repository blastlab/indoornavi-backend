package co.blastlab.serviceblbnavi.dto.floor;

import lombok.Getter;
import lombok.Setter;

import javax.ws.rs.FormParam;

@Getter
@Setter
public class ImageUpload {

	@FormParam("image")
	private byte[] image;
}
