package co.blastlab.serviceblbnavi.rest.facade;

import co.blastlab.serviceblbnavi.dto.floor.FloorDto;
import co.blastlab.serviceblbnavi.dto.floor.ImageUpload;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import com.wordnik.swagger.annotations.*;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/floor")
@Api("/floor")
@TokenAuthorization
public interface FloorFacade {

	@POST
	@ApiOperation(value = "create floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id empty or building doesn't exist")
	})
	FloorDto create(@ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@PUT
	@ApiOperation(value = "update floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
	})
	FloorDto update(@ApiParam(value = "floor", required = true) @Valid FloorDto floor);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id doesn't exist")
	})
	Response delete(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

	@GET
	@Path("/{id: \\d+}")
	@ApiOperation(value = "find floor", response = FloorDto.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id wasn't found")
	})
	FloorDto find(@PathParam("id") @ApiParam(value = "id", required = true) Long id);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update floors", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "building id or building empty or doesn't exist")
	})
	Response updateFloors(@PathParam("id") Long buildingId, @ApiParam(value = "floors", required = true) @Valid List<FloorDto> floors);

	@PUT
	@Path("/mToPix")
	@ApiOperation(value = "update mToPix", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id doesn't exist")
	})
	Response updatemToPix(@ApiParam(value = "floor", required = true) @Valid FloorDto.Extended floor);

	@POST
	@Path("/image")
	@ApiOperation(value = "upload image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id doesn't exist")
	})
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	Response uploadImage(@Valid @MultipartForm ImageUpload imageUpload) throws IOException;

	@GET
	@Path("/image/{id: \\d+}")
	@ApiOperation(value = "download image of the floor", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "floor with given id doesn't exist or it has no image")
	})
	Response downloadImage(@PathParam("id") Long floorId);
}
