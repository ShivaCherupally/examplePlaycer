package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.model.EventSingleTicketsItemData;
import in.playcer.model.EventsDataTable;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;


/**
 * Created by OFFICE on 9/14/2015.
 */

@SuppressLint({"InflateParams", "ViewHolder"})
public class ParticipateSingleBookingActivity extends Activity {
    ListView sportDetailsLV;
    public SingleSportSlotBookingListAdapter mAdapter;

    int count = 0;
    TextView slotPriceAmountTV;
    TextView totalAmountTV;
    int fee = 0;
    int total = 0;

    TextView slotPriceAmountLabelTV;
    TextView feeAmountLabelTV;
    TextView feeAmountTV;

    RelativeLayout feeLableRL;
    RelativeLayout totalLableRL;
    View viewLineTV;

    String eventID = "";
    String eventName = "";
    String eventDate = "";
    String convenience_label = "";
    String convenience_fee = "";

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(ParticipateSingleBookingActivity.this);

    ArrayList<EventsDataTable> cartEventList;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_listview_events_tickets);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(ParticipateSingleBookingActivity.this, ParticipateFullDetailActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    eventID = dataBundle.getString("EVENT_ID");
                    eventName = dataBundle.getString("EVENT_NAME");
                    eventDate = dataBundle.getString("EVENT_DATE");
                    convenience_label = dataBundle.getString("EVENT_convenience_label");
                    convenience_fee = dataBundle.getString("EVENT_convenience_fee");
                }
                cartItemsInitilization();
                setupNavigation();
            }
        }
    }

    private void cartItemsInitilization() {
        slotPriceAmountTV = (TextView) findViewById(R.id.slotPriceAmountTVID);
        totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

        slotPriceAmountLabelTV = (TextView) findViewById(R.id.slotPriceAmountLabelTVID);
        feeAmountLabelTV = (TextView) findViewById(R.id.feeAmountLabelTVID);
        feeAmountTV = (TextView) findViewById(R.id.feeAmountTVID);

        feeLableRL = (RelativeLayout) findViewById(R.id.feeLableRLID);
        totalLableRL = (RelativeLayout) findViewById(R.id.totalLableRLID);
        viewLineTV = (View) findViewById(R.id.viewLineTVID);


        sportDetailsLV = (ListView) findViewById(R.id.sportDetailsLVID);
        mAdapter = new SingleSportSlotBookingListAdapter(ParticipateSingleBookingActivity.this, Utility.singleEventTicketsListData);
        sportDetailsLV.setVisibility(View.VISIBLE);
        sportDetailsLV.setAdapter(mAdapter);

        cartEventList = dataBaseHelper.getEventsDataTableList();

        if (cartEventList.size() > 0) {
            for (int i = 0; i < cartEventList.size(); i++) {
                int cc = Integer.parseInt(cartEventList.get(i).get_eventPrice());
                count = count + cc;
            }

            total = fee + count;
            slotPriceAmountTV.setText("" + getString(R.string.rs) + " " + count);
            totalAmountTV.setText("" + getString(R.string.rs) + " " + total);
            slotPriceAmountLabelTV.setText("SUBTOTAL");
            feeAmountLabelTV.setText(""+convenience_label.toString().toUpperCase());
            if (convenience_fee!= null && !convenience_fee.equalsIgnoreCase("")){
                int cc = Integer.parseInt(convenience_fee);
                feeAmountTV.setText("" + getString(R.string.rs) + " " +cc);
            } else {
                feeLableRL.setVisibility(View.GONE);
                totalLableRL.setVisibility(View.GONE);
                viewLineTV.setVisibility(View.GONE);
                slotPriceAmountLabelTV.setText("TOTAL");
            }
        }

        if (cartEventList.size() == 0) {
            total = 0;
        }

        Button confirmBtn = (Button) findViewById(R.id.checkOutBtnID);
        confirmBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cartEventList = dataBaseHelper.getEventsDataTableList();
                if (cartEventList.size() > 0) {
                    Intent in = new Intent(ParticipateSingleBookingActivity.this, ScreenCheckoutActivity.class);
                    in.putExtra("CART_AMOUNT", count);  //Sub Total
                    in.putExtra("CONVENIENCE_FEE", fee); //Tax or Other
                    in.putExtra("BOOKING_TYPE", "events");   // // Booking Type
                    in.putExtra("CREDITS", "0");   // Credits
                    in.putExtra("EVENT_ID", ""+eventID);
                    in.putExtra("EVENT_DATE", ""+eventDate);
                    startActivity(in);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), ParticipateSingleBookingActivity.this);
                }
            }
        });
    }

    public void setupNavigation() {
        RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
        headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);

        RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
        backAllRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        titleTV.setText(""+getResources().getString(R.string.tab_participate));

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);

        Button backBtn = (Button) findViewById(R.id.backBtnID);
        backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public class SingleSportSlotBookingListAdapter extends BaseAdapter {
        private ArrayList<EventSingleTicketsItemData> listData;
        private LayoutInflater layoutInflater = null;
        Activity mContext;
        ViewHolder holder = null;

        public SingleSportSlotBookingListAdapter(Activity context, ArrayList<EventSingleTicketsItemData> listData) {
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
        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.event_list_items_row_tickets, null, false);

                holder.cardView = (RelativeLayout) convertView.findViewById(R.id.sportsCardCVID);

                holder.singleSlotTimeTV = (TextView) convertView.findViewById(R.id.singleSlotTimeTVID);

                holder.amountText = (TextView) convertView.findViewById(R.id.balance_amount_list);
                holder.amountText.setBackgroundResource(R.drawable.amount_background2);
                holder.balanceContainer = convertView.findViewById(R.id.balance_container_list);
                holder.addToCartBtn = (Button) convertView.findViewById(R.id.addToCartBtnID);

                // This will now execute only for the first time of each row
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            showAmountByHeader(listData.get(position).get_event_ticket_price(), holder);

            holder.singleSlotTimeTV.setText(""+listData.get(position).get_event_ticket_name());

            holder.addToCartBtn.setTag(R.drawable.city_untick);
            holder.addToCartBtn.setVisibility(View.VISIBLE);
            holder.addToCartBtn.setBackgroundResource(R.drawable.city_untick);

            ArrayList<EventsDataTable> cartList = dataBaseHelper.getEventsDataTableList();
            if (cartList.size() > 0) {
                for (int i = 0; i < cartList.size(); i++) {
                    if (listData.get(position).get_event_ticket_name().equalsIgnoreCase(cartList.get(i).get_eventTime()) && position == cartList.get(i).get_eventItemPostion()) {
                        holder.addToCartBtn.setTag(R.drawable.city_tick);
                        holder.addToCartBtn.setBackgroundResource(R.drawable.city_tick);
                        break;
                    }
                }
            }

            holder.balanceContainer.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Not Do
                }
            });

            holder.addToCartBtn.setFocusable(false);
            holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    count = 0;
                    total = 0;
                    int pos = (Integer) v.getTag();
                    if (pos == R.drawable.city_untick) {
                        boolean isFreeSlot = false;
                        ArrayList<EventsDataTable> cartList = dataBaseHelper.getEventsDataTableList();
                        if (cartList.size() > 0) {
                            for (int i = 0; i < cartList.size(); i++) {
                                if ((cartList.get(i).get_eventPrice().equalsIgnoreCase("0") || cartList.get(i).get_eventPrice().equalsIgnoreCase("1"))){
                                    if (position == cartList.get(i).get_eventItemPostion()&& !(listData.get(position).get_event_ticket_price().equalsIgnoreCase("0")|| listData.get(position).get_event_ticket_price().equalsIgnoreCase("1"))) {
                                        isFreeSlot = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!isFreeSlot){
                            dataBaseHelper.addToEventsDataTableItems(new EventsDataTable(""+eventName, ""+eventDate, ""+listData.get(position).get_event_ticket_name(), ""+listData.get(position).get_event_ticket_price(), position, ""+listData.get(position).get_event_ticket_ID()));
                        } else {
                            //Utility.showCustomToast("Already 1 free slot has been added in your cart!", mContext);
                        }
                    } else {
                        ArrayList<EventsDataTable> cartList = dataBaseHelper.getEventsDataTableList();
                        if (cartList.size() > 0) {
                            for (int i = 0; i < cartList.size(); i++) {
                                String tttt = ""+""+listData.get(position).get_event_ticket_name();
                                if (tttt.equalsIgnoreCase(cartList.get(i).get_eventTime()) && position == cartList.get(i).get_eventItemPostion()) {
                                    dataBaseHelper.deleteEventItem(cartList.get(i).get_eventDate(), cartList.get(i).get_eventID());
                                }
                            }
                        }
                        //Utility.showCustomToast("Removed from cart!", mContext);
                    }

                    ArrayList<EventsDataTable> cartEventList1 = dataBaseHelper.getEventsDataTableList();
                    if (cartEventList1.size() > 0) {
                        for (int i = 0; i < cartEventList1.size(); i++) {
                            int cc = Integer.parseInt(cartEventList1.get(i).get_eventPrice());
                            count = count + cc;
                        }

                       // amountCardViewsCV.setVisibility(View.VISIBLE);
                        total = fee + count;
                        slotPriceAmountTV.setText("" + getString(R.string.rs) + " " + count);
                        totalAmountTV.setText("" + getString(R.string.rs) + " " + total);
                        slotPriceAmountLabelTV.setText("SUBTOTAL");
                        feeAmountLabelTV.setText(""+convenience_label.toString().toUpperCase());
                        if (convenience_fee!= null && !convenience_fee.equalsIgnoreCase("")){
                            int cc = Integer.parseInt(convenience_fee);
                            feeAmountTV.setText("" + getString(R.string.rs) + " " +cc);
                        } else {
                            feeLableRL.setVisibility(View.GONE);
                            totalLableRL.setVisibility(View.GONE);
                            viewLineTV.setVisibility(View.GONE);
                            slotPriceAmountLabelTV.setText("TOTAL");
                        }
                    }

                    if (cartEventList1.size() == 0) {
                        total = 0;
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

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

        private void showAmountByHeader(String amount, ViewHolder holder) {
            holder.amountText.setVisibility(View.VISIBLE);
            holder.balanceContainer.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(amount)) {
                if (amount.equals("0")) {
                    holder.amountText.setVisibility(View.GONE);
                    holder.balanceContainer.setVisibility(View.GONE);
                } else {
                    holder.amountText.setText(this.mContext.getString(R.string.rs) + " " + amount);
                }
            } else {
                if (!TextUtils.isEmpty(amount)) {
                    if (amount.equals("0")) {
                        holder.amountText.setVisibility(View.GONE);
                        holder.balanceContainer.setVisibility(View.GONE);
                    } else {
                        holder.amountText.setText(this.mContext.getString(R.string.rs) + " " + amount);
                    }
                } else {
                    holder.amountText.setVisibility(View.GONE);
                    holder.balanceContainer.setVisibility(View.GONE);
                }
            }
        }

        class ViewHolder {
            TextView singleSlotTimeTV;
            RelativeLayout cardView;
            View balanceContainer;
            TextView amountText;
            Button addToCartBtn;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }

}