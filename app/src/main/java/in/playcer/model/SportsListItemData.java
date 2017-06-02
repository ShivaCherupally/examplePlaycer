package in.playcer.model;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SportsListItemData {
    private int sportID;
    private String sportName;
    private String slug;
    private String sportImageUtl;
    private String sportDetailsURL;

    public SportsListItemData(int sportID, String sportName, String _slug, String sportImageUtl, String sportDetailsURL) {
        this.sportID = sportID;
        this.sportName = sportName;
        this.slug = _slug;
        this.sportImageUtl = sportImageUtl;
        this.sportDetailsURL = sportDetailsURL;
    }

    public SportsListItemData() {
    }

    public int get_SportID() {
        return sportID;
    }

    public String get_sportName() {
        return sportName;
    }

    public String get_slug() {
        return slug;
    }

    public String get_sportImageUtl() {
        return sportImageUtl;
    }

    public String get_sportDetailsURL() {
        return sportDetailsURL;
    }

    public void set_SportID(int sportID) {
        this.sportID = sportID;
    }

    public void set_sportName(String sportName) {
        this.sportName = sportName;
    }

    public void set_slug(String _slug) {
        this.slug = _slug;
    }

    public void set_sportImageUtl(String sportImageUtl) {
        this.sportImageUtl = sportImageUtl;
    }

    public void set_sportDetailsURL(String sportDetailsURL) {
        this.sportDetailsURL = sportDetailsURL;
    }
}
