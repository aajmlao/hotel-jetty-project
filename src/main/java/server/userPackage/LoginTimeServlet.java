package server.userPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.hotelPackage.ServerSQLHandler;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginTimeServlet extends HttpServlet {
    ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * the backend doGet send data as json use to frontend to get the data from api
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.sendRedirect("/login");
            return;
        }
        String userName = session.getAttribute("username").toString();
        serverSQLHandler.queryLoginTime(userName, out);
    }

}
