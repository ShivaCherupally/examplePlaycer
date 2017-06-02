package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class EventsListItemData {
    private int event_ID;
    private String event_name;
    private String event_date;
    private String eventImageUtl;

    public EventsListItemData(int _event_ID, String event_name, String _event_date, String _eventImageUtl) {
        this.event_ID = _event_ID;
        this.event_name = event_name;
        this.event_date = _event_date;
        this.eventImageUtl = _eventImageUtl;
    }

    public EventsListItemData() {
    }

    public int get_event_ID() {
        return event_ID;
    }

    public String get_event_name() {
        return event_name;
    }

    public String get_event_date() {
        return event_date;
    }

    public String get_eventImageUtl() {
        return eventImageUtl;
    }

    public void set_event_ID(int _event_ID) {
        this.event_ID = _event_ID;
    }

    public void set_event_name(String _event_name) {
        this.event_name = _event_name;
    }

    public void set_event_date(String _event_date) {
        this.event_date = _event_date;
    }

    public void set_eventImageUtl(String _eventImageUtl) {
        this.eventImageUtl = _eventImageUtl;
    }
}