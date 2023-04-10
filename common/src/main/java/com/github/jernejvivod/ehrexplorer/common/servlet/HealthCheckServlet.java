package com.github.jernejvivod.ehrexplorer.common.servlet;

import java.io.IOException;
import java.io.Serial;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.InternalServerErrorException;

@WebServlet(name = "HealthCheckServlet", urlPatterns = "/ping")
public class HealthCheckServlet extends HttpServlet
{
    @Serial
    private static final long serialVersionUID = -1930900280311695407L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    {
        try
        {
            resp.setContentType("text/plain");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("pong");
        }
        catch (IOException e)
        {
            throw new InternalServerErrorException("Error writing ping response: " + e.getMessage(), e);
        }
    }
}
