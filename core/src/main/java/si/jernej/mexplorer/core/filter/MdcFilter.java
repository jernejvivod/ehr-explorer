package si.jernej.mexplorer.core.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.slf4j.MDC;

@Provider
public class MdcFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException
    {
        MDC.clear();
        MDC.put("requestId", UUID.randomUUID().toString());
        MDC.put("resourceClass", resourceInfo.getResourceClass().getSimpleName());
        MDC.put("resourceMethod", resourceInfo.getResourceMethod().getName());
        MDC.put("requestBody", readRequestBody(containerRequestContext));
    }

    protected String readRequestBody(ContainerRequestContext requestContext)
    {
        if (!"GET".equals(requestContext.getMethod()) && requestContext.hasEntity())
        {
            try
            {
                String requestString = entityStreamToString(requestContext.getEntityStream());
                requestContext.setEntityStream(new ByteArrayInputStream(requestString.getBytes()));
                return requestString;

            }
            catch (Exception e)
            {
                requestContext.setEntityStream(new ByteArrayInputStream(new byte[0]));
                return null;
            }
        }
        else
        {
            StringBuilder properties = new StringBuilder("Query parameters: {");

            BiConsumer<String, List<String>> paramAppender = (p, v) -> properties.append(String.format("%n\t%s:%s", p, v));

            requestContext.getUriInfo()
                    .getQueryParameters()
                    .forEach(paramAppender);

            properties.append("\n}\nPath parameters: {");

            requestContext.getUriInfo()
                    .getPathParameters()
                    .forEach(paramAppender);

            properties.append("\n}");
            return properties.toString();
        }
    }

    private static String entityStreamToString(InputStream inputStream)
    {
        return entityStreamToString(inputStream, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("SameParameterValue")
    private static String entityStreamToString(InputStream inputStream, Charset charset)
    {
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, charset)))
        {
            int c;
            while ((c = reader.read()) != -1)
            {
                sb.append((char) c);
            }
        }
        catch (IOException e)
        {
            throw new InternalServerErrorException("Error parsing request entity stream", e);
        }
        return sb.toString();
    }
}
