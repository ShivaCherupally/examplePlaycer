package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.libs.ProgressWheel;
import in.playcer.model.CartDataTable;
import in.playcer.model.SingleSlotsListData;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */

@SuppressLint({"InflateParams", "ViewHolder"})
public class SingleSportSlotBookingActivity extends Activity implements AdapterView.OnItemClickListener {
    ListView sportDetailsLV;
    public SingleSportSlotBookingListAdapter mAdapter;
    LinearLayout menuMainLL;
    String singleSportURL = null;
    String PickedCourtName = "";
    String pickedSportName = "";
    String PickedDate = "";
    String price_interval = "";
    String address = "";
    String latLng = "";
    String location = "";
    String city = "";
    String number = "";
    String pickedClubName_CLUB = "";

    String convenienceFee = "";
    String sclabel = "";

    String slotPrice = "";
    int MainSingleSportSlotsListCount = 0;
    public ArrayList<SingleSlotsListData> mSingleSlotsListData = new ArrayList<SingleSlotsListData>();
    private ProgressWheel progressWheel_CENTER;

    int pickedCourtID = -1;

    LinearLayout noNetLL;

    int count = 0;
    View balanceContainerHeader;
    TextView amountTextHeaderTV;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(SingleSportSlotBookingActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_a_single_slot_listview);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(SingleSportSlotBookingActivity.this, SportsPickVenueActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.setDimensions(this);

                    cartItemsInitilization();

                    progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
                    progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
                    progressWheel_CENTER.setRimColor(Color.LTGRAY);

                    noNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);

                    menuMainLL = (LinearLayout) findViewById(R.id.menuMainLLID);
                    //menuMainLL.getLayoutParams().height = (int) (Utility.screenHeight / 11.1);

                    sportDetailsLV = (ListView) findViewById(R.id.sportDetailsLVID);
                    sportDetailsLV.setOnItemClickListener(SingleSportSlotBookingActivity.this);

                    singleSportURL = dataBundle.getString(Utility.KEY_SINGLE_SPORT_SINGLE_SLOTS_URL);
                    PickedCourtName = dataBundle.getString(Utility.KEY_PickedCourtName);
                    pickedClubName_CLUB = dataBundle.getString(Utility.KEY_pickedClubName);
                    pickedSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    PickedDate = dataBundle.getString(Utility.KEY_pickedDate);
                    pickedCourtID = dataBundle.getInt(Utility.KEY_pickedCourtID);
                    price_interval = dataBundle.getString(Utility.KEY_price_interval);
                    address = dataBundle.getString(Utility.KEY_address);
                    latLng = dataBundle.getString(Utility.KEY_latlng);
                    location = dataBundle.getString(Utility.KEY_location);
                    city = dataBundle.getString(Utility.KEY_city);
                    number = dataBundle.getString(Utility.KEY_phone);

                    TextView pickedCourtTV = (TextView) findViewById(R.id.pickedCourtTVID);
                    TextView pricePerSlotTV = (TextView) findViewById(R.id.pricePerSlotTVID);
                    TextView pickeddateTV = (TextView) findViewById(R.id.pickeddateTVID);
                    TextView yearTV = (TextView) findViewById(R.id.yearTVID);

                    pickedCourtTV.setText("" + Html.fromHtml(PickedCourtName));
                    pricePerSlotTV.setText("" + Html.fromHtml(price_interval));

                    if (price_interval.trim().length() > 1) {
                        String[] price_intervalStr = price_interval.split("Rs.");
                        String p1 = price_intervalStr[0];
                        String p2 = price_intervalStr[1];
                        slotPrice = p2.replace(" ", "");
                    }

                    String[] parts1 = PickedDate.split(",");
                    String dayMont = parts1[0];
                    String year = parts1[1];

                    pickeddateTV.setText("" + dayMont);
                    yearTV.setText("" + year);

                    if (singleSportURL != null) {
                        try {
                            if (Utility.isOnline(SingleSportSlotBookingActivity.this)) {
                                getSingleSlots(singleSportURL);
                            } else {
                                singleSlotTroubleConnecting(singleSportURL);
                            }
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                    setupNavigation();
                }
            }
        }
    }

    private void cartItemsInitilization() {
        amountTextHeaderTV = (TextView) findViewById(R.id.balance_amount);
        balanceContainerHeader = findViewById(R.id.balance_container);
        balanceContainerHeader.setBackgroundResource(R.drawable.ic_add_shopping_cart_white_48dp);
        balanceContainerHeader.getLayoutParams().width = (int) (Utility.screenHeight / 14.0);
        balanceContainerHeader.getLayoutParams().height = (int) (Utility.screenHeight / 14.0);
        /*ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
        if (cartList.size() > 0) {
            balanceContainerHeader.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setTextColor(getResources().getColor(R.color.white));
            amountTextHeaderTV.setBackgroundResource(R.drawable.amount_background_cart);
            amountTextHeaderTV.setText("" + cartList.size());
        } else {
            balanceContainerHeader.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setVisibility(View.GONE);
        }*/
        cartTotalAmount();

        balanceContainerHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    Intent cartPageTogo = new Intent(SingleSportSlotBookingActivity.this, CartPlaceOrdersListActivity.class);
                    cartPageTogo.putExtra(Utility.KEY_FROM_TO_CART, "FROM_SINGLE_SLOTS");
                    startActivity(cartPageTogo);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), SingleSportSlotBookingActivity.this);
                }
            }
        });
    }

    private void singleSlotTroubleConnecting(final String _singleSportURL) {
        noNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noNetLL.setVisibility(View.GONE);
                sportDetailsLV.setVisibility(View.GONE);
                progressWheel_CENTER.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(SingleSportSlotBookingActivity.this)) {
                            noNetLL.setVisibility(View.GONE);
                            getSingleSlots(_singleSportURL);
                        } else {
                            progressWheel_CENTER.setVisibility(View.GONE);
                            noNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       /* String role = dataBaseHelper.getRegisteredRole();
        Log.w("role", "" + role);
        if (mSingleSlotsListData.size() > 0) {
            if (mSingleSlotsListData.get(position).get_slotStatus() == 0) {
                if (role != null && role.equalsIgnoreCase("manager")) {
                    Intent confirmScreen = new Intent(SingleSportSlotBookingActivity.this, SlotConfirmationManagerActivity.class);
                    confirmScreen.putExtra(Utility.KEY_Confirm, "AVAILABLE");
                    confirmScreen.putExtra(Utility.KEY_PickedCourtName, PickedCourtName);
                    confirmScreen.putExtra(Utility.KEY_pickedClubName, pickedClubName_CLUB);
                    confirmScreen.putExtra(Utility.KEY_SPORT_NAME, pickedSportName);
                    confirmScreen.putExtra(Utility.KEY_pickedDate, PickedDate);
                    confirmScreen.putExtra(Utility.KEY_pickedCourtID, pickedCourtID);
                    confirmScreen.putExtra(Utility.KEY_address, address);
                    confirmScreen.putExtra(Utility.KEY_latlng, latLng);
                    confirmScreen.putExtra(Utility.KEY_PickedSlotTime, mSingleSlotsListData.get(position).get_slotTime());
                    confirmScreen.putExtra(Utility.KEY_location, location);
                    confirmScreen.putExtra(Utility.KEY_city, city);
                    startActivity(confirmScreen);
                } else {
                    Intent confirmScreen = new Intent(SingleSportSlotBookingActivity.this, SlotConfirmationActivity.class);
                    confirmScreen.putExtra(Utility.KEY_Confirm, "AVAILABLE");
                    confirmScreen.putExtra(Utility.KEY_pickedPosition, position);
                    confirmScreen.putExtra(Utility.KEY_PickedCourtName, PickedCourtName);
                    confirmScreen.putExtra(Utility.KEY_pickedClubName, pickedClubName_CLUB);
                    confirmScreen.putExtra(Utility.KEY_SPORT_NAME, pickedSportName);
                    confirmScreen.putExtra(Utility.KEY_pickedDate, PickedDate);
                    confirmScreen.putExtra(Utility.KEY_pickedCourtID, pickedCourtID);
                    confirmScreen.putExtra(Utility.KEY_address, address);
                    confirmScreen.putExtra(Utility.KEY_latlng, latLng);
                    confirmScreen.putExtra(Utility.KEY_PickedSlotTime, mSingleSlotsListData.get(position).get_slotTime());
                    confirmScreen.putExtra(Utility.KEY_location, location);
                    confirmScreen.putExtra(Utility.KEY_city, city);
                    confirmScreen.putExtra(Utility.KEY_phone, number);
                    confirmScreen.putExtra(Utility.KEY_price_interval, mSingleSlotsListData.get(position).get_price());
                    confirmScreen.putExtra(Utility.KEY_convenience, convenienceFee);
                    confirmScreen.putExtra(Utility.KEY_sclabel, sclabel);
                    startActivity(confirmScreen);
                }
            } else {
                Utility.showCustomToast("Sorry! Slot Unavailable.", SingleSportSlotBookingActivity.this);
            }
        }
        MyApplication.getInstance().trackEvent("Single Slot", "Slot Tapped", "Picked Single Slot");*/
    }

    public void getSingleSlots(final String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;
                        /******* Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        jsonResponse = new JSONObject(response);

                        /*****
                         * Returns the value mapped by name if it exists and is a JSONArray.
                         ***/
                        /******* Returns null otherwise. *******/
                        JSONArray jsonMainNode = jsonResponse.getJSONArray("slots");

                        /*********** Process each JSON Node ************/
                        MainSingleSportSlotsListCount = jsonMainNode.length();

                        if (MainSingleSportSlotsListCount != 0) {
                            mSingleSlotsListData = new ArrayList<SingleSlotsListData>();

                            for (int i = 0; i < MainSingleSportSlotsListCount; i++) {
                                /****** Get Object for each JSON node. ***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                int all_slot_id = Integer.parseInt(jsonChildNode.optString("status").toString());
                                String all_sport_slot_name = jsonChildNode.optString("slot").toString();
                                String slot_time_actual = jsonChildNode.optString("slot_time").toString();
                                String price = jsonChildNode.optString("price").toString();

                                mSingleSlotsListData.add(new SingleSlotsListData(all_slot_id, all_sport_slot_name, slot_time_actual, price));

                                OutputData += "Node : \n\n     " + all_slot_id + " | " + all_sport_slot_name + " | " + " \n\n ";
                                Log.i("JSON parse", OutputData);
                            }

                            convenienceFee = jsonResponse.getString("convenience");
                            sclabel = jsonResponse.getString("sclabel");

                            Log.w("Main", "Main Count = " + MainSingleSportSlotsListCount);
                            mAdapter = new SingleSportSlotBookingListAdapter(SingleSportSlotBookingActivity.this, mSingleSlotsListData);
                            sportDetailsLV.setVisibility(View.VISIBLE);
                            sportDetailsLV.setAdapter(mAdapter);
                        } else {
                            singleSlotTroubleConnecting(url);
                        }

                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        singleSlotTroubleConnecting(url);
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        singleSlotTroubleConnecting(url);
                    }
                } else {
                    singleSlotTroubleConnecting(url);
                }
                progressWheel_CENTER.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                progressWheel_CENTER.setVisibility(View.GONE);
                singleSlotTroubleConnecting(url);
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
                onBackPressedAnimationByCHK();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        titleTV.setText("PICK A SLOT");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.VISIBLE);
        subTitleTV.setText("" + Html.fromHtml(pickedSportName.toUpperCase()));

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
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Slots");

        cartTotalAmount();
        if (mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    private void cartTotalAmount() {
        ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
        if (cartList.size() > 0) {
            balanceContainerHeader.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setTextColor(getResources().getColor(R.color.white));
            amountTextHeaderTV.setBackgroundResource(R.drawable.amount_background_cart);
            amountTextHeaderTV.setText("" + cartList.size());
        } else {
            balanceContainerHeader.setVisibility(View.VISIBLE);
            amountTextHeaderTV.setVisibility(View.GONE);
        }
    }

    public class SingleSportSlotBookingListAdapter extends BaseAdapter {
        private ArrayList<SingleSlotsListData> listData;
        private LayoutInflater layoutInflater = null;
        Activity mContext;
        ViewHolder holder = null;

        public SingleSportSlotBookingListAdapter(Activity context, ArrayList<SingleSlotsListData> listData) {
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
                convertView = layoutInflater.inflate(R.layout.list_items_row_single_slot_booking, null, false);

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
            showAmountByHeader(listData.get(position).get_price(), holder);

            /*if (position==0){
                holder.amountText.setText(this.mContext.getString(R.string.rs) + " 0");
            } else if (position==1){
                holder.amountText.setText(this.mContext.getString(R.string.rs) + " 0");
            }*/

            holder.singleSlotTimeTV.setText("" + listData.get(position).get_slot_time_actual());

            holder.addToCartBtn.setTag(R.drawable.add_to_cart);
            holder.addToCartBtn.setVisibility(View.VISIBLE);
            holder.addToCartBtn.setBackgroundResource(R.drawable.add_to_cart);

            if (listData.get(position).get_slotStatus() == 0) {
                ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (pickedSportName.equalsIgnoreCase(cartList.get(i).get_cartSportName())
                                && pickedClubName_CLUB.equalsIgnoreCase(cartList.get(i).get_pickedClubName_CLUB())
                                && PickedCourtName.equalsIgnoreCase(cartList.get(i).get_cartCourtName())
                                && PickedDate.equalsIgnoreCase(cartList.get(i).get_cartDate())
                                && holder.singleSlotTimeTV.getText().toString().equalsIgnoreCase(cartList.get(i).get_cartSlotTime())
                                && position == cartList.get(i).get_position()) {
                            holder.addToCartBtn.setTag(R.drawable.remove_from_cart);
                            holder.addToCartBtn.setBackgroundResource(R.drawable.remove_from_cart);
                            break;
                        }
                    }
                }
                holder.singleSlotTimeTV.setTextColor(mContext.getResources().getColor(R.color.slot_available));
            } else {
                holder.balanceContainer.setVisibility(View.GONE);
                holder.addToCartBtn.setVisibility(View.GONE);
                holder.singleSlotTimeTV.setTextColor(mContext.getResources().getColor(R.color.slot_not_available));
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
                    if (listData.get(position).get_slotStatus() == 0) {
                        int pos = (Integer) v.getTag();
                        if (pos == R.drawable.add_to_cart) {
                            boolean isFreeSlot = false;
                            ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                            if (cartList.size() > 0) {
                                for (int i = 0; i < cartList.size(); i++) {
                                    if ((cartList.get(i).get_cartSlotPrice().equalsIgnoreCase("0") || cartList.get(i).get_cartSlotPrice().equalsIgnoreCase("1"))){
                                        if (position == cartList.get(i).get_position() && !(listData.get(position).get_price().equalsIgnoreCase("0")|| listData.get(position).get_price().equalsIgnoreCase("1"))) {
                                            isFreeSlot = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!isFreeSlot){
                                Utility.showCustomToast(getResources().getString(R.string.add_to_cart), mContext);
                                dataBaseHelper.addToCartListItems(new CartDataTable(pickedClubName_CLUB, pickedSportName, PickedCourtName, PickedDate, listData.get(position).get_slot_time_actual(), listData.get(position).get_price(), ""+pickedCourtID, convenienceFee, sclabel, position));
                            } else {
                                Utility.showCustomToast(getResources().getString(R.string.already_added_slot), mContext);
                            }
                        } else {
                            ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                            if (cartList.size() > 0) {
                                for (int i = 0; i < cartList.size(); i++) {
                                    if (pickedSportName.equalsIgnoreCase(cartList.get(i).get_cartSportName())
                                            && pickedClubName_CLUB.equalsIgnoreCase(cartList.get(i).get_pickedClubName_CLUB())
                                            && PickedCourtName.equalsIgnoreCase(cartList.get(i).get_cartCourtName())
                                            && PickedDate.equalsIgnoreCase(cartList.get(i).get_cartDate())
                                            && listData.get(position).get_slot_time_actual().equalsIgnoreCase(cartList.get(i).get_cartSlotTime())
                                            && position == cartList.get(i).get_position()) {
                                        dataBaseHelper.deleteCartItem(cartList.get(i).get_cartDate(), cartList.get(i).get_cartSlotTime());
                                    }
                                }
                            }
                            Utility.showCustomToast(getResources().getString(R.string.removed_cart), mContext);
                        }
                        ArrayList<CartDataTable> cartList1 = dataBaseHelper.getCartDataTableList();
                        if (cartList1.size() > 0) {
                            balanceContainerHeader.setVisibility(View.VISIBLE);
                            amountTextHeaderTV.setVisibility(View.VISIBLE);
                            amountTextHeaderTV.setTextColor(mContext.getResources().getColor(R.color.white));
                            amountTextHeaderTV.setBackgroundResource(R.drawable.amount_background_cart);
                            amountTextHeaderTV.setText("" + cartList1.size());
                        } else {
                            balanceContainerHeader.setVisibility(View.VISIBLE);
                            amountTextHeaderTV.setVisibility(View.GONE);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.sorry_slot_unavail), mContext);
                    }
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