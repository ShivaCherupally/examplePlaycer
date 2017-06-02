package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SingleSlotsListData {
    private int slotStatus;
    private String slotTime;
    private String slot_time_actual;
    private String price;

    public SingleSlotsListData(int slotStatus, String slotTime, String _slot_time_actual, String _price) {
        this.slotStatus = slotStatus;
        this.slotTime = slotTime;
        this.slot_time_actual = _slot_time_actual;
        this.price = _price;
    }

    public SingleSlotsListData() {
    }

    public int get_slotStatus() {
        return slotStatus;
    }

    public String get_slotTime() {
        return slotTime;
    }

    public String get_slot_time_actual() {
        return slot_time_actual;
    }

    public String get_price() {
        return price;
    }

    public void set_slotStatus(int slotStatus) {
        this.slotStatus = slotStatus;
    }
    public void set_slotTime(String slotTime) {
        this.slotTime = slotTime;
    }
    public void set_slot_time_actual(String _slot_time_actual) {
        this.slot_time_actual = _slot_time_actual;
    }
    public void set_price(String _price) {
        this.price = _price;
    }
}
