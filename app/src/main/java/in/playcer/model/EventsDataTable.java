package in.playcer.model;

/**
 * Created by OFFICE on 01/25/2016.
 */

public class EventsDataTable {
    private String eventName;
    private String eventDate;
    private String eventTime;
    private String eventPrice;
    private int eventItemPostion;
    private String eventID;

    public EventsDataTable(String _eventName, String _eventDate, String _eventTime, String _eventPrice, int _eventItemPostion, String _eventID) {
        this.eventName = _eventName;
        this.eventDate = _eventDate;
        this.eventTime = _eventTime;
        this.eventPrice = _eventPrice;
        this.eventItemPostion = _eventItemPostion;
        this.eventID = _eventID;
    }

    public EventsDataTable() {
    }

    public String get_eventName() {
        return eventName;
    }

    public String get_eventDate() {
        return eventDate;
    }

    public String get_eventTime() {
        return eventTime;
    }

    public String get_eventPrice() {
        return eventPrice;
    }

    public int get_eventItemPostion() {
        return eventItemPostion;
    }

    public String get_eventID() {
        return eventID;
    }

    public void set_eventName(String eventName) {
        this.eventName = eventName;
    }

    public void set_eventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void set_eventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public void set_eventPrice(String eventPrice) {
        this.eventPrice = eventPrice;
    }

    public void set_eventID(String eventID) {
        this.eventID = eventID;
    }

    public void set_eventItemPostion(int _eventItemPostion) {
        this.eventItemPostion = _eventItemPostion;
    }
}
