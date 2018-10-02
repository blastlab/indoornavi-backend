package co.blastlab.serviceblbnavi.ext.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PermissionException extends RuntimeException {

}
