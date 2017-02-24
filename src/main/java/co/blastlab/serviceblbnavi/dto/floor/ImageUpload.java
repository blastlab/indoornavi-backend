package co.blastlab.serviceblbnavi.dto.floor;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

@Getter
@Setter
public class ImageUpload {
    @FormParam("floor")
    @NotNull
    private Long floorId;

    @FormParam("image")
    private byte[] image;
}
