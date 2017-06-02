package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class ClubsListData {
    private int clubID;
    private String clubName;
    private int availability;
    private String address;
    private String location;
    private String city;
    private String latlng;
    private String phone;


    public ClubsListData(int _clubID, String _clubName, int _availability, String _address, String _location, String _city, String _latlng, String _phone) {
        this.clubID = _clubID;
        this.clubName = _clubName;
        this.availability = _availability;
        this.address = _address;
        this.location = _location;
        this.city = _city;
        this.latlng = _latlng;
        this.phone = _phone;
    }

    public int get_clubID() {
        return clubID;
    }

    public String get_clubName() {
        return clubName;
    }

    public int get_availability() {
        return availability;
    }

    public void set_clubID(int clubID) {
        this.clubID = clubID;
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

    public String get_phone() {
        return phone;
    }

    public void set_clubName(String clubName) {
        this.clubName = clubName;
    }

    public void set_availability(int availability) {
        this.availability = availability;
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

    public void set_phone(String _phone) {
        this.phone = _phone;
    }
}
