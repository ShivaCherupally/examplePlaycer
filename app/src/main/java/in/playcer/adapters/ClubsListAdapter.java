package in.playcer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.ClubsListData;

/**
 * Created by HARIKRISHNA on 8/21/2015. at CaretTech
 */

@SuppressLint({"InflateParams", "ViewHolder"})
public class ClubsListAdapter extends BaseAdapter {
    ArrayList<ClubsListData> mClubsListData;
    private LayoutInflater layoutInflater = null;
    Context mContext;

    public ClubsListAdapter(Context context, ArrayList<ClubsListData> _ClubsListData) {
        this.mContext = context;
        this.mClubsListData = _ClubsListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mClubsListData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.list_items_row_clubs, null, false);

            holder.cardView = (RelativeLayout) convertView.findViewById(R.id.sportsCardCVID);

            holder.courtTitleTV = (TextView) convertView.findViewById(R.id.clubNameTVID);

            holder.landmarkTV = (TextView) convertView.findViewById(R.id.landmarkTVID);

            holder.availabilityTV = (TextView) convertView.findViewById(R.id.availabilityTVID);

            // This will now execute only for the first time of each row
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.courtTitleTV.setText("" + Html.fromHtml(mClubsListData.get(position).get_clubName()));
        holder.landmarkTV.setText("" + Html.fromHtml(mClubsListData.get(position).get_location()));

        if (mClubsListData.get(position).get_availability() == 1){
            //holder.availabilityTV.setBackgroundResource(R.drawable.avi_ticket);
            holder.availabilityTV.setVisibility(View.VISIBLE);
        } else {
            //holder.availabilityTV.setBackgroundResource(0);
            holder.availabilityTV.setVisibility(View.GONE);
        }
        switch (position % 2) {
            case 0:
                holder.cardView.setBackgroundResource(R.color.row1);
                break;
            case 1:
                holder.cardView.setBackgroundResource(R.color.white);
                break;
        }
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout cardView;
        TextView courtTitleTV;
        TextView landmarkTV;
        TextView availabilityTV;
    }
}