package server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import objects.Hotel;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * this is a helper class for re-usable code
 */
public class HelperClass {
    /**
     * use to create new session
     * @param request
     * @param userName
     */
    public static void createSession(HttpServletRequest request, String userName) {
        HttpSession session = request.getSession();
        session.setAttribute("username", userName);
        session.setMaxInactiveInterval(60*60); // one minute to test
    }

    /**
     * uses to out put velocity.
     * @param ve
     * @param out
     * @param context
     * @param htmlFilePath
     */
    public static void velocityHelperMethod(VelocityEngine ve, PrintWriter out, VelocityContext context, String htmlFilePath) {
        Template template = ve.getTemplate(htmlFilePath);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    /**
     * Load info from config file database.properties
     * @param propertyFile
     * @return
     */
    public static Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)){
            config.load(fr);
        } catch (IOException e) {
            System.out.println(e);
        }
        return config;
    }

    /**
     * help method that is used to multiple classes.
     * @param resultSet
     * @return
     */
    public static Hotel buildHotel(ResultSet resultSet) {
        Hotel hotel = null;
        try {
            String hotelId = resultSet.getString("hotelId");
            String hotelName = resultSet.getString("hotelName");
            String hotelAddress = resultSet.getString("address");
            String hotelCity = resultSet.getString("city");
            String hotelState = resultSet.getString("state");
            String latitude = resultSet.getString("latitude");
            String longitude = resultSet.getString("longitude");
            hotel = new Hotel(hotelName, hotelId, latitude, longitude, hotelAddress, hotelCity, hotelState);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return hotel;
    }
}
