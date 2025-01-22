package server.Auth;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import objects.LoginTime;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.HelperClass;
import server.hotelPackage.ServerSQLHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static server.HelperClass.velocityHelperMethod;

public class LoginServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * loginServlet doGet uses LandingPage.html and LoginTemplate.html
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false); // not create a session
        VelocityContext context = new VelocityContext();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        if (session != null) { // not null
            context.put("username", session.getAttribute("username").toString());
            String filePath = "templates/LandingPage.html";
            velocityHelperMethod(ve, out, context, filePath);
        } else { //null
            String filePath = "templates/LoginTemplate.html";
            velocityHelperMethod(ve, out, context, filePath);
        }
    }

    /**
     * doPost LoginServlet uses LandingPage.html and LoginTemplate.html
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException{
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false); // not create a session
        VelocityContext context = new VelocityContext();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        if (session != null) {
            context.put("username", session.getAttribute("username").toString());
            String filePath = "templates/LandingPage.html";
            velocityHelperMethod(ve, out, context, filePath);
        } else {
            boolean isValidAuth = verifyAuth(request);
            if (isValidAuth) {
                HttpSession newSession = request.getSession(false);
                context.put("username", newSession.getAttribute("username").toString());
                LoginTime loginTime = LoginTime.getInstance();
                loginTime.setDateNow();
                loginTime.setTimeNow();
                System.out.println(loginTime.getDate());
                System.out.println(loginTime.getTime());
                String filePath = "templates/LandingPage.html";
                velocityHelperMethod(ve, out, context, filePath);
            } else {
                context.put("errorMessage", "username/password is incorrect.");
                String filePath = "templates/LoginTemplate.html";
                velocityHelperMethod(ve, out, context, filePath);
            }
        }
    }

    /**
     * verify the auth in the database
     * @param request
     * @return
     */
    private boolean verifyAuth(HttpServletRequest request) {
        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();

        String usernameParam = request.getParameter("username");
        usernameParam = StringEscapeUtils.escapeHtml4(usernameParam);
        System.out.println(usernameParam);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml4(password);
        System.out.println(password);
        boolean isValid = databaseHandler.authenticateUser(usernameParam, password);
        // create session if valid
        if (isValid) {
            HelperClass.createSession(request, usernameParam);
        }
        return isValid;
    }
}
