package in.playcer.model;

/**
 * Created by Ravi on 29/07/15.
 */
public class CitiesListItem {
    private String cityName;

    public CitiesListItem() {
    }

    public CitiesListItem(String cityName) {
        this.cityName = cityName;
    }

    public String get_cityName() {
        return cityName;
    }

    public void set_cityName(String cityName) {
        this.cityName = cityName;
    }
}