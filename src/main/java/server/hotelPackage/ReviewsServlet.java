package server.hotelPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import objects.Hotel;
import org.apache.commons.text.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * ReviewsServlet uses to operate JSON.
 */
public class ReviewsServlet extends HttpServlet {
    private static final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();

    /**
     * ReviewsServlet doGet method get review in reviewCollection
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        String hotelQuery = request.getParameter("hotelId");
        hotelQuery = StringEscapeUtils.escapeHtml4(hotelQuery);

        String page = request.getParameter("page");
        page = StringEscapeUtils.escapeHtml4(page);
        String listSize = request.getParameter("listSize");
        listSize = StringEscapeUtils.escapeHtml4(listSize);

        int numPage = page.isEmpty() ? 1 : Integer.parseInt(page);
        int numSize = listSize.isEmpty() ? 10 : Integer.parseInt(listSize);

        int offset = (numPage - 1) * numSize;

        Hotel hotel = serverSQLHandler.getHotelByHotelId(hotelQuery);
        if (hotel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("Hotel Not Found");
            return;
        }

        serverSQLHandler.queryReviews(hotelQuery, numSize, offset, out);
    }

    /**
     * ReviewsServlet doPost method add the review in reviewCollection
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("POST Review");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        HttpSession session = request.getSession(false);
        String userName = session.getAttribute("username").toString();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter  dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(currentDate.format(dateFormat), dateFormat);

        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse( currentTime.format(timeFormat), timeFormat);

        BufferedReader bufferedReader = request.getReader();
        String[] br = bufferedReader.readLine().split("\"title\":\"")[1].split("\",\"text\":\"");
        String title = br[0];
        String[] br1 = br[1].split("\",\"rating\":");
        String text = br1[0];
        String[] br2 = br1[1].split(",\"hotelId\":\"");
        int rating = Integer.parseInt(br2[0]);
        String[] br3 = br2[1].split("\"}");
        String hotelId = br3[0];
        serverSQLHandler.addReview(userName, hotelId, rating, title, text, Date.valueOf(date), Time.valueOf(time));
    }

    /**
     * ReviewsServlet doPut method update review in reviewCollection
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("PUT Review");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter  dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(currentDate.format(dateFormat), dateFormat);

        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime time = LocalTime.parse( currentTime.format(timeFormat), timeFormat);

        BufferedReader bufferedReader = request.getReader();
        String[] br = bufferedReader.readLine().split("\"updateTitle\":\"")[1].split("\",\"updateText\":\"");
        String updateTitle = br[0];
        String[] br1 = br[1].split("\",\"updateRate\":");
        String updateText = br1[0];
        String[] br2 = br1[1].split(",\"reviewId\":\"");
        int updateRate = Integer.parseInt(br2[0]);
        String[] br3 = br2[1].split("\"}");
        String reviewId = br3[0];
        serverSQLHandler.updateReview(reviewId, updateTitle, updateText, Date.valueOf(date),Time.valueOf(time), updateRate);
    }

    /**
     * ReviewServlet doDelete method remove review by reviewId in reviewCollection
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("DELETE Review");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        BufferedReader bufferedReader = request.getReader();
        String[] br = bufferedReader.readLine().split("\"");
        String reviewId = br[3];
        serverSQLHandler.deleteReview(reviewId);
    }


}
