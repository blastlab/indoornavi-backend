package co.blastlab.serviceblbnavi.dao.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PermissionException extends RuntimeException {

}
