package objects;
/**
 * This is a Review object to store the review information.
 * **/
public class Review implements Comparable<Review> {
    private String hotelId;
    private String reviewId;
    private double overAllRating;
    private String title;
    private String text;
    private String userNickName;
    private String date;

    public Review(String hotelId, String reviewId,
                  double overAllRating, String title,
                  String text, String userNickName, String date) {
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.overAllRating = overAllRating;
        this.title = title;
        this.text = text;
        this.userNickName = userNickName;
        this.date = date;
    }
    /**
     * Get hotel ID
     * @return
     */
    public String getHotelId() {
        return hotelId;
    }
    /**
     * Get Review ID
     * @return
     */
    public String getReviewId() {
        return reviewId;
    }
    /**
     * Get overall Rating
     * @return
     */
    public double getOverAllRating() {
        return overAllRating;
    }
    /**
     * Get title
     * @return
     */
    public String getTitle() {
        return title;
    }
    /**
     * Get text
     * @return
     */
    public String getText() {
        return text;
    }
    /**
     * Get user nickname
     * @return
     */
    public String getUserNickName() {
        return userNickName;
    }
    /**
     * Get date
     * @return
     */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverAllRating(double overAllRating) {
        this.overAllRating = overAllRating;
    }
    /**
     * Override the toString
     * @return
     */
    @Override
    public String toString() {
        return "Review{" +
                "HotelId='" + hotelId + '\'' +
                ", ReviewId='" + reviewId + '\'' +
                ", Overall Rating=" + overAllRating +
                ", Review Title='" + title + '\'' +
                ", Review Text='" + text + '\'' +
                ", User NickName='" + userNickName + '\'' +
                ", Date='" + date + '\'' +
                '}';
    }
    /**
     * Compare submission date, recent date list in front.
     * year > month > day. This is the order priority
     * @param otherReview is a review object
     * **/
    @Override
    public int compareTo(Review otherReview) {

        String[] thisReviewSplit = this.date.split("-");
        int year = Integer.parseInt(thisReviewSplit[0]);
        int month = Integer.parseInt(thisReviewSplit[1]);
        int day = Integer.parseInt(thisReviewSplit[2]);

        String[] otherReviewSplit = otherReview.date.split("-");
        int otherYear = Integer.parseInt(otherReviewSplit[0]);
        int otherMonth = Integer.parseInt(otherReviewSplit[1]);
        int otherDay = Integer.parseInt(otherReviewSplit[2]);

        if (year > otherYear) {
            return -1;
        } else if (year < otherYear) {
            return 1;
        } else {
            if (month > otherMonth) {
                return -1;
            } else if (month < otherMonth) {
                return 1;
            } else {
                if (day > otherDay) {
                    return -1;
                } else if (day < otherDay) {
                    return 1;
                } else {
                    return this.reviewId.compareTo(otherReview.reviewId);
                }
            }
        }
    }

}
