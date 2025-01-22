package server.hotelPackage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletResponse;
import objects.Hotel;
import server.Auth.DatabaseHandler;
import server.HelperClass;
import server.PreparedStatements;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static server.HelperClass.loadConfigFile;

/**
 * This is a singleton class
 */
public class ServerSQLHandler {
    private static final ServerSQLHandler serverSQLHandler = new ServerSQLHandler("database.properties");
    private Properties config;
    private String url;
    private final ReentrantReadWriteLock reviewReadWriteLock = new ReentrantReadWriteLock();


    private ServerSQLHandler(String propFile) {
        this.config = loadConfigFile(propFile);
        this.url = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database");
    }

    public static ServerSQLHandler getInstance() {
        return serverSQLHandler;
    }

    /**
     * create table only once.
     */
    public void createHotelTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_HOTELS_TABLE);
            System.out.println("create hotels table");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * CreateReviewTable to review data like hotelId, reviewId, rating
     * title, text, username, date, time, liked
     */
    public void createReviewTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_REVIEWS_TABLE);
            System.out.println("create reviews table");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * CreateUser Table hotelId, username
     */
    public void createUserHotelTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_USERHOTEL_TABLE);
            System.out.println("create usersHotel table");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * CreateExpedia table store data
     * like hotelId, username, link
     */
    public void createExpediaTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_EXPEDIA_LINKS_TABLE);
            System.out.println("create expedia table");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Create comment table to store data
     * like hotelId, reviewId, commentId, username, text, date, time
     */
    public void createCommentTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_REVIEW_UNDER_REVIEW_TABLE);
        }catch (SQLException e) {
            System.out.println(e);
        }
    }
    /**
     * insertHotel for Json data from the database file to hotel Table
     * @param hotelId
     * @param hotelName
     * @param lat
     * @param lng
     * @param address
     * @param city
     * @param state
     */
    public void insertHotel(String hotelId, String hotelName, String lat, String lng, String address, String city, String state) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.config.getProperty("username"), this.config.getProperty("password"));
            try {
                PreparedStatement checkStatement = connection.prepareStatement(PreparedStatements.HOTELS_SQL_BY_HOTELID);
                checkStatement.setString(1, hotelId);
                ResultSet resultSet = checkStatement.executeQuery();
                boolean flag = resultSet.next();
                if (!flag) {
                    System.out.println("Inserting hotel");
                    PreparedStatement statement = connection.prepareStatement(PreparedStatements.INSERT_HOTELS);
                    statement.setString(1, hotelId);
                    statement.setString(2, hotelName);
                    statement.setString(3, lat);
                    statement.setString(4, lng);
                    statement.setString(5, address);
                    statement.setString(6, city);
                    statement.setString(7, state);
                    statement.executeUpdate();
                    statement.close();
                }
            }catch (SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * insertReview from Json data from the database file to review table
     * @param reviewId
     * @param hotelId
     * @param rating
     * @param title
     * @param text
     * @param date
     * @param time
     * @param userName
     */
    public void insertReviewInit(String reviewId, String hotelId, int rating, String title, String text, Date date, Time time, String userName) {
        try {
            reviewReadWriteLock.writeLock().lock();
            Connection connection = DriverManager.getConnection(this.url, this.config.getProperty("username"), this.config.getProperty("password"));
            try {
                PreparedStatement checkStatement = connection.prepareStatement(PreparedStatements.REVIEWS_SQL);
                checkStatement.setString(1, reviewId);
                ResultSet resultSet = checkStatement.executeQuery();
                boolean flag = resultSet.next();

                if (flag) { //true is exist
                    System.out.println("exist review");
                    return;
                }
                if (userName.isEmpty()) {
                    String hashed = DatabaseHandler.getHash("Anonymous", reviewId);
                    userName = "Anonymous-" + hashed.substring(1, 20).toLowerCase();
                }
                PreparedStatement statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEWS);
                statement.setString(1, hotelId);
                statement.setString(2, reviewId);
                statement.setInt(3, rating);
                statement.setString(4, title);
                statement.setString(5, text);
                statement.setString(6, userName);
                statement.setDate(7, (java.sql.Date) date);
                statement.setTime(8, (java.sql.Time) time);
                statement.setInt(9, 0);
                statement.executeUpdate();
                statement.close();
                System.out.println("inserted A Review");
            }catch (SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * Get data from hotel table by using a hotelId and return Hotel object
     * @param hotelId
     * @return
     */
    public Hotel getHotelByHotelId(String hotelId) {
        reviewReadWriteLock.readLock().lock();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("Connected to the database hotels table");
            statement = connection.prepareStatement(PreparedStatements.HOTELS_SQL_BY_HOTELID);
            statement.setString(1, hotelId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                return HelperClass.buildHotel(resultSet);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
        return null;
    }

    /**
     * query hotels from at hotels table and set to some limit.
     * @param hotelQuery
     * @param pageSize
     * @param offset
     * @param response
     * @throws IOException
     */
    public void queryHotels(String hotelQuery, int pageSize, int offset, HttpServletResponse response) throws IOException {
        reviewReadWriteLock.readLock().lock();
        PrintWriter out = response.getWriter();
        String SQL_HOTELS_QUERY;
        String SQL_HOTEL_TOTEL_QUERY;
        boolean isHotelId = false;
        boolean isHotelQueryEmpty = false;
        if (hotelQuery.isEmpty()) {
            isHotelQueryEmpty = true;
            SQL_HOTELS_QUERY = "SELECT hotelName, hotelId FROM hotels ORDER BY hotelName LIMIT ? OFFSET ?;";
            SQL_HOTEL_TOTEL_QUERY = "SELECT COUNT(*) as Total FROM hotels";
        } else {
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(hotelQuery);
            if (m.find()) {
                isHotelId = true;
                SQL_HOTELS_QUERY = "SELECT hotelName, hotelId FROM hotels WHERE hotelId = ? ORDER BY hotelName;";
                SQL_HOTEL_TOTEL_QUERY = "SELECT COUNT(*) as Total FROM hotels WHERE hotelId = ?;";
            } else {
                SQL_HOTELS_QUERY = "SELECT hotelName, hotelId FROM hotels WHERE hotelName LIKE ? ORDER BY hotelName LIMIT ? OFFSET ?;";
                SQL_HOTEL_TOTEL_QUERY = "SELECT COUNT(*) as Total FROM hotels WHERE hotelName LIKE ?;";
            }
        }

        PreparedStatement statement;
        PreparedStatement statementCount;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("Connected to the database hotels table to query hotels");
            int totalCount = 0;
            statement = connection.prepareStatement(SQL_HOTELS_QUERY);
            statementCount = connection.prepareStatement(SQL_HOTEL_TOTEL_QUERY);
            if (!isHotelQueryEmpty) {
                if (!isHotelId) {
                    statementCount.setString(1, "%" + hotelQuery + "%");
                    statement.setString(1, "%" + hotelQuery + "%");
                    statement.setInt(2, pageSize);
                    statement.setInt(3, offset);
                } else {
                    statement.setString(1, hotelQuery);
                    statementCount.setString(1, hotelQuery);
                }
            } else {
                statement.setInt(1, pageSize);
                statement.setInt(2, offset);
            }
            ResultSet resultSet = statement.executeQuery();
            ResultSet resultSetCount = statementCount.executeQuery();
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject hotelJson = new JsonObject();
                hotelJson.addProperty("hotelId", resultSet.getString("hotelId"));
                hotelJson.addProperty("hotelName", resultSet.getString("hotelName"));
                jsonArray.add(hotelJson);
            }
            jsonObject.add("hotels", jsonArray);
            while (resultSetCount.next()) {
                totalCount = resultSetCount.getInt("Total");
            }
            jsonObject.addProperty("totalCount", totalCount);
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

    /**
     * query reviews from review table with set limit.
     * @param hotelQuery
     * @param pageSize
     * @param offset
     * @param out
     */
    public void queryReviews(String hotelQuery, int pageSize, int offset, PrintWriter out) {
        reviewReadWriteLock.readLock().lock();
        String SQL_REVIEWS_QUERY = "SELECT * FROM reviews WHERE hotelId = ? ORDER BY date DESC, time DESC LIMIT ? OFFSET ?;";
        String SQL_REVIEWS_COUNT = "SELECT COUNT(*) as Total FROM reviews WHERE hotelId = ?;";
        String SQL_TOTAL_RATING = "SELECT SUM(rating) as TotalRate FROM reviews WHERE hotelId = ?;";
        PreparedStatement statement;
        PreparedStatement statementCount;
        PreparedStatement rateStatements;
        int totalRating = 0;
        int totalCount = 0;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("Connected to the database reviews table to query reviews");
            statement = connection.prepareStatement(SQL_REVIEWS_QUERY);
            statement.setString(1, hotelQuery);
            statement.setInt(2, pageSize);
            statement.setInt(3, offset);
            ResultSet resultSet = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            boolean hasReview = resultSet.next();
            if (!hasReview) {
                JsonObject nullObject = new JsonObject();
                nullObject.addProperty("hasReview", false);
                jsonArray.add(nullObject);
            } else {
                do {
                    JsonObject reviewJson = new JsonObject();
                    reviewJson.addProperty("hasReview", true);
                    reviewJson.addProperty("hotelId", resultSet.getString("hotelId"));
                    reviewJson.addProperty("reviewId", resultSet.getString("reviewId"));
                    int rating = resultSet.getInt("rating");
                    reviewJson.addProperty("rating", rating);
                    String title = resultSet.getString("title").isEmpty() ? "No title" : resultSet.getString("title");
                    reviewJson.addProperty("title", title);
                    String text = resultSet.getString("text").isEmpty() ? "No text" : resultSet.getString("text");
                    reviewJson.addProperty("text", text);
                    reviewJson.addProperty("userName", resultSet.getString("userName"));
                    reviewJson.addProperty("date", String.valueOf(resultSet.getDate("date")));
                    reviewJson.addProperty("time", String.valueOf(resultSet.getTime("time")));
                    reviewJson.addProperty("likes", resultSet.getInt("liked"));
                    jsonArray.add(reviewJson);
                } while (resultSet.next());
            }
            jsonObject.add("reviews", jsonArray);

            statementCount = connection.prepareStatement(SQL_REVIEWS_COUNT);
            statementCount.setString(1, hotelQuery);
            ResultSet resultSetCount = statementCount.executeQuery();
            while (resultSetCount.next()) {
                totalCount = resultSetCount.getInt("Total");
            }
            rateStatements = connection.prepareStatement(SQL_TOTAL_RATING);
            rateStatements.setString(1, hotelQuery);
            ResultSet resultSetRate = rateStatements.executeQuery();
            while (resultSetRate.next()) {
                totalRating = resultSetRate.getInt("TotalRate");
            }
            jsonObject.addProperty("totalCount", totalCount);
            jsonObject.addProperty("totalRating", totalRating);
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

    /**
     * add review in to the review database, this is different than insert review because data is from the website.
     * @param userName
     * @param hotelId
     * @param rating
     * @param title
     * @param text
     * @param date
     * @param time
     */
    public void addReview(String userName, String hotelId, int rating, String title, String text, Date date, Time time) {
        reviewReadWriteLock.writeLock().lock();
        int count = 0;
        String SQL_GET_COUNT_USERNAME = "SELECT COUNT(*) as CountName FROM reviews WHERE userName = ?;";
        PreparedStatement statementCount;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statementCount = connection.prepareStatement(SQL_GET_COUNT_USERNAME);
            statementCount.setString(1, userName);
            ResultSet resultSetCount = statementCount.executeQuery();
            while (resultSetCount.next()) {
                count = resultSetCount.getInt("CountName");
            }
            count++;
            String hashedReviewID = DatabaseHandler.getHash(userName, hotelId + count);
            PreparedStatement statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEWS);
            statement.setString(1, hotelId);
            statement.setString(2, hashedReviewID);
            statement.setInt(3, rating);
            statement.setString(4, title);
            statement.setString(5, text);
            statement.setString(6, userName);
            statement.setDate(7, (java.sql.Date) date);
            statement.setTime(8, (java.sql.Time) time);
            statement.setInt(9, 0);
            statement.executeUpdate();
            statement.close();
            System.out.println("Add Review to reviews table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * update the existing review from database from the website
     * @param reviewId
     * @param updateTitle
     * @param updateText
     * @param updateDate
     * @param updateTime
     * @param updateRate
     */
    public void updateReview(String reviewId, String updateTitle, String updateText, Date updateDate, Time updateTime, int updateRate) {
        reviewReadWriteLock.writeLock().lock();
        String UPDATE_REVIEW = "UPDATE reviews SET title = ?, text = ?, date = ?, time = ?, rating = ? WHERE reviewId = ?;";
        PreparedStatement updateStatement;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            updateStatement = connection.prepareStatement(UPDATE_REVIEW);
            updateStatement.setString(1, updateTitle);
            updateStatement.setString(2, updateText);
            updateStatement.setDate(3, updateDate);
            updateStatement.setTime(4, updateTime);
            updateStatement.setInt(5, updateRate);
            updateStatement.setString(6, reviewId);
            updateStatement.executeUpdate();
            updateStatement.close();
            System.out.println("Update Review table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * delete review by reviewId from the website
     * @param reviewId
     */
    public void deleteReview(String reviewId) {
        reviewReadWriteLock.writeLock().lock();
        String DELETE_REVIEW = "DELETE FROM reviews WHERE reviewId = ?;";
        PreparedStatement deleteStatement;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            deleteStatement = connection.prepareStatement(DELETE_REVIEW);
            deleteStatement.setString(1, reviewId);
            deleteStatement.executeUpdate();
            deleteStatement.close();
            System.out.println("Delete Review table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * This method is to operate favorite hotel list on the favorite hotels
     * @param hotelId
     * @param out
     */
    public void queryHotelFromUserHotel(String hotelId, String username, PrintWriter out) {
        reviewReadWriteLock.readLock().lock();
        String QUERY = "SELECT * FROM userHotel WHERE hotelId = ? AND username = ?;";
        PreparedStatement statement;
        boolean isFavor = false;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))){
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, hotelId);
            statement.setString(2, username);
            JsonObject jsonObject = new JsonObject();
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                isFavor = true;
                String name = resultSet.getString("username");
                jsonObject.addProperty("username", name);
            } else {
                jsonObject.addProperty("username", "Null");
            }
            jsonObject.addProperty("isFavor", isFavor);
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

    /**
     * add favor to the hotel and add the table in database
     * @param hotelId
     * @param username
     */
    public void insertFavorHotel(String hotelId, String username) {
        reviewReadWriteLock.writeLock().lock();
        String QUERY = "SELECT * FROM userHotel WHERE hotelId = ? AND username = ?;";
        String INSERT_QUERY = "INSERT INTO userHotel (hotelId, username) VALUES (?, ?);";
        PreparedStatement checkStatement;
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))){
            checkStatement = connection.prepareStatement(QUERY);
            checkStatement.setString(1, hotelId);
            checkStatement.setString(2, username);
            ResultSet resultSet = checkStatement.executeQuery();
            if (resultSet.next()) {
                return;
            }
            statement = connection.prepareStatement(INSERT_QUERY);
            statement.setString(1, hotelId);
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
            System.out.println("Insert Hotel table");
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * delete the favor when click again.
     * @param hotelId
     * @param username
     */
    public void deleteFavorHotel(String hotelId, String username) {
        reviewReadWriteLock.writeLock().lock();
        String DELETE_QUERY = "DELETE FROM userHotel WHERE username = ? AND hotelId = ?;";
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(DELETE_QUERY);
            statement.setString(1, username);
            statement.setString(2, hotelId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Delete Hotel table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * update the last login and store in database
     * @param lastLogin
     * @param lastLoginTime
     * @param username
     */
    public void updateLastLogin(Date lastLogin, Time lastLoginTime, String username) {
        reviewReadWriteLock.writeLock().lock();
        String UPDATE_QUERY = "UPDATE users SET date = ?, time = ? WHERE username = ?";
        PreparedStatement statement;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(UPDATE_QUERY);
            statement.setDate(1, lastLogin);
            statement.setTime(2, lastLoginTime);
            statement.setString(3, username);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * query the last login time
     * @param username
     * @param out
     */
    public void queryLoginTime(String username, PrintWriter out) {
        reviewReadWriteLock.readLock().lock();
        String QUERY = "SELECT date, time FROM users WHERE username = ?";
        PreparedStatement statement;
        Date date = null;
        Time time = null;
        try(Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                date = resultSet.getDate("date");
                time = resultSet.getTime("time");
            }
            JsonObject jsonObject = new JsonObject();
            if (date != null && time != null) {
                jsonObject.addProperty("username", username);
                jsonObject.addProperty("date", String.valueOf(date));
                jsonObject.addProperty("time", String.valueOf(time));
            } else {
                jsonObject.addProperty("username", username);
                jsonObject.addProperty("date", "Null");
                jsonObject.addProperty("time", "Null");
            }
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

    /**
     * insert expedia link to the database
     * @param link
     * @param username
     * @param hotelId
     */
    public void insertExpediaHotel(String link, String username, String hotelId) {
        reviewReadWriteLock.writeLock().lock();
        String CHECK_EXIST = "SELECT * FROM expedia WHERE link = ? AND username = ?";
        String INSERT_QUERY = "INSERT INTO expedia (link, username, hotelId) VALUES (?, ?, ?);";
        PreparedStatement statement;
        PreparedStatement checkExistStatement;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            checkExistStatement = connection.prepareStatement(CHECK_EXIST);
            checkExistStatement.setString(1, link);
            checkExistStatement.setString(2, username);
            ResultSet resultSet = checkExistStatement.executeQuery();
            if (resultSet.next()) {
                return;
            }
            statement = connection.prepareStatement(INSERT_QUERY);
            statement.setString(1, link);
            statement.setString(2, username);
            statement.setString(3, hotelId);
            statement.executeUpdate();
            statement.close();
            System.out.println("Insert Expedia Hotel table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * query from the database
     * @param username
     * @param out
     */
    public void queryExpediaHotel(String username, PrintWriter out) {
        reviewReadWriteLock.readLock().lock();
        String QUERY = "SELECT link, hotelId FROM expedia WHERE username = ?";
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(QUERY);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            boolean hasLink = resultSet.next();
            if (!hasLink) {
                jsonObject.addProperty("numLink", "Null");
            } else {
                do {
                    JsonObject json = new JsonObject();
                    json.addProperty("hotelId", resultSet.getString("hotelId"));
                    json.addProperty("link", resultSet.getString("link"));
                    jsonArray.add(json);
                } while (resultSet.next());
                jsonObject.add("expedia", jsonArray);
                jsonObject.addProperty("numLink", "NotNull");
            }
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

    /**
     * delete from the database
     * @param username
     * @param hotelId
     */
    public void deleteExpediaHotel(String username, String hotelId) {
        reviewReadWriteLock.writeLock().lock();
        String CHECK_EXIST = "SELECT * FROM expedia WHERE hotelId = ? AND username = ?";
        String DELETE_QUERY = "DELETE FROM expedia WHERE hotelId = ? AND username = ?";
        PreparedStatement statement;
        PreparedStatement checkExistStatement;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            checkExistStatement = connection.prepareStatement(CHECK_EXIST);
            checkExistStatement.setString(1, hotelId);
            checkExistStatement.setString(2, username);
            ResultSet resultSet = checkExistStatement.executeQuery();
            if (!resultSet.next()) {
                return;
            }
            statement = connection.prepareStatement(DELETE_QUERY);
            statement.setString(1, hotelId);
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
            System.out.println("Delete Expedia Hotel table");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }

    }

    /**
     * add comments to the review
     * @param comment
     * @param reviewId
     * @param hotelId
     * @param username
     * @param date
     * @param time
     */
    public void addComment(String comment, String reviewId, String hotelId, String username, Date date, Time time) {
        reviewReadWriteLock.writeLock().lock();
        int count = 0;
        String SQL_GET_COUNT_USERNAME = "SELECT COUNT(*) as CountName FROM comments WHERE username = ?;";
        String INSERT = "INSERT INTO comments (hotelId, reviewId, commentId, text, username, date, time) VALUES (?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement statement;
        PreparedStatement statementCount;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statementCount = connection.prepareStatement(SQL_GET_COUNT_USERNAME);
            statementCount.setString(1, username);
            ResultSet resultSetCount = statementCount.executeQuery();
            while (resultSetCount.next()) {
                count = resultSetCount.getInt("CountName");
            }
            count++;
            String hashedReviewID = DatabaseHandler.getHash(username, hotelId + count + reviewId);
            statement = connection.prepareStatement(INSERT);
            statement.setString(1, hotelId);
            statement.setString(2, reviewId);
            statement.setString(3, hashedReviewID);
            statement.setString(4, comment);
            statement.setString(5, username);
            statement.setDate(6, date);
            statement.setTime(7, time);
            statement.executeUpdate();
            statementCount.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }


    }

    public void queryComment(String username, String reviewId, String hotelId, PrintWriter out) {
        reviewReadWriteLock.readLock().lock();
        String SQL = "SELECT * FROM comments WHERE reviewId = ? AND username = ? AND hotelId = ?;";
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(url, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, reviewId);
            statement.setString(2, username);
            statement.setString(3, hotelId);
            ResultSet resultSet = statement.executeQuery();
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            while (resultSet.next()) {
                JsonObject json = new JsonObject();
                json.addProperty("hotelId", resultSet.getString("hotelId"));
                json.addProperty("username", resultSet.getString("username"));
                json.addProperty("reviewId", resultSet.getString("reviewId"));
                json.addProperty("text", resultSet.getString("text"));
                json.addProperty("date", String.valueOf(resultSet.getDate("date")));
                json.addProperty("time", String.valueOf(resultSet.getTime("time")));
                jsonArray.add(json);
            }
            jsonObject.add("comments", jsonArray);
            out.println(jsonObject);
            out.flush();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
    }

}
