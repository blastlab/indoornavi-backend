package co.blastlab.serviceblbnavi.rest.facade.ext.mapper;

import co.blastlab.serviceblbnavi.rest.facade.ext.mapper.accessory.ConstraintSearcher;
import co.blastlab.serviceblbnavi.rest.facade.ext.mapper.content.DbConstraintViolationContent;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ConstraintViolationMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

	@Override
	public Response toResponse(EJBTransactionRolledbackException exception) {

		String constraintName = ConstraintSearcher.retrieveConstraintName(exception);
		String message;

        switch (constraintName){
            case "unique_minor_major":
                message = "Minor and major must be unique";
                break;
            case "unique_level_building_id":
                message = "You can't have more than one floor with the same level";
                break;default:
                message = "Unknown constraint violation exception";
        }

		return Response.status(Response.Status.BAD_REQUEST)
			.entity(new DbConstraintViolationContent(message)).build();
	}
}