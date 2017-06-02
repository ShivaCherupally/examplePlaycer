package in.playcer.model;

import java.util.ArrayList;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class EventSingleItemData {
    private int event_ID;
    private String event_name;
    private String content;
    private String excerpt;
    private String event_date;
    private String eventImageUtl;
    private int ticket_count;
    private ArrayList<EventSingleTicketsItemData> event_tickets;
    private String convenience_label;
    private String convenience_fee;

    public EventSingleItemData(int _event_ID, String _event_name,String _content, String _excerpt, String _event_date, String _eventImageUtl, int _ticket_count, ArrayList<EventSingleTicketsItemData> _event_tickets, String _convenience_label, String _convenience_fee) {
        this.event_ID = _event_ID;
        this.event_name = _event_name;
        this.content = _content;
        this.excerpt = _excerpt;
        this.event_date = _event_date;
        this.eventImageUtl = _eventImageUtl;
        this.ticket_count = _ticket_count;
        this.event_tickets = _event_tickets;
        this.convenience_label = _convenience_label;
        this.convenience_fee=_convenience_fee;
    }

    public EventSingleItemData() {
    }

    public int get_event_ID() {
        return event_ID;
    }

    public String get_event_name() {
        return event_name;
    }

    public String get_content() {
        return content;
    }

    public String get_excerpt() {
        return excerpt;
    }

    public String get_event_date() {
        return event_date;
    }

    public String get_eventImageUtl() {
        return eventImageUtl;
    }

    public int get_ticket_count() {
        return ticket_count;
    }

    public ArrayList<EventSingleTicketsItemData> get_event_tickets() {
        return event_tickets;
    }

    public String get_convenience_label() {
        return convenience_label;
    }

    public String get_convenience_fee() {
        return convenience_fee;
    }

    public void set_event_ID(int _event_ID) {
        this.event_ID = _event_ID;
    }

    public void set_content(String _content) {
        this.content = _content;
    }

    public void set_excerpt(String _excerpt) {
        this.excerpt = _excerpt;
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

    public void set_ticket_count(int _ticket_count) {
        this.ticket_count = _ticket_count;
    }

    public void set_event_tickets(ArrayList<EventSingleTicketsItemData> _event_tickets) {
        this.event_tickets = _event_tickets;
    }

    public void set_convenience_label(String _convenience_label) {
        this.convenience_label = _convenience_label;
    }

    public void set_convenience_fee(String _convenience_fee) {
        this.convenience_fee = _convenience_fee;
    }
}