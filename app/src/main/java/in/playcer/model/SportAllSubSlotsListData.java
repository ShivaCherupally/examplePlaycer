package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SportAllSubSlotsListData {
    private int slotStatus;
    private String slotTime;

    public SportAllSubSlotsListData(int slotStatus, String slotTime) {
        this.slotStatus = slotStatus;
        this.slotTime = slotTime;
    }

    public SportAllSubSlotsListData() {
    }

    public int get_slotStatus() {
        return slotStatus;
    }

    public String get_slotTime() {
        return slotTime;
    }

    public void set_slotStatus(int slotStatus) {
        this.slotStatus = slotStatus;
    }
    public void set_slotTime(String slotTime) {
        this.slotTime = slotTime;
    }
}
