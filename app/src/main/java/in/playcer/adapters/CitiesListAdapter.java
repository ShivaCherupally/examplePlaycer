package in.playcer.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.CitiesListItem;
import in.playcer.utilities.AppDataBaseHelper;

/**
 * Created by HARIKRISHNA on 10/30/2015. at CaretTech
 */
public class CitiesListAdapter extends BaseAdapter {

    ArrayList<CitiesListItem> citiesList;
    Activity context;
    boolean[] itemChecked;
    AppDataBaseHelper dataBaseHelper;
    String currentCities = "";

    public CitiesListAdapter(Activity context, ArrayList<CitiesListItem> _citiesList) {
        super();
        this.context = context;
        this.citiesList = _citiesList;
        itemChecked = new boolean[citiesList.size()];
        dataBaseHelper = new AppDataBaseHelper(context);
    }

    private class ViewHolder {
        TextView cityNameTV;
        TextView ck1;
    }

    public int getCount() {
        return citiesList.size();
    }

    public Object getItem(int position) {
        return citiesList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cities_list_item, null);
            holder = new ViewHolder();
            holder.cityNameTV = (TextView) convertView.findViewById(R.id.textView1);
            holder.ck1 = (TextView) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.cityNameTV.setText(citiesList.get(position).get_cityName());
        currentCities = dataBaseHelper.getCurrentCity();
        if (holder.cityNameTV.getText().toString().equalsIgnoreCase(currentCities)){
               holder.ck1.setBackgroundResource(R.drawable.city_tick);
        } else {
             holder.ck1.setBackgroundResource(R.drawable.city_untick);
        }
        return convertView;
    }
}