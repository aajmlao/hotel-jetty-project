package objects;
/**
 * This is the Hotel object to store the hotel information
 * **/
public class Hotel {
    private String name;
    private String hotelId;
    private String lat;
    private String lng;
    private String address;
    private String city;
    private String state;

    public Hotel(String name, String id, String lat, String lng, String address, String city, String state) {
        this.name = name;
        this.hotelId = id;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.city = city;
        this.state = state;
    }

    /**
     * Get name
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * Get hotelId
     * @return
     */
    public String getHotelId() {
        return hotelId;
    }
    /**
     * Get latitude
     * @return
     */
    public String getLat() {
        return lat;
    }
    /**
     * Get longitude
     * @return
     */
    public String getLng() {
        return lng;
    }
    /**
     * Get address
     * @return
     */
    public String getAddress() {
        return address;
    }
    /**
     * Get city
     * @return
     */
    public String getCity() {
        return city;
    }
    /**
     * Get state
     * @return
     */
    public String getState() {
        return state;
    }
    /**
     * Override the toString
     * @return
     */
    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", hotelId='" + hotelId + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

