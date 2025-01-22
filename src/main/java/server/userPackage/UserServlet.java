package server.userPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import server.hotelPackage.ServerSQLHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * backend doGet and send favor hotel data as json to used for frontend
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelId = request.getParameter("hotelId");
        String userName = request.getParameter("username");
        serverSQLHandler.queryHotelFromUserHotel(hotelId, userName, out);
    }

    /**
     * backend doPost when user like the hotel, then the add to the database for doGet to query
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
        String[] line = br.readLine().split("\"");
        String hotelId = line[3];
        serverSQLHandler.insertFavorHotel(hotelId, userName);
    }

    /**
     * backend doDelete when users unlike remove the data from database for doGet to query
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String userName = session.getAttribute("username").toString();
        BufferedReader br = request.getReader();
        String[] line = br.readLine().split("\"");
        String hotelId = line[3];
        serverSQLHandler.deleteFavorHotel(hotelId, userName);
    }
}
