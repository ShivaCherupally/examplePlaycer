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
import in.playcer.model.SportAllSlotsListData;
import in.playcer.model.SportAllSubSlotsListData;

/**
 * Created by HARIKRISHNA on 8/21/2015. at CaretTech
 */

@SuppressLint({"InflateParams", "ViewHolder"})
public class SportVenueListAdapter extends BaseAdapter {
    ArrayList<SportAllSlotsListData> mSportAllSlotsListData;
    private LayoutInflater layoutInflater = null;
    Context mContext;
    ArrayList<SportAllSubSlotsListData> subList;

    public SportVenueListAdapter(Context context, ArrayList<SportAllSlotsListData> mSportAllSlotsListData) {
        this.mContext = context;
        this.mSportAllSlotsListData = mSportAllSlotsListData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mSportAllSlotsListData.size();
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
            convertView = layoutInflater.inflate(R.layout.list_items_row_pick_full_slots, null, false);

            holder.cardView = (RelativeLayout) convertView.findViewById(R.id.sportsCardCVID);

            holder.courtTitleTV = (TextView) convertView.findViewById(R.id.slotTitleTVID);

            //row one
            holder.slotsTVs1 = (TextView) convertView.findViewById(R.id.row11TVID);
            holder.slotsTVs2 = (TextView) convertView.findViewById(R.id.row12TVID);
            holder.slotsTVs3 = (TextView) convertView.findViewById(R.id.row13TVID);
            holder.slotsTVs4 = (TextView) convertView.findViewById(R.id.row14TVID);
            holder.slotsTVs5 = (TextView) convertView.findViewById(R.id.row15TVID);
            holder.slotsTVs6 = (TextView) convertView.findViewById(R.id.row16TVID);
            holder.slotsTVs7 = (TextView) convertView.findViewById(R.id.row17TVID);

            //row two
            holder.slotsTVs21 = (TextView) convertView.findViewById(R.id.row21TVID);
            holder.slotsTVs22 = (TextView) convertView.findViewById(R.id.row22TVID);
            holder.slotsTVs23 = (TextView) convertView.findViewById(R.id.row23TVID);
            holder.slotsTVs24 = (TextView) convertView.findViewById(R.id.row24TVID);
            holder.slotsTVs25 = (TextView) convertView.findViewById(R.id.row25TVID);
            holder.slotsTVs26 = (TextView) convertView.findViewById(R.id.row26TVID);
            holder.slotsTVs27 = (TextView) convertView.findViewById(R.id.row27TVID);

            //row three
            holder.slotsTVs31 = (TextView) convertView.findViewById(R.id.row31TVID);
            holder.slotsTVs32 = (TextView) convertView.findViewById(R.id.row32TVID);
            holder.slotsTVs33 = (TextView) convertView.findViewById(R.id.row33TVID);
            holder.slotsTVs34 = (TextView) convertView.findViewById(R.id.row34TVID);
            holder.slotsTVs35 = (TextView) convertView.findViewById(R.id.row35TVID);
            holder.slotsTVs36 = (TextView) convertView.findViewById(R.id.row36TVID);
            holder.slotsTVs37 = (TextView) convertView.findViewById(R.id.row37TVID);

            //row four
            holder.slotsTVs41 = (TextView) convertView.findViewById(R.id.row41TVID);
            holder.slotsTVs42 = (TextView) convertView.findViewById(R.id.row42TVID);
            holder.slotsTVs43 = (TextView) convertView.findViewById(R.id.row43TVID);
            holder.slotsTVs44 = (TextView) convertView.findViewById(R.id.row44TVID);
            holder.slotsTVs45 = (TextView) convertView.findViewById(R.id.row45TVID);
            holder.slotsTVs46 = (TextView) convertView.findViewById(R.id.row46TVID);
            holder.slotsTVs47 = (TextView) convertView.findViewById(R.id.row47TVID);

            // This will now execute only for the first time of each row
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.courtTitleTV.setText("" + Html.fromHtml(mSportAllSlotsListData.get(position).get_sportCourtName()));

        holder.allViews = new TextView[] { holder.slotsTVs1,  holder.slotsTVs2, holder.slotsTVs3, holder.slotsTVs4, holder.slotsTVs5, holder.slotsTVs6, holder.slotsTVs7,
                holder.slotsTVs21, holder.slotsTVs22, holder.slotsTVs23, holder.slotsTVs24, holder.slotsTVs25, holder.slotsTVs26, holder.slotsTVs27
        , holder.slotsTVs31, holder.slotsTVs32, holder.slotsTVs33, holder.slotsTVs34, holder.slotsTVs35, holder.slotsTVs36, holder.slotsTVs37, holder.slotsTVs41, holder.slotsTVs42, holder.slotsTVs43, holder.slotsTVs44, holder.slotsTVs45, holder.slotsTVs46, holder.slotsTVs47};

        try {
            for (int i = 0; i < mSportAllSlotsListData.size(); i++) {
                if (i == position) {
                    subList = new ArrayList<SportAllSubSlotsListData>();
                    ArrayList<String> slotsTimesList = new ArrayList<String>();
                    subList.clear();
                    slotsTimesList.clear();
                    subList = mSportAllSlotsListData.get(i).get_sportSlotsList();

                    for (int k = 0; k < subList.size(); k++) {
                        holder.allViews[k].setVisibility(View.VISIBLE);
                        if (subList.get(k).get_slotStatus() == 0) {
                            holder.allViews[k].setText(" " + subList.get(k).get_slotTime() + " ");
                            holder.allViews[k].setTextColor(mContext.getResources().getColor(R.color.slot_available));
                        } else {
                            holder.allViews[k].setText(" " + subList.get(k).get_slotTime() + " ");
                            holder.allViews[k].setTextColor(mContext.getResources().getColor(R.color.slot_not_available));
                        }
                    }
                    break;
                }
            }
        } catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }  catch (Exception e){
            e.printStackTrace();
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

        // Row one
        TextView slotsTVs1;
        TextView slotsTVs2;
        TextView slotsTVs3;
        TextView slotsTVs4;
        TextView slotsTVs5;
        TextView slotsTVs6;
        TextView slotsTVs7;

        // Row two
        TextView slotsTVs21;
        TextView slotsTVs22;
        TextView slotsTVs23;
        TextView slotsTVs24;
        TextView slotsTVs25;
        TextView slotsTVs26;
        TextView slotsTVs27;

        // Row three
        TextView slotsTVs31;
        TextView slotsTVs32;
        TextView slotsTVs33;
        TextView slotsTVs34;
        TextView slotsTVs35;
        TextView slotsTVs36;
        TextView slotsTVs37;

        // Row Four
        TextView slotsTVs41;
        TextView slotsTVs42;
        TextView slotsTVs43;
        TextView slotsTVs44;
        TextView slotsTVs45;
        TextView slotsTVs46;
        TextView slotsTVs47;

        TextView[] allViews;
    }
}
