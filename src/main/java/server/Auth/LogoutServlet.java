package server.Auth;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import objects.LoginTime;
import server.hotelPackage.ServerSQLHandler;

import java.io.IOException;
import java.sql.Date;
import java.sql.Time;

public class LogoutServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * LogoutServlet to log out user, which void the session
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String username = session.getAttribute("username").toString();
        if (session != null) {
            LoginTime time = LoginTime.getInstance();
            System.out.println(time.getDate());
            System.out.println(time.getTime());
            serverSQLHandler.updateLastLogin(Date.valueOf(time.getDate()), Time.valueOf(time.getTime()),username);
            session.removeAttribute("username");
            session.invalidate();
        }
        response.sendRedirect("/login");
    }
}
