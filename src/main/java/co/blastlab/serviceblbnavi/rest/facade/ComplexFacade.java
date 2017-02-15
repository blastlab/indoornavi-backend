package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.dto.complex.ComplexDto;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/complex")
@Api("/complex")
public interface ComplexFacade {

    @POST
    @ApiOperation(value = "create complex", response = ComplexDto.class)
    ComplexDto create(@ApiParam(value = "complex", required = true) ComplexDto complex);

    @PUT
    @ApiOperation(value = "update complex by id", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    ComplexDto update(@ApiParam(value = "complex", required = true) ComplexDto complex);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete complex by id", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/{id: \\d+}")
    @ApiOperation(value = "find complex by id", response = ComplexDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    ComplexDto find(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/building/{id: \\d+}")
    @ApiOperation(value = "find complex by building id", response = ComplexDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    ComplexDto findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/floor/{id: \\d+}")
    @ApiOperation(value = "find complex by floor id", response = ComplexDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    ComplexDto findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/complete/{id: \\d+}")
    @ApiOperation(value = "find complete complex by id", response = ComplexDto.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    ComplexDto findComplete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/person/{id: \\d+}")
    @ApiOperation(value = "find complexes by person id", response = ComplexDto.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 404, message = "person id empty, person or complex doesn't exist")
    })
    List<ComplexDto> findByPerson(@ApiParam(value = "personId", required = true) @PathParam("id") Long personId);
}