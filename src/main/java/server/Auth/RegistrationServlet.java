package server.Auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import objects.LoginTime;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import server.HelperClass;

import java.io.IOException;
import java.io.PrintWriter;

import static server.HelperClass.velocityHelperMethod;

public class RegistrationServlet extends HttpServlet {
    /**
     * RegistrationServlet doGet method
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false); // not create a session
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        if (session != null) {
            response.sendRedirect("/login");
        } else {
            VelocityContext context = new VelocityContext();
            String filePath = "templates/registrationTemplate.html";
            velocityHelperMethod(ve,out,context,filePath);
        }
    }

    /**
     * RegistrationServlet doPost method use registrationTemplate.html
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        String usernameParam = request.getParameter("username");
        usernameParam = StringEscapeUtils.escapeHtml4(usernameParam);
        System.out.println(usernameParam);
        String password = request.getParameter("password");
        password = StringEscapeUtils.escapeHtml4(password);
        System.out.println(password);

        DatabaseHandler databaseHandler = DatabaseHandler.getInstance();
        boolean isUserNameExist = databaseHandler.checkUsernameExist(usernameParam);

        VelocityContext context = new VelocityContext();
        if (isUserNameExist) {
            context.put("errorMessage", "Username is already exist.");
        } else {
            boolean isValidPassword = validPassword(password);
            if (isValidPassword) {
                databaseHandler.registerUser(usernameParam, password);
                HelperClass.createSession(request, usernameParam);
                LoginTime time = LoginTime.getInstance();
                time.setDateNow();
                time.setTimeNow();
                response.sendRedirect("/login?username="+usernameParam);
            } else {
                context.put("errorMessage", "Invalid Password. Fail to register the user: " + usernameParam);
            }
        }

        //if this fail will come here otherwise will redirect to /login
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        String filePath = "templates/registrationTemplate.html";
        velocityHelperMethod(ve,out, context, filePath);
    }

    /**
     * helper method
     * @param password
     * @return
     */
    private boolean validPassword(String password) {
        String[] specialChar = {"~","`","!","@","#","$","%","^","&","*","(",")","_","-","+","=","|",";","'","<",">","?","/",":","[","]","{","}"};
        if (password.length() >=5 && password.length() <= 20) {
            for (int i = 0; i < specialChar.length; i++) {
                if (password.contains(specialChar[i])){
                    return true;
                }
            }
            System.out.println("Password need to contain at least one special character");
        } else {
            System.out.println("password is invalid");
        }
        return false;
    }

}
