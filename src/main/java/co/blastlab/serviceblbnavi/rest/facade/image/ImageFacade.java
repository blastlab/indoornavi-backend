package co.blastlab.serviceblbnavi.rest.facade.image;

import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import co.blastlab.serviceblbnavi.properties.Properties;
import io.swagger.annotations.*;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/images")
@Api("/images")
@TokenAuthorization
public interface ImageFacade {

	@POST
	@Path("/{floorId: \\d+}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	@ApiOperation(value = "upload image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	Response uploadImage(@ApiParam(value = "floorId", required = true) @PathParam("floorId") @Valid @NotNull Long floorId,
		@Valid @MultipartForm ImageUpload image) throws IOException;

	@GET
	@Path("/{id: \\d+}")
	@Produces({"image/png", "image/jpeg"})
	@ApiOperation(value = "download image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist or it has no image")
	})
	Response downloadImage(@PathParam("id") Long floorId);

	@GET
	@Path("/configuration")
	@ApiOperation(value = "get properties of images", response = Properties.class)
	Properties retrievePropertiesOfImages();
}
