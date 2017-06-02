package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.adapters.EventsBookingCartPlaceOrdersListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.CartDataTable;
import in.playcer.model.EventsDataTable;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 1/25/2016.
 */
public class MyBookingsSingleOrderListActivity extends Activity implements AdapterView.OnItemClickListener {
    public CartPlaceOrdersListAdapter mAdapter;
    ListView cartListviewLV;

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(MyBookingsSingleOrderListActivity.this);
    ArrayList<CartDataTable> myBookingsList = new ArrayList<CartDataTable>();
    ArrayList<EventsDataTable> myEventsBookingsList = new ArrayList<EventsDataTable>();
    TextView noCartListAvailableTV, extrasLabelTV, extrasAmountTV, subTotalLabelTV, subTotalAmountTV, creditsLabelTV;
    TextView creditsTV, couponAmountLabelTV, couponAmountTV, totalAmountLabelTV, totalAmountTV;
    LinearLayout creditsLL, couponLL, extrasAmountLL;
    private ProgressWheel progressWheel_CENTER_BOOKINGS;
    String ORDER_ID = "0";
    String EVENT_ID = "0";
    String FROM_WHERE_ACTIVITY = "0";

    String headeTitle = "SPORTS";
    LinearLayout noBookingsNetLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_bookings_single_order_list_screen);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);

        subTotalLabelTV = (TextView) findViewById(R.id.subTotalLabelTVID);
        creditsLabelTV = (TextView) findViewById(R.id.creditsLabelTVID);

        creditsTV = (TextView) findViewById(R.id.creditsTVID);
        extrasLabelTV = (TextView) findViewById(R.id.extrasLabelTVID);

        extrasAmountTV = (TextView) findViewById(R.id.extrasAmountTVID);
        subTotalAmountTV = (TextView) findViewById(R.id.subTotalAmountTVID);

        couponAmountLabelTV = (TextView) findViewById(R.id.couponAmountLabelTVID);
        couponAmountTV = (TextView) findViewById(R.id.couponAmountTVID);

        totalAmountLabelTV = (TextView) findViewById(R.id.totalAmountLabelTVID);
        totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

        creditsLL = (LinearLayout) findViewById(R.id.creditsLLID);
        couponLL = (LinearLayout) findViewById(R.id.couponLLID);
        extrasAmountLL = (LinearLayout) findViewById(R.id.extrasAmountLabelLLID);

        noCartListAvailableTV = (TextView) findViewById(R.id.noCartListAvailableTVID);
        cartListviewLV = (ListView) findViewById(R.id.cartListViewLVID);
        cartListviewLV.setOnItemClickListener(MyBookingsSingleOrderListActivity.this);

        progressWheel_CENTER_BOOKINGS = (ProgressWheel) findViewById(R.id.progress_wheel1);
        progressWheel_CENTER_BOOKINGS.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER_BOOKINGS.setRimColor(Color.LTGRAY);

        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                FROM_WHERE_ACTIVITY = dataBundle.getString("FROM_EVENTS_BOOKINGS");
                if (FROM_WHERE_ACTIVITY != null && FROM_WHERE_ACTIVITY.equalsIgnoreCase("EVENTS")) {
                    headeTitle = "EVENTS";
                } else {
                    headeTitle = "SPORTS";
                }
            }
        }

        setUpBookingOrdersList();
        setupNavigation();
    }

    private void setUpBookingOrdersList() {
        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                FROM_WHERE_ACTIVITY = dataBundle.getString("FROM_EVENTS_BOOKINGS");
                String cookieStr = dataBaseHelper.getRegisteredCookie();
                ORDER_ID = dataBundle.getString("ORDER_ID");
                if (FROM_WHERE_ACTIVITY!= null && FROM_WHERE_ACTIVITY.equalsIgnoreCase("EVENTS")){
                    headeTitle = "EVENTS";
                    EVENT_ID = dataBundle.getString("EVENT_ID");
                    if (ORDER_ID != null && !ORDER_ID.equalsIgnoreCase("0")){
                        RequestParams params = new RequestParams();
                        params.put("cookie", cookieStr);
                        params.put("orderId", ""+ORDER_ID);
                        getBookedsDetailsByOrderID(AppConstants.ORDER_FULL_EVENT_DETAILS_URL, params);
                    }
                } else {
                    headeTitle = "SPORTS";
                    if (ORDER_ID != null && !ORDER_ID.equalsIgnoreCase("0")){
                        RequestParams params = new RequestParams();
                        params.put("cookie", cookieStr);
                        params.put("orderId", ""+ORDER_ID);
                        getBookedsDetailsByOrderID(AppConstants.ORDER_FULL_DETAILS_URL, params);
                    }
                }
            }
        }
    }

    private void getBookedsDetailsByOrderID(String orderFullDetailsUrl, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(orderFullDetailsUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            String status = jsonResponse.getString("status").toString();
                            String sub_total = jsonResponse.optString("sub_total").toString();
                            String playcer_credits = jsonResponse.optString("playcer_credits").toString();
                            String coupon_amount = jsonResponse.optString("coupon_amount").toString();
                            String extras = jsonResponse.optString("extras").toString();
                            String total = jsonResponse.optString("total").toString();

                            //PLAYCER CREDITS VALUES HERE
                            if (playcer_credits!=null && playcer_credits.trim().length()>0){
                                creditsTV.setText("-"+getResources().getString(R.string.rs)+""+playcer_credits);
                            } else {
                                creditsLL.setVisibility(View.GONE);
                            }

                            if (coupon_amount!=null && coupon_amount.trim().length()>0){
                                couponAmountTV.setText("-" + getResources().getString(R.string.rs) + "" + coupon_amount);
                            } else {
                                couponLL.setVisibility(View.GONE);
                            }

                            if (extras!=null && extras.trim().length()>0){
                                extrasAmountTV.setText("+"+getResources().getString(R.string.rs)+""+extras);
                            } else {
                                extrasAmountLL.setVisibility(View.GONE);
                            }

                            subTotalAmountTV.setText(""+getResources().getString(R.string.rs)+""+sub_total);
                            totalAmountTV.setText(""+getResources().getString(R.string.rs)+""+total);

                            if (status.equalsIgnoreCase("ok")) {
                                if (FROM_WHERE_ACTIVITY !=null && FROM_WHERE_ACTIVITY.equalsIgnoreCase("EVENTS")) {
                                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                                    /*******  Returns null otherwise.  *******/
                                    // JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");
                                    final JSONArray jsonMainNode = jsonResponse.getJSONArray("event_bookings");

                                    /*********** Process each JSON Node ************/

                                    if (jsonMainNode.length() != 0) {
                                        myEventsBookingsList = new ArrayList<EventsDataTable>();
                                        for (int i = 0; i < jsonMainNode.length(); i++) {
                                            /****** Get Object for each JSON node.***********/
                                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                            /******* Fetch node values **********/
                                            String ticket_title = jsonChildNode.optString("title").toString();
                                            String ticket_date = jsonChildNode.optString("ticket_date").toString();
                                            String ticket_price = jsonChildNode.optString("ticket_price").toString();

                                            myEventsBookingsList.add(new EventsDataTable(ticket_title, ticket_date,"", ticket_price, 0,""));

                                            OutputData += "Node : \n\n     " + ticket_date + " | " + ticket_price;
                                            Log.i("JSON parse", OutputData);
                                        }

                                        /************ Show Output on screen/activity **********/
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                // This method will be executed once the timer is over
                                                if (jsonMainNode.length() != 0) {
                                                    EventsBookingCartPlaceOrdersListAdapter ca = new EventsBookingCartPlaceOrdersListAdapter(MyBookingsSingleOrderListActivity.this, myEventsBookingsList);
                                                    cartListviewLV.setAdapter(ca);
                                                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                                } else {
                                                    cartListviewLV.setVisibility(View.GONE);
                                                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                                    noCartListAvailableTV.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }, 1000);
                                    } else {
                                        cartListviewLV.setVisibility(View.GONE);
                                        progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                        noCartListAvailableTV.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                                    /*******  Returns null otherwise.  *******/
                                    // JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");
                                    final JSONArray jsonMainNode = jsonResponse.getJSONArray("bookings");

                                    /*********** Process each JSON Node ************/

                                    if (jsonMainNode.length() != 0) {
                                        myBookingsList = new ArrayList<CartDataTable>();
                                        for (int i = 0; i < jsonMainNode.length(); i++) {
                                            /****** Get Object for each JSON node.***********/
                                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                            /******* Fetch node values **********/
                                            String courtID = jsonChildNode.optString("courtID").toString();
                                            String club_name = jsonChildNode.optString("club_name").toString();
                                            String court_name = jsonChildNode.optString("court_name").toString();
                                            String sport_name = jsonChildNode.optString("sport_name").toString();
                                            String slot = jsonChildNode.optString("slot").toString();
                                            String date = jsonChildNode.optString("date").toString();
                                            String price = jsonChildNode.optString("price").toString();

                                            myBookingsList.add(new CartDataTable(club_name, sport_name, court_name, date, slot, price, courtID, "", "", 0));

                                            OutputData += "Node : \n\n     " + club_name + " | " + sport_name;
                                            Log.i("JSON parse", OutputData);
                                        }

                                        /************ Show Output on screen/activity **********/
                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                // This method will be executed once the timer is over
                                                if (jsonMainNode.length() != 0) {
                                                    CartPlaceOrdersListAdapter ca = new CartPlaceOrdersListAdapter(MyBookingsSingleOrderListActivity.this, myBookingsList);
                                                    cartListviewLV.setAdapter(ca);
                                                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                                } else {
                                                    cartListviewLV.setVisibility(View.GONE);
                                                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                                    noCartListAvailableTV.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }, 1000);
                                    } else {
                                        cartListviewLV.setVisibility(View.GONE);
                                        progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                        noCartListAvailableTV.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                            noCartListAvailableTV.setVisibility(View.GONE);
                            if (e != null) {
                                e.printStackTrace();
                            }
                            getBookingsTroubleConnecting();
                        }
                    } catch (Exception e) {
                        progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                        noCartListAvailableTV.setVisibility(View.GONE);
                        if (e != null) {
                            e.printStackTrace();
                        }
                        getBookingsTroubleConnecting();
                    }
                } else {
                    cartListviewLV.setVisibility(View.GONE);
                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                    noCartListAvailableTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                noCartListAvailableTV.setVisibility(View.GONE);
                if (content!= null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(MyBookingsSingleOrderListActivity.this, VerificationExistingActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else {
                    getBookingsTroubleConnecting();
                }
            }
        });
    }

    private void getBookingsTroubleConnecting() {
        noBookingsNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);
        noBookingsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noBookingsNetLL.setVisibility(View.GONE);
                progressWheel_CENTER_BOOKINGS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(MyBookingsSingleOrderListActivity.this)) {
                            setUpBookingOrdersList();
                        } else {
                            progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                            noBookingsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    public class CartPlaceOrdersListAdapter extends BaseAdapter {
        ArrayList<CartDataTable> mCartListData;
        private LayoutInflater layoutInflater = null;
        Activity mContext;

        public CartPlaceOrdersListAdapter(Activity context, ArrayList<CartDataTable> _ClubsListData) {
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

                holder.slotDateTV = (TextView) convertView.findViewById(R.id.slotDateTVID);

                holder.slotTimeTV = (TextView) convertView.findViewById(R.id.slotTimeTVID);

                holder.slotPriceTV = (TextView) convertView.findViewById(R.id.slotPriceTVID);

                // This will now execute only for the first time of each row
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.clubNameTV.setText("" + Html.fromHtml(mCartListData.get(position).get_pickedClubName_CLUB()));
            holder.courtTitleTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartCourtName()) + "-" + Html.fromHtml(mCartListData.get(position).get_cartSportName()));
            holder.sportNameTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartSportName()));
            holder.slotDateTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartDate()) + ", " + Html.fromHtml(mCartListData.get(position).get_cartSlotTime()));
            holder.slotTimeTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartSlotTime()));

            if (mCartListData.get(position).get_cartSlotPrice() != null && Integer.parseInt(mCartListData.get(position).get_cartSlotPrice()) !=0){
                holder.slotPriceTV.setText(getResources().getString(R.string.rs) + " " + Html.fromHtml(mCartListData.get(position).get_cartSlotPrice()));
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

    public void setupNavigation() {
        RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
        headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);

        RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
        backAllRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        titleTV.setText(""+headeTitle.toString().toUpperCase()+" BOOKINGS");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.VISIBLE);
        subTitleTV.setText("Order Number #"+ORDER_ID);

        Button backBtn = (Button) findViewById(R.id.backBtnID);
        backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        Button cartViewIcon = (Button) findViewById(R.id.moreBtnID);
        cartViewIcon.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (FROM_WHERE_ACTIVITY != null && FROM_WHERE_ACTIVITY.equalsIgnoreCase("EVENTS")) {
           // Intent transitionIntent = new Intent(MyBookingsSingleOrderListActivity.this, ParticipateFullDetailActivity.class);
           // transitionIntent.putExtra("EVENT_ID",""+myEventsBookingsList.get(position).get_eventID());
           // startActivity(transitionIntent);
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