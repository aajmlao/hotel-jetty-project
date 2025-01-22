package jsonParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import server.Auth.DatabaseHandler;
import server.hotelPackage.ServerSQLHandler;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Parse a JSON to store at the Review object.
 * **/
public class ReviewParser implements Parser {

    /**
     * Parsing the Json data to a review Object
     * @param filename is a single json format file. and it is a path.
     * **/
    @Override
    public void parse(String filename) {
        ServerSQLHandler handlerHotels = ServerSQLHandler.getInstance();
        DatabaseHandler registerHandler = DatabaseHandler.getInstance();

        try (FileReader fr = new FileReader(filename)) {
            JsonParser parser = new JsonParser();
            JsonObject ob = (JsonObject) parser.parse(fr);
            JsonObject rewDetail = ob.getAsJsonObject("reviewDetails");
            JsonObject reviewCollectionOb = rewDetail.getAsJsonObject("reviewCollection");
            JsonArray reviewArr = reviewCollectionOb.getAsJsonArray("review");
            if (!reviewArr.isEmpty()) {
                for (int i = 0; i < reviewArr.size(); i++) {
                    JsonObject reviewOb = reviewArr.get(i).getAsJsonObject();
                    String hotelId = reviewOb.get("hotelId").getAsString();
                    String reviewId = reviewOb.get("reviewId").getAsString();
                    int overallRate = reviewOb.get("ratingOverall").getAsInt();
                    String title = reviewOb.get("title").getAsString();
                    String text = reviewOb.get("reviewText").getAsString();
                    String nickName = reviewOb.get("userNickname").getAsString();
                    String reviewSubmissionDat = reviewOb.get("reviewSubmissionDate").getAsString();
                    String fixedTime = "01:00:00";

                    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(reviewSubmissionDat, dateFormat);
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalTime time = LocalTime.parse(fixedTime, timeFormat);

                    if (nickName.isEmpty()) {
                        String hashed = DatabaseHandler.getHash("Anonymous", reviewId);
                        nickName = "Anonymous-" + hashed.substring(1,20).toLowerCase();
                    }
                    handlerHotels.insertReviewInit(reviewId, hotelId, overallRate, title, text, Date.valueOf(date), Time.valueOf(time), nickName);
                    registerHandler.registerUser(nickName, "@6878ok");
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException();
        }
    }
}
