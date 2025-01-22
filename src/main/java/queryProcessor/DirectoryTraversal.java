package queryProcessor;

import jsonParser.ReviewParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/***
 * This class is to traverse the given file directory. Using ExecutorService to create works
 * Using phaser to notify and grab the task (register), after the task is done remove the task (deregister)
 * Using logger to debug the multithread code.
 * ReviewdateCollection is a collector for the review task
 * invertedIndex is a collector for words
 */
public class DirectoryTraversal{
    private final ExecutorService poolOfWorkers;
    private final Phaser phaser;
    private final Logger logger = LogManager.getLogger();

    /***
     * The constructor set the pool of threads, phaser, set the reviewCollection object and invertedIndex object
     * @param threads
     */
    public DirectoryTraversal(int threads) {
        poolOfWorkers = Executors.newFixedThreadPool(threads);
        phaser = new Phaser();
    }

    /***
     * This is an inner class for workers.
     * It implements the runnable for multitasking.
     */
    class ReviewCollector implements Runnable{
        private final Path file;
        public ReviewCollector(Path file) {
            this.file = file;
        }

        /**
         * use to run and add the reviewCollection
         */
        @Override
        public void run() {
            try {
                logger.debug("Parsing " + file.toString());
                ReviewParser reviewParser = new ReviewParser();
                reviewParser.parse(file.toString());
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                phaser.arriveAndDeregister();
            }
        }
    }

    /***
     * this method traverse the paths and get the file.
     * @param directory
     */
    private void traverseReviewPaths(Path directory) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directory)) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    traverseReviewPaths(path);
                } else {
                    if (path.toString().endsWith(".json")) {
                        phaser.register();
                        poolOfWorkers.submit(new ReviewCollector(path));
                        logger.debug("Created a worker for " + path);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("IOException: not able to open the directory " + directory);
        }
    }

    /***
     * this methode shutdown the pool and wait for all register thread is done.
     */
    private void waitAndDeregister() {
        try {
            phaser.awaitAdvance(0);
            poolOfWorkers.shutdownNow();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /***
     * this method is like a main in this class. it processes the methods together.
     * @param p
     */
    public void processReviewData(String p) {
        Path path = Paths.get(p);
        if (p.isEmpty()) return;
        traverseReviewPaths(path);
        waitAndDeregister();
    }
}
