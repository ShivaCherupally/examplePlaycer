package in.playcer.model;

import java.util.ArrayList;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SingleMainSlotsListData {
    private int sportSlotID;
    private String sportSlotName;
    private ArrayList<SingleSlotsListData> sportSlotsList;

    public SingleMainSlotsListData(int sportSlotID, String sportSlotName, ArrayList<SingleSlotsListData> sportSlotsList) {
        this.sportSlotID = sportSlotID;
        this.sportSlotName = sportSlotName;
        this.sportSlotsList = sportSlotsList;
    }

    public SingleMainSlotsListData() {
    }

    public int get_sportSlotID() {
        return sportSlotID;
    }

    public String get_sportSlotName() {
        return sportSlotName;
    }

    public ArrayList<SingleSlotsListData> get_sportSlotsList() {
        return sportSlotsList;
    }


    public void set_sportSlotID(int sportSlotID) {
        this.sportSlotID = sportSlotID;
    }

    public void set_sportSlotName(String sportSlotName) {
        this.sportSlotName = sportSlotName;
    }

    public void set_sportSlotsList(ArrayList<SingleSlotsListData> sportImageUtl) {
        this.sportSlotsList = sportImageUtl;
    }
}
