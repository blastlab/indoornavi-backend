package co.blastlab.serviceblbnavi.dao.exception;

import javax.ejb.ApplicationException;

/**
 *
 * @author Grzegorz Konupek
 */
@ApplicationException(rollback = true)
public class PermissionException extends RuntimeException {

}
