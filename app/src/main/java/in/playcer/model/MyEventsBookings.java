package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class MyEventsBookings {
    private int bookingID;
    private String event_id;
    private String event_name;
    private String eventDate;
    private String eventTotal;


    public MyEventsBookings(int bookingID, String event_id, String event_name, String eventDate, String eventTotal) {
        this.bookingID = bookingID;
        this.event_id = event_id;
        this.event_name = event_name;
        this.eventDate = eventDate;
        this.eventTotal = eventTotal;
    }

    public int get_BookingID() {
        return bookingID;
    }

    public String get_event_id() {
        return event_id;
    }

    public String get_event_name() {
        return event_name;
    }

    public String get_eventDate() {
        return eventDate;
    }

    public String get_eventTotal() {
        return eventTotal;
    }

    public void set_BookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public void set_event_id(String event_id) {
        this.event_id = event_id;
    }

    public void set_event_name(String event_name) {
        this.event_name = event_name;
    }

    public void set_eventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void set_eventTotal(String eventTotal) {
        this.eventTotal = eventTotal;
    }
}
