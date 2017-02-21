package co.blastlab.serviceblbnavi.rest.facade.ext.mapper;

import javax.ejb.EJBTransactionRolledbackException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class ConstraintViolationMapper implements ExceptionMapper<EJBTransactionRolledbackException> {

    @Override
    public Response toResponse(EJBTransactionRolledbackException exception) {

        /*String constraintName = null;
        Response.Status status;

        //exception.getCausedByException().getCause().getCause().

        switch (constraintName){
            case "unique_minor_major":
                status = Response.Status.BAD_REQUEST;
                break;
            default:
                //...
        }
*/
       //return Response.status(status).entity("").build();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("").build();
    }
}