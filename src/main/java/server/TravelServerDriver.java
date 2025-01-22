package server;

public class TravelServerDriver {
    public static final int PORT = 8080;

    /**
     * main to create the JettyServer and pass the port
     * @param args
     */
    public static void main(String[] args)  {
        if (args.length < 1) {
            args = new String[] {"-hotels", "dataset/hotels/hotels.json","-reviews","dataset/reviews","-threads","10"};
        }
        JettyServer jettyServer = new JettyServer(args, PORT);
        jettyServer.start();
    }
}
