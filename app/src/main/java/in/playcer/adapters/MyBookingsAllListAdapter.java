package in.playcer.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.R;
import in.playcer.model.MyBookings;
import in.playcer.utilities.AppDataBaseHelper;

/**
 * Created by HARIKRISHNA on 8/21/2015. at CaretTech
 */

@SuppressLint({ "InflateParams", "ViewHolder" })
public class MyBookingsAllListAdapter extends BaseAdapter {
	private int size;
	ArrayList<MyBookings> myBookingsList;
	Activity mActivity;
	AppDataBaseHelper dataBaseHelper;
	String role;
	private LayoutInflater layoutInflater = null;

	public MyBookingsAllListAdapter(Activity _activity, int _size, ArrayList<MyBookings> _myBookingsList) {
		this.mActivity = _activity;
		this.size = _size;
		this.myBookingsList = _myBookingsList;
		dataBaseHelper = new AppDataBaseHelper(mActivity);
		role = dataBaseHelper.getRegisteredRole();
		layoutInflater = LayoutInflater.from(_activity);
	}

	@Override
	public int getCount() {
		return myBookingsList.size();
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
	        convertView = layoutInflater.inflate(R.layout.list_items_row_mybooking, null, false);

			holder.bookingIDTV =  (TextView) convertView.findViewById(R.id.bookedIDTVID);
			holder.bookedDateTV = (TextView) convertView.findViewById(R.id.bookedDateTVID);
			holder.bookingTotalTV = (TextView) convertView.findViewById(R.id.bookingTotalTVID);
			holder.bookingTotalTV.setBackgroundResource(R.drawable.amount_background2);
			holder.byManagerBtn = (TextView) convertView.findViewById(R.id.byManagerBtnID);

	        // This will now execute only for the first time of each row
	        convertView.setTag(holder);
	    } else {
	        holder = (ViewHolder) convertView.getTag();
	    }

		MyBookings ci = myBookingsList.get(position);
		holder.bookingIDTV.setText(""+ci.get_BookingID());
		holder.bookedDateTV.setText(""+ci.get_bookingDate());
		holder.bookingTotalTV.setText( mActivity.getResources().getString(R.string.rs) +" "+ ci.get_bookingTotal());
		if (role != null && role.equalsIgnoreCase("manager")) {
			if (ci.get_bookedByManager().equalsIgnoreCase("0")){
				holder.byManagerBtn.setVisibility(View.VISIBLE);
			} else {
				holder.byManagerBtn.setVisibility(View.GONE);
			}
		}

	    return convertView;
	}

	static class ViewHolder {
		protected TextView bookingIDTV;
		protected TextView bookedDateTV;
		protected TextView bookingTotalTV;
		protected TextView byManagerBtn;
	}
}