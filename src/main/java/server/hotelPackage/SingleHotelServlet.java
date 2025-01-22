package server.hotelPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import objects.Hotel;
import objects.Review;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import static server.HelperClass.velocityHelperMethod;

public class SingleHotelServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();
    /**
     * SingleHotelServlet doGet Method to get the single page hotel
     * and velocity the SingleHotelTemplate.html
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
        context.put("loginUser", username);
        String hotelId = request.getParameter("hotelId");

        Hotel hotel = serverSQLHandler.getHotelByHotelId(hotelId);
        Map<String, Review> reviewMap = null;

        if (hotel == null) {
            response.sendRedirect("/login");
        } else {
            double sum = 0;
            if (reviewMap != null) {
                for (String reviewId : reviewMap.keySet()) {
                    sum += reviewMap.get(reviewId).getOverAllRating();
                }
                double avgRate = sum / reviewMap.size();
                context.put("avgRate", avgRate);
            } else {
                context.put("avgRate", "No rating");
            }

            String filePath = "templates/SingleHotelTemplate.html";
            String hotelName = hotel.getName();
            context.put("hotelId", hotelId);
            context.put("name", hotelName);
            StringBuilder address = new StringBuilder();
            address.append(hotel.getAddress()).append(System.lineSeparator())
                    .append(hotel.getCity()).append(hotel.getState());
            context.put("address", address.toString());
            String editedHotelName = hotelName.replace(",", "").replace("/", "")
                    .replace(" ", "-").replace(".","").replace("&", "");
            String expedia = "https://www.expedia.com/"+editedHotelName
                    +".h"+hotelId+".Hotel-Information";
            context.put("expedia", expedia);
            velocityHelperMethod(ve, out, context,filePath);
        }
    }
}
