package co.blastlab.serviceblbnavi.dto.floor;

import com.sun.jersey.core.header.FormDataContentDisposition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

@Getter
@Setter
public class ImageUpload {

	@FormParam("floor")
	@NotNull
	@ApiModelProperty(example = "1")
	private Long floorId;

	@FormParam("image")
	@ApiModelProperty(hidden = true)
	private FormDataContentDisposition fileDetail;

	@FormParam("image")
	private byte[] image;
}
