package server;

import jsonParser.DataLoader;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import server.Auth.LoginServlet;
import server.Auth.LogoutServlet;
import server.Auth.RegistrationServlet;
import server.ExtraComment.CommentServlet;
import server.hotelPackage.HotelsServlet;
import server.hotelPackage.ReviewsServlet;
import server.hotelPackage.SingleHotelServlet;
import server.userPackage.ExpediaLinkServlet;
import server.userPackage.HomePageServlet;
import server.userPackage.LoginTimeServlet;
import server.userPackage.UserServlet;


public class JettyServer {
    private final String[] args;
    private Server server;
    private ServletContextHandler handler;

    public JettyServer(String[] args, int PORT) {
        this.args = args;
        this.server = new Server(PORT);
        this.handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    }

    /**
     * start the server.
     */
    public void start() {
        DataLoader loader = DataLoader.getDataLoader();
        loader.loadData(args); // need it
        handler.addServlet(RegistrationServlet.class, "/register");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(HotelsServlet.class, "/login/hotels");
        handler.addServlet(SingleHotelServlet.class, "/login/hotel/info");
        handler.addServlet(ReviewsServlet.class, "/api/reviews");
        handler.addServlet(UserServlet.class, "/api/user");
        handler.addServlet(HomePageServlet.class, "/myHome");
        handler.addServlet(LoginTimeServlet.class, "/api/loginTime");
        handler.addServlet(ExpediaLinkServlet.class, "/api/expediaLink");
        handler.addServlet(CommentServlet.class, "/api/comments");

        //these lines from the codeExample for javascript to works.
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("templates");
        HandlerList handlers = new HandlerList();

        VelocityEngine velocity = new VelocityEngine();
        velocity.init();
        handler.setAttribute("templateEngine", velocity);

        handlers.setHandlers(new Handler[] { resourceHandler, handler});
        server.setHandler(handlers);

        try {
            System.out.println("Server started");
            server.start();
            server.join();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Could not start the server.");
        }
    }
}
