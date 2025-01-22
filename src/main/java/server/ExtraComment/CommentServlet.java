package server.ExtraComment;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import server.hotelPackage.ServerSQLHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class CommentServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelId = request.getParameter("hotelId");
        String username = request.getParameter("username");
        String reviewId = request.getParameter("reviewId");
        serverSQLHandler.queryComment(username, reviewId, hotelId, out);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(currentDate.format(dateFormat), dateFormat);

        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse( currentTime.format(timeFormat), timeFormat);

        BufferedReader reader = request.getReader();
        String[] s = reader.readLine().split("\"");
        System.out.println(Arrays.toString(s));
        String comment = s[3];
        String reviewId = s[7];
        String hotelId = s[11];
        String username = s[15];
        serverSQLHandler.addComment(comment,reviewId,hotelId,username, Date.valueOf(date), Time.valueOf(time));
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
