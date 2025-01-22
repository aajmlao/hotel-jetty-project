package server.userPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.PrintWriter;

import static server.HelperClass.velocityHelperMethod;

public class HomePageServlet extends HttpServlet {
    /**
     * frontend doGet and user interact with this.
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String username = null;
        if (session == null) {
            response.sendRedirect("/login");
        } else {
            username = session.getAttribute("username").toString();
        }
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        VelocityContext context = new VelocityContext();
        VelocityEngine ve = (VelocityEngine) getServletContext().getAttribute("templateEngine");
        context.put("username", username);
        String filePath = "templates/HomePage.html";
        velocityHelperMethod(ve, out, context,filePath);
    }
}
