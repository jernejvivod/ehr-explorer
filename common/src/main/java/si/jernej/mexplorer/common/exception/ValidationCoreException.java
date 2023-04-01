package si.jernej.mexplorer.common.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ValidationCoreException extends RuntimeException
{
    public ValidationCoreException(String message)
    {
        super(message);
    }
}
