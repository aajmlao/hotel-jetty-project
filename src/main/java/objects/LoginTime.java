package objects;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * singleton class to set and get date and time now.
 */
public class LoginTime {
    private LocalDate date;
    private LocalTime time;
    private static final LoginTime loginTime = new LoginTime();

    private LoginTime() {}

    public static LoginTime getInstance() {
        return loginTime;
    }

    /**
     * set date now
     */
    public void setDateNow() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        date = LocalDate.parse(currentDate.format(dateFormat), dateFormat);
    }

    /**
     * set Time now
     */
    public void setTimeNow() {
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        time = LocalTime.parse( currentTime.format(timeFormat), timeFormat);
    }

    /**
     * get the data was set login
     * @return
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * get the Time was set in login
     * @return
     */
    public LocalTime getTime() {
        return time;
    }

}
