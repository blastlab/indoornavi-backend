package co.blastlab.serviceblbnavi.rest.facade;


import co.blastlab.serviceblbnavi.domain.Complex;
import co.blastlab.serviceblbnavi.rest.facade.ext.filter.TokenAuthorization;
import co.blastlab.serviceblbnavi.views.View;
import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/complex")
@Api("/complex")
@TokenAuthorization
public interface ComplexFacade {

    @POST
    @ApiOperation(value = "create complex", response = Complex.class)
    Complex create(@ApiParam(value = "complex", required = true) Complex complex);

    @PUT
    @ApiOperation(value = "delete complex by id", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    Complex update(@ApiParam(value = "complex", required = true) Complex complex);

    @DELETE
    @Path("/{id: \\d+}")
    @ApiOperation(value = "delete complex by id", response = Response.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    Response delete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    Complex find(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/building/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by building id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "building id empty or building doesn't exist")
    })
    Complex findByBuilding(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/floor/{id: \\d+}")
    @JsonView(View.ComplexInternal.class)
    @ApiOperation(value = "find complex by floor id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "floor id empty or floor doesn't exist")
    })
    Complex findByFloor(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/complete/{id: \\d+}")
    @JsonView(View.External.class)
    @ApiOperation(value = "find complete complex by id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "complex id empty or complex doesn't exist")
    })
    Complex findComplete(@ApiParam(value = "id", required = true) @PathParam("id") Long id);

    @GET
    @Path("/person/{id: \\d+}")
    @JsonView(View.PersonInternal.class)
    @ApiOperation(value = "find complexes by person id", response = Complex.class)
    @ApiResponses({
            @ApiResponse(code = 404, message = "person id empty, person or complex doesn't exist")
    })
    List<Complex> findByPerson(@ApiParam(value = "personId", required = true) @PathParam("id") Long personId);
}