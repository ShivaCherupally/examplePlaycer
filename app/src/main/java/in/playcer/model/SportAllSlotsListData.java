package in.playcer.model;

import java.util.ArrayList;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SportAllSlotsListData {
    private int sportSlotID;
    private String sportCourtName;
    private String availability;
    private String price_interval;
    private String address;
    private String location;
    private String city;
    private String latlng;
    private ArrayList<SportAllSubSlotsListData> sportSlotsList;
    private String sportSlotDetailsURL;


    public SportAllSlotsListData(int _sportSlotID, String _sportCourtName,String _availability,String _price_interval,String _address,String _location,String _city,String _latlng, ArrayList<SportAllSubSlotsListData> _sportSlotsList, String _sportSlotDetailsURL) {
        this.sportSlotID = _sportSlotID;
        this.sportCourtName = _sportCourtName;
        this.availability = _availability;
        this.price_interval = _price_interval;
        this.address = _address;
        this.location = _location;
        this.city = _city;
        this.latlng = _latlng;
        this.sportSlotsList = _sportSlotsList;
        this.sportSlotDetailsURL = _sportSlotDetailsURL;
    }

    public int get_sportSlotID() {
        return sportSlotID;
    }

    public String get_sportCourtName() {
        return sportCourtName;
    }

    public String get_availability() {
        return availability;
    }

    public String get_price_interval() {
        return price_interval;
    }

    public String get_address() {
        return address;
    }

    public String get_location() {
        return location;
    }

    public String get_city() {
        return city;
    }

    public String get_latlng() {
        return latlng;
    }

    public ArrayList<SportAllSubSlotsListData> get_sportSlotsList() {
        return sportSlotsList;
    }

    public String get_sportSlotDetailsURL() {
        return sportSlotDetailsURL;
    }

    public void set_sportSlotID(int sportSlotID) {
        this.sportSlotID = sportSlotID;
    }

    public void set_sportCourtName(String sportCourtName) {
        this.sportCourtName = sportCourtName;
    }

    public void set_availability(String availability) {
        this.availability = availability;
    }

    public void set_price_interval(String price_interval) {
        this.price_interval = price_interval;
    }

    public void set_address(String address) {
        this.address = address;
    }

    public void set_location(String location) {
        this.location = location;
    }

    public void set_city(String city) {
        this.city = city;
    }

    public void set_latlng(String latlng) {
        this.latlng = latlng;
    }

    public void set_sportSlotsList(ArrayList<SportAllSubSlotsListData> sportImageUtl) {
        this.sportSlotsList = sportImageUtl;
    }

    public void set_sportSlotDetailsURL(String sportSlotDetailsURL) {
        this.sportSlotDetailsURL = sportSlotDetailsURL;
    }
}
