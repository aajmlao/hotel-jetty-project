package server.hotelPackage;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;


public class HotelsServlet extends HttpServlet {
    private final ServerSQLHandler serverSQLHandler = ServerSQLHandler.getInstance();
    /**
     * HotelsServlet doGet Method
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect("/login");
        }
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        String hotelQuery = request.getParameter("hotelQuery");
        hotelQuery = StringEscapeUtils.escapeHtml4(hotelQuery);
        String page = request.getParameter("page");
        page = StringEscapeUtils.escapeHtml4(page);
        String listSize = request.getParameter("listSize");
        listSize = StringEscapeUtils.escapeHtml4(listSize);

        int numPage = page.isEmpty() ? 1 : Integer.parseInt(page);
        int numSize = listSize.isEmpty() ? 10 : Integer.parseInt(listSize);

        int offset = (numPage - 1) * numSize;
        serverSQLHandler.queryHotels(hotelQuery, numSize, offset, response);
    }

}
