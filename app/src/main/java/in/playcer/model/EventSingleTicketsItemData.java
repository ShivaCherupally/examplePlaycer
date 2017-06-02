package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class EventSingleTicketsItemData {
    private int event_ticket_ID;
    private String event_ticket_name;
    private String event_ticket_price;

    public EventSingleTicketsItemData(int _event_ticket_ID, String event_ticket_name, String _event_ticket_price) {
        this.event_ticket_ID = _event_ticket_ID;
        this.event_ticket_name = event_ticket_name;
        this.event_ticket_price = _event_ticket_price;
    }

    public EventSingleTicketsItemData() {
    }

    public int get_event_ticket_ID() {
        return event_ticket_ID;
    }

    public String get_event_ticket_name() {
        return event_ticket_name;
    }

    public String get_event_ticket_price() {
        return event_ticket_price;
    }

    public void set_event_ticket_ID(int _event_ticket_ID) {
        this.event_ticket_ID = _event_ticket_ID;
    }

    public void set_event_ticket_name(String _event_ticket_name) {
        this.event_ticket_name = _event_ticket_name;
    }

    public void set_event_ticket_price(String _event_ticket_price) {
        this.event_ticket_price = _event_ticket_price;
    }
}