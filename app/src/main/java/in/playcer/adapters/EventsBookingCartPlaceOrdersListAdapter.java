package in.playcer.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.EventsDataTable;

/**
 * Created by HARIKRISHNA on 3/11/2016. at CaretTech
 */
public class EventsBookingCartPlaceOrdersListAdapter extends BaseAdapter {
    ArrayList<EventsDataTable> mCartListData;
    private LayoutInflater layoutInflater = null;
    Activity mContext;

    public EventsBookingCartPlaceOrdersListAdapter(Activity context, ArrayList<EventsDataTable> _ClubsListData) {
        this.mContext = context;
        this.mCartListData = _ClubsListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mCartListData.size();
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
            convertView = layoutInflater.inflate(R.layout.list_items_row_single_order_details_list, null, false);

            holder.cardView = (RelativeLayout) convertView.findViewById(R.id.sportsCardCVID);

            holder.clubNameTV = (TextView) convertView.findViewById(R.id.clubNameTVID);

            holder.courtTitleTV = (TextView) convertView.findViewById(R.id.courtNameTVID);

            holder.sportNameTV = (TextView) convertView.findViewById(R.id.sportNameTVID);
            holder.sportNameTV.setVisibility(View.GONE);

            holder.slotDateTV = (TextView) convertView.findViewById(R.id.slotDateTVID);
            holder.slotDateTV.setVisibility(View.GONE);

            holder.slotTimeTV = (TextView) convertView.findViewById(R.id.slotTimeTVID);
            holder.slotTimeTV.setVisibility(View.GONE);

            holder.slotPriceTV = (TextView) convertView.findViewById(R.id.slotPriceTVID);

            // This will now execute only for the first time of each row
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.clubNameTV.setText("" + Html.fromHtml(mCartListData.get(position).get_eventName()));
        holder.courtTitleTV.setText("" + Html.fromHtml(mCartListData.get(position).get_eventDate()));

        if (mCartListData.get(position).get_eventPrice() != null && Integer.parseInt(mCartListData.get(position).get_eventPrice()) !=0){
            holder.slotPriceTV.setText(mContext.getResources().getString(R.string.rs) + " " + Html.fromHtml(mCartListData.get(position).get_eventPrice()));
        } else {
            holder.slotPriceTV.setText("Free");
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

    class ViewHolder {
        RelativeLayout cardView;
        TextView courtTitleTV, clubNameTV;
        TextView sportNameTV;
        TextView slotDateTV, slotTimeTV, slotPriceTV;
    }
}