package in.playcer.model;

/**
 * Created by OFFICE on 01/25/2016.
 */

public class CartDataTable {
    private String cartSportName;
    private String cartCourtName;
    private String cartDate;
    private String cartSlotTime;
    private String cartSlotPrice;
    private String pickedClubName_CLUB;
    public  String cartPickedCourtID;
    public String convenienceFee;
    public String convenienceLabel;
    private int position;

    public CartDataTable(String _pickedClubName_CLUB,String _cartSportName, String _cartCourtName,
                         String _cartDate, String _cartSlotTime, String _cartSlotPrice,
                         String _cartPickedCourtID, String _convenienceFee,String _convenienceLabel, int _position) {
        this.pickedClubName_CLUB = _pickedClubName_CLUB;
        this.cartSportName = _cartSportName;
        this.cartCourtName = _cartCourtName;
        this.cartDate = _cartDate;
        this.cartSlotTime = _cartSlotTime;
        this.cartSlotPrice = _cartSlotPrice;
        this.cartPickedCourtID = _cartPickedCourtID;
        this.convenienceFee = _convenienceFee;
        this.convenienceLabel = _convenienceLabel;
        this.position = _position;
    }

    public CartDataTable() {
    }
    public String get_pickedClubName_CLUB() {
        return pickedClubName_CLUB;
    }

    public String get_cartSportName() {
        return cartSportName;
    }

    public String get_cartCourtName() {
        return cartCourtName;
    }

    public String get_cartDate() {
        return cartDate;
    }

    public String get_cartSlotTime() {
        return cartSlotTime;
    }

    public String get_cartSlotPrice() {
        return cartSlotPrice;
    }

    public String get_cartPickedCourtID() {
        return cartPickedCourtID;
    }

    public String get_convenienceFee() {
        return convenienceFee;
    }

    public String get_convenienceLabel() {
        return convenienceLabel;
    }

    public int get_position() {
        return position;
    }

    public void set_pickedClubName_CLUB(String _pickedClubName_CLUB) {
        this.pickedClubName_CLUB = _pickedClubName_CLUB;
    }

    public void set_cartSportName(String cartSportName) {
        this.cartSportName = cartSportName;
    }

    public void set_cartCourtName(String cartCourtName) {
        this.cartCourtName = cartCourtName;
    }

    public void set_cartDate(String cartDate) {
        this.cartDate = cartDate;
    }

    public void set_cartSlotTime(String cartSlotTime) {
        this.cartSlotTime = cartSlotTime;
    }

    public void set_cartSlotPrice(String cartSlotPrice) {
        this.cartSlotPrice = cartSlotPrice;
    }

    public void set_cartPickedCourtID(String _cartPickedCourtID) {
        this.cartPickedCourtID = _cartPickedCourtID;
    }

    public void set_convenienceFee(String _convenienceFee) {
        this.convenienceFee = _convenienceFee;
    }

    public void set_convenienceLabel(String _convenienceLabel) {
        this.convenienceLabel = _convenienceLabel;
    }

    public void set_position(int _position) {
        this.position = _position;
    }
}
