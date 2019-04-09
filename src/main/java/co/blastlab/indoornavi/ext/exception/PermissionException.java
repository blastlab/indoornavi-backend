package co.blastlab.indoornavi.ext.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PermissionException extends RuntimeException {

}
