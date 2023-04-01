package si.jernej.mexplorer.common.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import si.jernej.mexplorer.common.exception.ValidationCoreException;

@Provider
public class ValidationCoreExceptionMapper implements ExceptionMapper<ValidationCoreException>
{
    @Override
    public Response toResponse(ValidationCoreException e)
    {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
    }
}
