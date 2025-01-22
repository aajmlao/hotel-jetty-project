package server.userPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.hotelPackage.ServerSQLHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class ExpediaLinkServlet extends HttpServlet {
    ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * api doGet and write to api as json
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String userName = session.getAttribute("username").toString();
        PrintWriter out = response.getWriter();

        serverSQLHandler.queryExpediaHotel(userName, out);
    }

    /**
     * doPost use to add the link to the database
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String userName = session.getAttribute("username").toString();
        BufferedReader br = request.getReader();
        String[] s = br.readLine().split("\"");
        String link = s[3];
        String hotelId = s[7];
        serverSQLHandler.insertExpediaHotel(link, userName, hotelId);
    }

    /**
     * doDelete is used to send delete request to the database and delete from the database.
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        BufferedReader br = request.getReader();
        String[] s = br.readLine().split("\"");
        String username = s[3];
        String hotelId = s[7];
        serverSQLHandler.deleteExpediaHotel(username, hotelId);
    }
}
