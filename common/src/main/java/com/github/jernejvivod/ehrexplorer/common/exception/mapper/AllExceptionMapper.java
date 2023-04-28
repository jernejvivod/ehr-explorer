package com.github.jernejvivod.ehrexplorer.common.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.github.jernejvivod.ehrexplorer.common.exception.dto.ErrorDto;

@Provider
public class AllExceptionMapper implements ExceptionMapper<Throwable>
{
    @Override
    public Response toResponse(Throwable exception)
    {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setMessage("Internal server error");
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorDto)
                .build();
    }
}
