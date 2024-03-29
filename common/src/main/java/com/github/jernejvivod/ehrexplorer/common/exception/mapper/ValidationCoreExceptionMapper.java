package com.github.jernejvivod.ehrexplorer.common.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.github.jernejvivod.ehrexplorer.common.exception.ValidationCoreException;
import com.github.jernejvivod.ehrexplorer.common.exception.dto.ErrorDto;

@Provider
public class ValidationCoreExceptionMapper implements ExceptionMapper<ValidationCoreException>
{
    @Override
    public Response toResponse(ValidationCoreException e)
    {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage(e.getMessage());
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorDto)
                .build();
    }
}
