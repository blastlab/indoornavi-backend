package co.blastlab.serviceblbnavi.rest.facade.image;

import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.ext.filter.TokenAuthorization;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/images")
@Api("/images")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@TokenAuthorization
public interface ImageFacade {

	@POST
	@ApiOperation(value = "upload image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist")
	})
	Response uploadImage(@MultipartForm @Valid ImageUpload imageUpload//, @MultipartForm
	                    // @ApiParam  ( value = "attachmentparam", type=MediaType.APPLICATION_OCTET_STREAM) Attachment attachment
	) throws IOException;

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "download image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Floor with given id does not exist or it has no image")
	})
	Response downloadImage(@PathParam("id") Long floorId);
}
