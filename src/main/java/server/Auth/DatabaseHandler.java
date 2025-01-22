package server.Auth;

import server.HelperClass;
import server.PreparedStatements;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseHandler {
    private static final DatabaseHandler databaseHandler = new DatabaseHandler("database.properties");;
    private Properties config; // a "map" of properties
    private String uri = null; // uri to connect to mysql using jdbc
    private Random random = new Random(); // used in password  generation
    private final ReentrantReadWriteLock reviewReadWriteLock = new ReentrantReadWriteLock();

    private DatabaseHandler(String propFile) {
        this.config = HelperClass.loadConfigFile(propFile);
        this.uri = "jdbc:mysql://"+ config.getProperty("hostname") + "/" + config.getProperty("database");
    }

    /**
     * Returns the instance of the database handler.
     * @return instance of the database handler
     */
    public static DatabaseHandler getInstance() {
        return databaseHandler;
    }


    /**
     * createTable for database, but only create once
     */
    public void createTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("createTable dbConnection successful");
            statement = dbConnection.createStatement();
            statement.execute(PreparedStatements.CREATE_USER_TABLE);
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * get the hash
     * @param password
     * @param salt
     * @return
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return hashed;
    }

    /**
     * register a user to the database
     * @param newuser
     * @param newpass
     */
    public void registerUser(String newuser, String newpass) {
        // Generate salt
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); // salt
        String passhash = getHash(newpass, usersalt); // hashed password

        PreparedStatement checkedStatement;
        PreparedStatement statement;
        reviewReadWriteLock.writeLock().lock();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            System.out.println("registerUser dbConnection successful");
            try {
                checkedStatement = connection.prepareStatement(PreparedStatements.USER_SQL);
                checkedStatement.setString(1, newuser);
                ResultSet results = checkedStatement.executeQuery();
                boolean flag = results.next();
                if (flag) {
                    results.close();
                    return;
                }
                statement = connection.prepareStatement(PreparedStatements.REGISTER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
                System.out.println("registerUser successful");
            } catch (SQLException e) {
                System.out.println(e);
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        } finally {
            reviewReadWriteLock.writeLock().unlock();
        }
    }

    /**
     * check the user is valid
     * @param username
     * @param password
     * @return
     */
    public boolean authenticateUser(String username, String password) {
        reviewReadWriteLock.readLock().lock();
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTH_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
        return false;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }

    /**
     * check if the user exist in the database
     * @param username
     * @return
     */
    public boolean checkUsernameExist(String username) {
        PreparedStatement statements;
        reviewReadWriteLock.readLock().lock();
        try(Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statements = connection.prepareStatement(PreparedStatements.USER_SQL);

            statements.setString(1, username);
            ResultSet resultSet = statements.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            reviewReadWriteLock.readLock().unlock();
        }
        return false;
    }
}
