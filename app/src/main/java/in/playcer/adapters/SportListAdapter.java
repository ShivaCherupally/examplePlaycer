package in.playcer.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.SportsListItemData;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/21/2015. at CaretTech
 */

@SuppressLint({ "InflateParams", "ViewHolder" })
public class SportListAdapter extends BaseAdapter {
	private ArrayList<SportsListItemData> listData;
	private LayoutInflater layoutInflater = null;
	Context mContext;
	//Animation animation = null;

	public SportListAdapter(Context context, ArrayList<SportsListItemData> listData) {
		this.mContext = context;
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
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
	public View getView(int position, View convertView, ViewGroup paramViewGroup) {
	    ViewHolder holder = null;
	    if (convertView == null) {
	        holder = new ViewHolder();
	        convertView = layoutInflater.inflate(R.layout.list_items_row_sports, null, false);
	        
	        holder.cardView = (FrameLayout) convertView.findViewById(R.id.sportsCardFrameFLID);
	        holder.cardView.getLayoutParams().height = (int) (Utility.screenHeight / 3.55);

            holder.layoutFL = (ImageView) convertView.findViewById(R.id.sportImage);

	        holder.sportName = (TextView) convertView.findViewById(R.id.sportNameTVID);

	        // This will now execute only for the first time of each row
	        convertView.setTag(holder);
	    } else {
	        holder = (ViewHolder) convertView.getTag();
	    }

			holder.sportName.setText("" + Html.fromHtml(listData.get(position).get_sportName()));

			Picasso.with(mContext).load(listData.get(position).get_sportImageUtl())
						//.resize(Utility.screenWidth, (int) (Utility.screenHeight / 3.2))
				.placeholder(R.drawable.placeholder)
				.into(holder.layoutFL);

		/*try {
			animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
			animation.setDuration(1000);
			convertView.startAnimation(animation);
			animation = null;
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	    return convertView;
	}

	static class ViewHolder {
        FrameLayout cardView;
		ImageView layoutFL;
		TextView sportName;
	}
}