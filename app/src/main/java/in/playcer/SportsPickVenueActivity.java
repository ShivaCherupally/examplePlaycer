package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import in.playcer.adapters.SportVenueListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.CartDataTable;
import in.playcer.model.SportAllSlotsListData;
import in.playcer.model.SportAllSubSlotsListData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SportsPickVenueActivity extends Activity implements AdapterView.OnItemClickListener {
    public SportVenueListAdapter mAdapter;
    public ArrayList<SportAllSlotsListData> mSportAllSlotsListData = new ArrayList<SportAllSlotsListData>();
    public ArrayList<SportAllSubSlotsListData> mSportAllSubSlotsListData = new ArrayList<SportAllSubSlotsListData>();
    ListView sportDetailsLV;
    LinearLayout menuMainLL;
    TextView[] menusBtn;

    String singleSportURL = "";
    String singleSportName = "";
    String pickedClubName = "";
    int pickedClubID = -1;
    String address = "";
    String latLng = "";
    String location = "";
    String city = "";
    String sportSlug = "";
    String number = "";

    int MainSingleSportSlotsListCount = 0;
    int subSingleSportSlotsListCount = 0;
    List<Date> dates;
    private ProgressWheel progressWheel_CENTER;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(this);

    DateFormat formatter;

    String pickedDate = "";

    TextView noSlotsAvailableTV;

    LinearLayout noNetLL;

    View balanceContainerHeader;
    TextView amountTextHeaderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_a_slot_full_slots_listview);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                String role = dataBaseHelper.getRegisteredRole();
                Intent bookingDoneIntent = null;
                if (role != null && role.equalsIgnoreCase("manager")) {
                    bookingDoneIntent = new Intent(SportsPickVenueActivity.this, DashboardActivity.class);
                } else {
                    bookingDoneIntent = new Intent(SportsPickVenueActivity.this, ClubsActivity.class);
                }
                if (bookingDoneIntent != null) {
                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bookingDoneIntent.putExtra("EXIT", true);
                    startActivity(bookingDoneIntent);
                }
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.setDimensions(this);
                    cartItemsInitilization();

                    progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
                    progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
                    progressWheel_CENTER.setRimColor(Color.LTGRAY);

                    menuMainLL = (LinearLayout) findViewById(R.id.menuMainLLID);
                    menuMainLL.getLayoutParams().height = (int) (Utility.screenHeight / 9.0);

                    noSlotsAvailableTV = (TextView) findViewById(R.id.noSlotsAvailableTVID);

                    sportDetailsLV = (ListView) findViewById(R.id.sportDetailsLVID);
                    sportDetailsLV.setOnItemClickListener(SportsPickVenueActivity.this);

                    noNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);

                    singleSportURL = dataBundle.getString(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL);
                    singleSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    sportSlug = dataBundle.getString(Utility.KEY_SLUG);
                    pickedClubName = dataBundle.getString(Utility.KEY_pickedClubName);
                    pickedClubID = dataBundle.getInt(Utility.KEY_pickedClubID);
                    address = dataBundle.getString(Utility.KEY_address);
                    latLng = dataBundle.getString(Utility.KEY_latlng);
                    location = dataBundle.getString(Utility.KEY_location);
                    city = dataBundle.getString(Utility.KEY_city);
                    number = dataBundle.getString(Utility.KEY_phone);

                    dates = new ArrayList<Date>();
                    Date currDate = new Date();
                    DateFormat dateFormat = new SimpleDateFormat("E-dd-MMM-yyyy");
                    String strCurrDate = dateFormat.format(currDate);

                    SimpleDateFormat parser = new SimpleDateFormat("E-dd-MMM-yyyy");
                    Date nullDate = null;
                    try {
                        nullDate = parser.parse(strCurrDate.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)", "$1$2"));
                    } catch (ParseException e3) {
                        // TODO Auto-generated catch block
                        e3.printStackTrace();
                    }
                    SimpleDateFormat formatter1 = new SimpleDateFormat("E-dd-MMM-yyyy");

                    String str_date = formatter1.format(nullDate);
                    Date m = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(m);
                    cal.add(Calendar.DATE, 6);
                    m = cal.getTime();

                    DateFormat dateFormat1 = new SimpleDateFormat("E-dd-MMM-yyyy");
                    String strCurrDate1 = dateFormat1.format(m);
                    SimpleDateFormat parser1 = new SimpleDateFormat("E-dd-MMM-yyyy");
                    Date nullDate1 = null;
                    try {
                        nullDate1 = parser1.parse(strCurrDate1.replaceAll("([\\+\\-]\\d\\d):(\\d\\d)", "$1$2"));
                    } catch (ParseException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                    SimpleDateFormat formatter11 = new SimpleDateFormat("E-dd-MMM-yyyy");

                    String end_date = formatter11.format(nullDate1);
                    formatter = new SimpleDateFormat("E-dd-MMM-yyyy");
                    Date startDate = null;
                    try {
                        startDate = (Date) formatter.parse(str_date);
                    } catch (ParseException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    Date endDate = null;
                    try {
                        endDate = (Date) formatter.parse(end_date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    long interval = 24 * 1000 * 60 * 60;
                    long endTime = endDate.getTime();
                    long curTime = startDate.getTime();
                    while (curTime <= endTime) {
                        dates.add(new Date(curTime));
                        curTime += interval;
                    }

                    String dayOfDate = "";
                    String dayOfNumDate = "";
                    String dayOfMont = "";

                    menusBtn = new TextView[100];
                    for (int i = 0; i < dates.size(); i++) {
                        Date lDate = (Date) dates.get(i);
                        String ds = formatter.format(lDate);
                        System.out.println(" Date is ..." + ds);

                        String[] parts = ds.split("-");
                        dayOfDate = parts[0]; // 004
                        dayOfNumDate = parts[1]; // 034556
                        dayOfMont = parts[2]; // 034556
                        String year = parts[3]; // 034556

                        menusBtn[i] = new TextView(getApplicationContext());
                        menusBtn[i].setText("" + dayOfDate + "\n" + dayOfNumDate + "\n" + dayOfMont + "");
                        menusBtn[i].setId(i);
                        menusBtn[i].setTextColor(Color.WHITE);
                        //menusBtn[i].setSingleLine(true);
                        //menusBtn[i].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                        menusBtn[i].setGravity(Gravity.CENTER_HORIZONTAL);
                        menusBtn[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                for (int i = 0; i < dates.size(); i++) {
                                    if (v.getId() == i) {
                                        menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.menu_selected);
                                        if (sportSlug != null) {
                                            try {
                                                if (mAdapter != null) {
                                                    mAdapter = null;
                                                    sportDetailsLV.setVisibility(View.GONE);
                                                }
                                                noSlotsAvailableTV.setVisibility(View.GONE);
                                                progressWheel_CENTER.setVisibility(View.VISIBLE);
                                                if (Utility.isOnline(SportsPickVenueActivity.this)) {
                                                    loadAllSlots(i);
                                                } else {
                                                    allSlotsTroubleConnecting(i);
                                                }
                                            } catch (Exception e) {
                                                if (e != null) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.menu_selected);
                                    } else {
                                        menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    }
                                }
                            }
                        });

                        if (i == 0) {
                            if (Utility.isOnline(SportsPickVenueActivity.this)) {
                                loadAllSlots(i);
                            } else {
                                allSlotsTroubleConnecting(i);
                            }
                            menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.menu_selected);
                        } else {
                            menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        }
                        menusBtn[i].setWidth((int) Utility.screenWidth / 7);
                        menuMainLL.addView(menusBtn[i]);
                    }
                    setupNavigation();
                }
            }
        }
    }

    private void allSlotsTroubleConnecting(final int i) {
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
                        if (Utility.isOnline(SportsPickVenueActivity.this)) {
                            noNetLL.setVisibility(View.GONE);
                            loadAllSlots(i);
                        } else {
                            progressWheel_CENTER.setVisibility(View.GONE);
                            noNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void loadAllSlots(int i) {
        menusBtn[i].setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.menu_selected);
        if (sportSlug != null) {
            try {
                Date lDate1 = (Date) dates.get(i);
                String ds1 = formatter.format(lDate1);
                System.out.println(" Date is ..." + ds1);

                String[] parts1 = ds1.split("-");
                String dayOfNumDate1 = parts1[1]; // Date
                String dayOfMont1 = parts1[2]; // Month
                String year1 = parts1[3]; // year
                String cookieStr = dataBaseHelper.getRegisteredCookie();
                String cityStr = dataBaseHelper.getCurrentCity();
                String dateStr = "" + dayOfNumDate1 + dayOfMont1 + year1;
                //String paramss = "?city="+cityStr+"&d="+dateStr+"&cookie="+cookieStr;
                pickedDate = "" + dayOfNumDate1 + " " + dayOfMont1.toUpperCase() + ", " + year1;
                //String slotsURL = singleSportURL+paramss;
                RequestParams params = new RequestParams();
                //params.put("city",cityStr);
                params.put("d", dateStr);
                params.put("cookie", cookieStr);
                params.put("sport", sportSlug);
                params.put("id", String.valueOf(pickedClubID));
                getSlotsDetails(AppConstants.GET_SLOTS_URL, params, i);
            } catch (Exception e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void getSlotsDetails(String url, RequestParams params, final int _i) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        noNetLL.setVisibility(View.GONE);
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;
                        /******* Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        jsonResponse = new JSONObject(response);

                        /*****
                         * Returns the value mapped by name if it exists and is a JSONArray.
                         ***/
                        /******* Returns null otherwise. *******/
                        JSONArray jsonMainNode = jsonResponse.getJSONArray("courts");

                        /*********** Process each JSON Node ************/
                        MainSingleSportSlotsListCount = jsonMainNode.length();

                        if (MainSingleSportSlotsListCount != 0) {
                            mSportAllSlotsListData = new ArrayList<SportAllSlotsListData>();

                            for (int i = 0; i < MainSingleSportSlotsListCount; i++) {
                                /****** Get Object for each JSON node. ***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                int all_slot_id = Integer.parseInt(jsonChildNode.optString("id").toString());
                                String all_sport_slot_name = jsonChildNode.optString("name").toString();
                                String all_sport_slot_URL = jsonChildNode.optString("url").toString();

                                String availability = jsonChildNode.optString("availability").toString();
                                String price_interval = jsonChildNode.optString("price_interval").toString();
                                String address = jsonChildNode.optString("address").toString();
                                String latlng = jsonChildNode.optString("latlng").toString();
                                String location = jsonChildNode.optString("location").toString();
                                String city = jsonChildNode.optString("city").toString();

                                JSONArray jsonSubMenu = jsonChildNode.getJSONArray("slots");
                                subSingleSportSlotsListCount = jsonSubMenu.length();

                                //if (subSingleSportSlotsListCount != 0) {
                                mSportAllSubSlotsListData = new ArrayList<SportAllSubSlotsListData>();
                                //}

                                for (int j = 0; j < subSingleSportSlotsListCount; j++) {
                                    /****** Get Object for each JSON node. ***********/
                                    JSONObject jsonChildSub = jsonSubMenu.getJSONObject(j);
                                    int status = Integer.parseInt(jsonChildSub.optString("status").toString());
                                    String slot_time = jsonChildSub.optString("slot").toString();

                                    mSportAllSubSlotsListData.add(new SportAllSubSlotsListData(status, slot_time));
                                }

                                mSportAllSlotsListData.add(new SportAllSlotsListData(all_slot_id, all_sport_slot_name, availability, price_interval, address, location, city, latlng, mSportAllSubSlotsListData, all_sport_slot_URL));

                                OutputData += "Node : \n\n     " + all_slot_id + " | " + all_sport_slot_name + " | " + all_sport_slot_URL + " \n\n ";
                                Log.i("JSON parse", OutputData);
                            }

                            Log.w("Main", "Main Count = " + MainSingleSportSlotsListCount);
                            Log.w("Sub", "Sub Count = " + subSingleSportSlotsListCount);

                            mAdapter = new SportVenueListAdapter(SportsPickVenueActivity.this, mSportAllSlotsListData);
                            sportDetailsLV.setAdapter(mAdapter);
                            sportDetailsLV.setVisibility(View.VISIBLE);
                            noSlotsAvailableTV.setVisibility(View.GONE);
                        } else {
                            noSlotsAvailableTV.setVisibility(View.VISIBLE);
                            sportDetailsLV.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                            //Utility.showCustomToast("Suc JSONException = "+e.getMessage().toString(), SportsPickVenueActivity.this);
                        }
                        allSlotsTroubleConnecting(_i);
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                            //Utility.showCustomToast("Suc Exception = " + e.getMessage().toString(), SportsPickVenueActivity.this);
                        }
                        allSlotsTroubleConnecting(_i);
                    }
                } else {
                    noSlotsAvailableTV.setVisibility(View.VISIBLE);
                    sportDetailsLV.setVisibility(View.GONE);
                }
                progressWheel_CENTER.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                progressWheel_CENTER.setVisibility(View.GONE);
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(SportsPickVenueActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else {
                    allSlotsTroubleConnecting(_i);
                }
            }
        });
    }

    private void cartItemsInitilization() {
        amountTextHeaderTV = (TextView) findViewById(R.id.balance_amount);
        balanceContainerHeader = findViewById(R.id.balance_container);
        balanceContainerHeader.setBackgroundResource(R.drawable.ic_add_shopping_cart_white_48dp);
        balanceContainerHeader.getLayoutParams().width = (int) (Utility.screenHeight / 14.0);
        balanceContainerHeader.getLayoutParams().height = (int) (Utility.screenHeight / 14.0);

        /*
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
        }*/
        cartTotalAmount();

        balanceContainerHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    Intent cartPageTogo = new Intent(SportsPickVenueActivity.this, CartPlaceOrdersListActivity.class);
                    cartPageTogo.putExtra(Utility.KEY_FROM_TO_CART, "FROM_SLOTS_SPORTS");
                    startActivity(cartPageTogo);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), SportsPickVenueActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mSportAllSlotsListData.get(position).get_availability().equalsIgnoreCase("1")) {
            Intent sportIntent = new Intent(SportsPickVenueActivity.this, SingleSportSlotBookingActivity.class);
            sportIntent.putExtra(Utility.KEY_Confirm, "AVAILABLE");
            sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_SINGLE_SLOTS_URL, mSportAllSlotsListData.get(position).get_sportSlotDetailsURL());
            sportIntent.putExtra(Utility.KEY_PickedCourtName, mSportAllSlotsListData.get(position).get_sportCourtName());
            sportIntent.putExtra(Utility.KEY_pickedClubName, pickedClubName);
            sportIntent.putExtra(Utility.KEY_SPORT_NAME, singleSportName);
            sportIntent.putExtra(Utility.KEY_pickedDate, pickedDate);
            sportIntent.putExtra(Utility.KEY_pickedCourtID, mSportAllSlotsListData.get(position).get_sportSlotID());
            sportIntent.putExtra(Utility.KEY_address, mSportAllSlotsListData.get(position).get_address());
            sportIntent.putExtra(Utility.KEY_latlng, mSportAllSlotsListData.get(position).get_latlng());
            sportIntent.putExtra(Utility.KEY_price_interval, mSportAllSlotsListData.get(position).get_price_interval());
            sportIntent.putExtra(Utility.KEY_location, mSportAllSlotsListData.get(position).get_location());
            sportIntent.putExtra(Utility.KEY_city, mSportAllSlotsListData.get(position).get_city());
            sportIntent.putExtra(Utility.KEY_phone, number);
            startActivity(sportIntent);
        } else {
            String role = dataBaseHelper.getRegisteredRole();
            Log.w("role", "" + role);
            if (role != null && role.equalsIgnoreCase("manager")) {
                Intent sportIntent = new Intent(SportsPickVenueActivity.this, SlotConfirmationManagerActivity.class);
                sportIntent.putExtra(Utility.KEY_Confirm, "UNAVAILABLE");
                sportIntent.putExtra(Utility.KEY_PickedCourtName, mSportAllSlotsListData.get(position).get_sportCourtName());
                sportIntent.putExtra(Utility.KEY_pickedClubName, pickedClubName);
                sportIntent.putExtra(Utility.KEY_SPORT_NAME, singleSportName);
                sportIntent.putExtra(Utility.KEY_pickedDate, pickedDate);
                sportIntent.putExtra(Utility.KEY_pickedCourtID, mSportAllSlotsListData.get(position).get_sportSlotID());
                sportIntent.putExtra(Utility.KEY_address, mSportAllSlotsListData.get(position).get_address());
                sportIntent.putExtra(Utility.KEY_latlng, mSportAllSlotsListData.get(position).get_latlng());
                sportIntent.putExtra(Utility.KEY_location, mSportAllSlotsListData.get(position).get_location());
                sportIntent.putExtra(Utility.KEY_city, mSportAllSlotsListData.get(position).get_city());
                startActivity(sportIntent);
            } else {
                Intent sportIntent = new Intent(SportsPickVenueActivity.this, SlotConfirmationUnAvaiActivity.class);
                sportIntent.putExtra(Utility.KEY_Confirm, "UNAVAILABLE");
                sportIntent.putExtra(Utility.KEY_pickedClubName, pickedClubName);
                sportIntent.putExtra(Utility.KEY_SPORT_NAME, singleSportName);
                sportIntent.putExtra(Utility.KEY_pickedDate, "");
                sportIntent.putExtra(Utility.KEY_phone, number);
                sportIntent.putExtra(Utility.KEY_address, address);
                sportIntent.putExtra(Utility.KEY_latlng, latLng);
                sportIntent.putExtra(Utility.KEY_location, location);
                sportIntent.putExtra(Utility.KEY_city, city);
                startActivity(sportIntent);
            }
        }

        MyApplication.getInstance().trackEvent("All Slots & All Courts", "Tapped", "Picked Slots");
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
        // titleTV.setTypeface(Utility.font_bold);
        titleTV.setText("PICK YOUR COURT");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.VISIBLE);
        subTitleTV.setText(singleSportName.toUpperCase());

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

        Button moreBtn = (Button) findViewById(R.id.moreBtnID);
        moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Courts");

        cartTotalAmount();
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