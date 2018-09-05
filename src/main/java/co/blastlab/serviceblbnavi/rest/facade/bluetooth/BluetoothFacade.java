package co.blastlab.serviceblbnavi.rest.facade.bluetooth;

import co.blastlab.serviceblbnavi.dto.bluetooth.BluetoothDto;
import co.blastlab.serviceblbnavi.ext.filter.AuthorizedAccess;
import co.blastlab.serviceblbnavi.ext.filter.SetOperationId;
import io.swagger.annotations.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/bluetooths")
@Api("/bluetooths")
@SetOperationId
public interface BluetoothFacade {
	@POST
	@ApiOperation(value = "create bluetooth", response = BluetoothDto.class)
	@AuthorizedAccess("BLUETOOTH_CREATE")
	BluetoothDto create(@ApiParam(value = "bluetooth", required = true) @Valid BluetoothDto bluetooth);

	@PUT
	@Path("/{id: \\d+}")
	@ApiOperation(value = "update bluetooth by id", response = BluetoothDto.class)
	@AuthorizedAccess("BLUETOOTH_UPDATE")
	BluetoothDto update(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id,
	                 @ApiParam(value = "bluetooth", required = true) @Valid BluetoothDto bluetooth);

	@DELETE
	@Path("/{id: \\d+}")
	@ApiOperation(value = "delete bluetooth by id", response = Response.class)
	@ApiResponses({
		@ApiResponse(code = 404, message = "Bluetooth id empty or bluetooth does not exist"),
		@ApiResponse(code = 204, message = "Deleted successfully but there is no new information to return")
	})
	@AuthorizedAccess("BLUETOOTH_DELETE")
	Response delete(@ApiParam(value = "id", required = true) @PathParam("id") @Valid @NotNull Long id);

	@GET
	@ApiOperation(value = "find all bluetooths", response = BluetoothDto.class, responseContainer = "List")
	@AuthorizedAccess("BLUETOOTH_READ")
	List<BluetoothDto> findAll();
}
