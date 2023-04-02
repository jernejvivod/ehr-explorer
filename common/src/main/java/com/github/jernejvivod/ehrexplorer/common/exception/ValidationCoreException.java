package com.github.jernejvivod.ehrexplorer.common.exception;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ValidationCoreException extends RuntimeException
{
    public ValidationCoreException(String message)
    {
        super(message);
    }
}
