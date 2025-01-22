package jsonParser;

import queryProcessor.DirectoryTraversal;
import server.Auth.DatabaseHandler;
import server.hotelPackage.ServerSQLHandler;

public class DataLoader {
    private int THREAD = 3;
    private final ServerSQLHandler handlerHotels;
    private static final DataLoader dataLoader = new DataLoader();
    private final DatabaseHandler databaseHandler;

    private DataLoader() {
        handlerHotels = ServerSQLHandler.getInstance();
        databaseHandler = DatabaseHandler.getInstance();
    }

    public static DataLoader getDataLoader() {
        return dataLoader;
    }

    private void createTables() {
        databaseHandler.createTable();
        handlerHotels.createHotelTable();
        handlerHotels.createReviewTable();
        handlerHotels.createUserHotelTable();
        handlerHotels.createExpediaTable();
        handlerHotels.createCommentTable();
    }
    /**
     * loadData method uses to load data to hotelCollector and reviewCollector
     * @param args
     */
    public void loadData(String[] args) {
        createTables();
        ProgramArgumentParser argumentParser = new ProgramArgumentParser();
        argumentParser.parseArgs(args);
        String hotelPath;
        String reviewPath;
        try {
            hotelPath = argumentParser.getArgumentValue("-hotels");
            reviewPath = argumentParser.getArgumentValue("-reviews");
            THREAD = Integer.parseInt(argumentParser.getArgumentValue("-threads"));
        }  catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        HotelParser hotelParser = new HotelParser();
        hotelParser.parse(hotelPath);
        DirectoryTraversal reviewDataProcessor = new DirectoryTraversal(THREAD);
        reviewDataProcessor.processReviewData(reviewPath);
    }


    /**
     * get the thread
     * @return
     */
    public int getTHREAD() {
        return THREAD;
    }
}
