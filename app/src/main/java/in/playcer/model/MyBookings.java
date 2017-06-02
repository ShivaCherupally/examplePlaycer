package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class MyBookings {
    private int bookingID;
    private String bookingDate;
    private String bookingTotal;
    private String bookedByManager;


    public MyBookings(int bookingID, String bookingDate,String bookingTotal, String bookedByManager) {
        this.bookingID = bookingID;
        this.bookingDate = bookingDate;
        this.bookingTotal = bookingTotal;
        this.bookedByManager = bookedByManager;
    }

    public int get_BookingID() {
        return bookingID;
    }


    public String get_bookingDate() {
        return bookingDate;
    }

    public String get_bookingTotal() {
        return bookingTotal;
    }

    public String get_bookedByManager() {
        return bookedByManager;
    }

    public void set_BookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public void set_bookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void set_bookingTotal(String bookingTotal) {
        this.bookingTotal = bookingTotal;
    }
    public void set_bookedByManager(String bookedByManager) {
        this.bookedByManager = bookedByManager;
    }
}
