package jsonParser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import objects.Hotel;
import server.hotelPackage.ServerSQLHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse the JSON to store in an object Hotel
 * **/
public class HotelParser implements Parser {

    /***
     * Parsing the Json data to a Hotel object
     * @param filename is a single json format file. and it is a path.
     * @return
     */
    @Override
    public void parse(String filename) {
        ServerSQLHandler handlerHotels = ServerSQLHandler.getInstance();
        try (FileReader fr = new FileReader(filename)) {
            JsonParser parser = new JsonParser();
            JsonObject ob = (JsonObject)parser.parse(fr);
            JsonArray sr = ob.getAsJsonArray("sr");
            for (int i = 0; i < sr.size(); i++) {
                JsonObject hotelOb = (JsonObject) sr.get(i);
                String name = hotelOb.get("f").getAsString();
                String id = hotelOb.get("id").getAsString();
                JsonObject ll = hotelOb.getAsJsonObject("ll");
                String lat = ll.get("lat").getAsString();
                String lng = ll.get("lng").getAsString();
                String ad = hotelOb.get("ad").getAsString();
                String ci = hotelOb.get("ci").getAsString();
                String pr = hotelOb.get("pr").getAsString();
                handlerHotels.insertHotel(id, name, lat, lng, ad, ci, pr);
            }
        }
        catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException();
        }
    }
}
