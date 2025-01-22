package server;

public class PreparedStatements {
    /** Prepared Statements  */
    /** For creating the users table */
    public static final String CREATE_USER_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "userId INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL," +
                    "date DATE," +
                    "time TIME); ";
    /** Used to insert a new user into the database. */

    public static final String REGISTER_SQL =
            "INSERT INTO users (username, password, usersalt) " +
                    "VALUES (?, ?, ?)";
    /** Used to determine if a username already exists. */

    public static final String USER_SQL =
            "SELECT username FROM users WHERE username = ?";
    /** Used to retrieve the salt associated with a specific user. */

    public static final String SALT_SQL =
            "SELECT usersalt FROM users WHERE username = ?";
    /** Used to authenticate a user. */

    public static final String AUTH_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?";

    /** For creating the hotels table */
    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE IF NOT EXISTS hotels (" +
            "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "hotelId VARCHAR(32) NOT NULL UNIQUE," +
            "hotelName VARCHAR(255) NOT NULL UNIQUE," +
            "latitude VARCHAR(64) NOT NULL, " +
            "longitude VARCHAR(64) NOT NULL, " +
            "address VARCHAR(64) NOT NULL, " +
            "city VARCHAR(32) NOT NULL," +
            "state VARCHAR(32) NOT NULL);";

    /** For creating the reviews table */
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE IF NOT EXISTS reviews (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "reviewId VARCHAR(128) NOT NULL UNIQUE, " +
                    "rating INTEGER NOT NULL, " +
                    "title VARCHAR(64)," +
                    "text TEXT, " +
                    "userName VARCHAR(255) NOT NULL, " +
                    "date DATE NOT NULL, " +
                    "time TIME NOT NULL," +
                    "liked INTEGER NOT NULL);";

    /** Used to insert a new hotels into the database. */
    public static final String INSERT_HOTELS =
            "INSERT INTO hotels (" +
                    "hotelId, hotelName, latitude, longitude, address, city, state)" +
                    "VALUES (?,?,?,?,?,?,?);";

    /** Used to determine if a hotelId already exists. */
    public static final String HOTELS_SQL_BY_HOTELID =
            "SELECT * FROM hotels WHERE hotelId = ?";
    /**
     * Used to insert reviews to reviews table
     */
    public static final String INSERT_REVIEWS =
            "INSERT INTO reviews (hotelId, reviewId, rating, title, text, userName, date, time, liked)" +
                    "VALUES (?,?,?,?,?,?,?,?,?);";

    /**
     * query all reviews data by the review id
     */
    public static final String REVIEWS_SQL =
            "SELECT * FROM reviews WHERE reviewId = ?";
    /**
     * used to create like a hotel table.
     */
    public static final String CREATE_USERHOTEL_TABLE =
            "CREATE TABLE IF NOT EXISTS userHotel (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY," +
                    "hotelId VARCHAR(32) NOT NULL, " +
                    "username VARCHAR(255), " +
                    "FOREIGN KEY (username) REFERENCES users(username)" +
                    ");";
    /**
     * used to create an expedia table
     */
    public static final String CREATE_EXPEDIA_LINKS_TABLE =
            "CREATE TABLE IF NOT EXISTS expedia (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY," +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "username VARCHAR(255) NOT NULL," +
                    "link VARCHAR(255) NOT NULL," +
                    "FOREIGN KEY (username) REFERENCES users(username)" +
                    ");";

    public static final String CREATE_REVIEW_UNDER_REVIEW_TABLE =
            "CREATE TABLE IF NOT EXISTS comments(" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY," +
                    "hotelId VARCHAR(32) NOT NULL," +
                    "reviewId VARCHAR(128) NOT NULL," +
                    "commentId VARCHAR(128) NOT NULL UNIQUE," +
                    "username VARCHAR(255) NOT NULL," +
                    "text TEXT NOT NULL," +
                    "date DATE NOT NULL," +
                    "time TIME NOT NULL);";
}
