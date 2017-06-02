package in.playcer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrus.sdk.Environment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.libs.PagerSlidingTabStrip;
import in.playcer.model.CartDataTable;
import in.playcer.model.EventsDataTable;
import in.playcer.ui.utils.CitrusFlowManager;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.SharedPreference;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 3/17/2016.
 */
public class ScreenCheckoutActivity extends AppCompatActivity {
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;

    public static TextView cartAmountTVID;

    public static TextView convienceAmountTVID;

    public static TextView payAmountTVID;

    public static TextView couponAmountTVID, couponAmountlTV;

    public static TextView creditsAmountTVID;

    public static RelativeLayout couponCodeRLID;

    public static RelativeLayout creditsRLID, feeLableRL;

    public static View couponDividerVID;

    public static View creditDividerVID;

    public static View viewLisneTVIDneTV;

    Button proccedBtnID;
    public static int cartAmount = 0;
    int convenienceFee;
    String bookingTypeShow = "";
    String credits = "";

    private MyPagerAdapter adapter;

    String sendMultipleCartsData = "";
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(ScreenCheckoutActivity.this);
    ProgressDialog ringProgressDialog;

    String eventID = "";
    String eventDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_checkout_activity);
        Utility.setDimensions(this);
        refreshSharedPreferenceData();

        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                if (getIntent().getBooleanExtra("EXIT", false)) {
                    Intent bookingDoneIntent = new Intent(ScreenCheckoutActivity.this, SingleSportSlotBookingActivity.class);
                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bookingDoneIntent.putExtra("EXIT", true);
                    startActivity(bookingDoneIntent);
                    onBackPressedAnimationByCHK();
                } else {
                    setupNavigation();
                    cartAmountTVID = (TextView) findViewById(R.id.cartAmountTVID);
                    convienceAmountTVID = (TextView) findViewById(R.id.convienceAmountTVID);
                    payAmountTVID = (TextView) findViewById(R.id.payAmountTVID);
                    couponAmountTVID = (TextView) findViewById(R.id.couponAmountTVID);
                    couponAmountlTV = (TextView) findViewById(R.id.couponAmountlTVID);
                    creditsAmountTVID = (TextView) findViewById(R.id.creditsAmountTVID);

                    feeLableRL = (RelativeLayout) findViewById(R.id.feeLableRLID);
                    couponCodeRLID = (RelativeLayout) findViewById(R.id.couponCodeRLID);
                    creditsRLID = (RelativeLayout) findViewById(R.id.creditsRLID);
                    couponDividerVID = findViewById(R.id.couponDividerVID);
                    creditDividerVID = findViewById(R.id.creditDividerVID);
                    viewLisneTVIDneTV = findViewById(R.id.viewLisneTVIDneTVID);

                    tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
                    tabs.setUnderlineHeight(1);
                    tabs.setIndicatorHeight(5);
                    tabs.getLayoutParams().height = (int) (Utility.screenHeight / 11.5);
                    pager = (ViewPager) findViewById(R.id.pager);

                    proccedBtnID = (Button) findViewById(R.id.proccedBtnID);
                    proccedBtnID.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
                    proccedBtnID.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (Utility.isOnline(ScreenCheckoutActivity.this)) {
                                String role = dataBaseHelper.getRegisteredRole();
                                String couponCodeStr = SharedPreference.getPreferences(ScreenCheckoutActivity.this, "couponCode");
                                String couponAmountStr = SharedPreference.getPreferences(ScreenCheckoutActivity.this, "couponAmount");
                                String payAmount = SharedPreference.getPreferences(ScreenCheckoutActivity.this, "PAY_AMOUNT");
                                String convenienceFeestr = SharedPreference.getPreferences(ScreenCheckoutActivity.this, "convenienceFeestr");

                                if (bookingTypeShow != null && bookingTypeShow.equalsIgnoreCase("slots")) {
                                    ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                                    if (cartList.size() > 0) {
                                        try {
                                            JSONObject sendBookingGroup = createBookingGroupInServer(cartList, payAmount, convenienceFeestr, couponCodeStr, couponAmountStr);
                                            sendMultipleCartsData = sendBookingGroup.toString();
                                            Log.w("HARI-->", "" + sendMultipleCartsData);

                                            if (role != null && role.equalsIgnoreCase("manager")) {
                                                Intent confirmScreen = new Intent(ScreenCheckoutActivity.this, SlotConfirmationManagerActivity.class);
                                                confirmScreen.putExtra(Utility.KEY_Confirm, "AVAILABLE");
                                                confirmScreen.putExtra(Utility.KEY_CART_LIST_JSON, sendMultipleCartsData);
                                                startActivity(confirmScreen);
                                            } else {
                                                ringProgressDialog = ProgressDialog.show(ScreenCheckoutActivity.this, "Please wait ...", "Confirming your booking...", true);
                                                ringProgressDialog.setCancelable(true);
                                                String cookieStr = dataBaseHelper.getRegisteredCookie();
                                                RequestParams params = new RequestParams();
                                                params.put("cookie", cookieStr);
                                                params.put("type", "save");
                                                params.put("booking_cart", "" + sendMultipleCartsData);

                                                Log.w("Request Parameters -->", "" + params);
                                                sendConfirmationRequesttoServer(AppConstants.CONFIRMATION_URL, params);
                                                MyApplication.getInstance().trackEvent("Pay Button", "Tapped Pay Button", "Pay Button");
                                            }
                                        } catch (JSONException ee) {
                                            ee.printStackTrace();
                                        } catch (Exception ee) {
                                            ee.printStackTrace();
                                        }
                                    } else {
                                        Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), ScreenCheckoutActivity.this);
                                    }
                                } else {
                                    ArrayList<EventsDataTable> cartEventList = dataBaseHelper.getEventsDataTableList();
                                    if (cartEventList.size() > 0) {
                                        try {
                                            JSONObject sendBookingGroup = createBookingGroupInServerEVENTS(cartEventList, payAmount, convenienceFeestr, eventID, eventDate, couponCodeStr, couponAmountStr);
                                            sendMultipleCartsData = sendBookingGroup.toString();
                                            Log.w("HARI-->", "" + sendMultipleCartsData);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            ringProgressDialog = ProgressDialog.show(ScreenCheckoutActivity.this, "Please wait ...", "Confirming your booking...", true);
                                            ringProgressDialog.setCancelable(true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            String cookieStr = dataBaseHelper.getRegisteredCookie();
                                            RequestParams params = new RequestParams();
                                            params.put("cookie", cookieStr);
                                            params.put("events_cart", "" + sendMultipleCartsData);

                                            Log.w("Request Parameters -->", "" + params);
                                            sendConfirmationRequesttoServerEVENTS(AppConstants.EVENT_BOOKING_URL, params);

                                            MyApplication.getInstance().trackEvent("Pay Button", "Tapped Pay Button", "Pay Button");
                                        } catch (Exception ee) {
                                            ee.printStackTrace();
                                        }
                                    } else {
                                        Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), ScreenCheckoutActivity.this);
                                    }
                                }
                            } else {
                                Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), ScreenCheckoutActivity.this);
                            }
                        }
                    });

                    // Cart Amount
                    cartAmount = dataBundle.getInt("CART_AMOUNT");

                    // Tax or other
                    convenienceFee = dataBundle.getInt("CONVENIENCE_FEE");

                    // Booking type
                    bookingTypeShow = dataBundle.getString("BOOKING_TYPE");

                    // Playcer Credits
                    credits = dataBundle.getString("CREDITS");

                    // Event ID from Events only
                    eventID = dataBundle.getString("EVENT_ID");

                    // Event Date from Events only
                    eventDate = dataBundle.getString("EVENT_DATE");

                    //23th
                    String cartAmountstr = String.valueOf(cartAmount);
                    String convenienceFeestr = String.valueOf(convenienceFee);
                    int orginalAmount = cartAmount + convenienceFee;
                    String payAmountstr = String.valueOf(orginalAmount);

                    cartAmountTVID.setText(" \u20B9 " + cartAmountstr);
                    convienceAmountTVID.setText(" +" + " \u20B9 " + convenienceFeestr);
                    payAmountTVID.setText(" \u20B9 " + payAmountstr);

                    if(convenienceFee!=0){
                        feeLableRL.setVisibility(View.VISIBLE);
                        viewLisneTVIDneTV.setVisibility(View.VISIBLE);
                    } else{
                        feeLableRL.setVisibility(View.GONE);
                        viewLisneTVIDneTV.setVisibility(View.GONE);
                    }

                    SharedPreference.setPreference(getApplicationContext(), "cartAmountstr", cartAmountstr);
                    SharedPreference.setPreference(getApplicationContext(), "convenienceFeestr", convenienceFeestr);
                    SharedPreference.setPreference(getApplicationContext(), "PAY_AMOUNT", "" + payAmountstr);
                    SharedPreference.setPreference(getApplicationContext(), "bookingTypeShow", bookingTypeShow);
                    //SharedPreference.setPreference(getApplicationContext(), "couponCodeAmount", couponCodeAmount);
                    SharedPreference.setPreference(getApplicationContext(), "credits", credits);
                }
            }
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // nothing
                Log.v("HARI--> ", "" + arg0);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v("HARI--> ", "Scrolled Pos->" + position);
            }

            @Override
            public void onPageSelected(int position) {
                Log.v("HARI-->", "Selected Pos->" + position);
            }
        });

        ArrayList<String> tabNames = new ArrayList<String>();
        tabNames.add("PLAYCER CREDITS");
        tabNames.add("APPLY COUPON");

        adapter = new MyPagerAdapter(getSupportFragmentManager(), tabNames);
        pager.setAdapter(adapter);
        tabs.setViewPager(pager);
        tabs.setVisibility(View.VISIBLE);
    }

    private void refreshSharedPreferenceData() {
        cartAmount = 0;
        SharedPreference.setPreference(getApplicationContext(), "couponBalance", "");
        SharedPreference.setPreference(getApplicationContext(), "checkedValue", "");
        SharedPreference.setPreference(getApplicationContext(), "couponCode", "");
        SharedPreference.setPreference(getApplicationContext(), "couponAmount", "");
        SharedPreference.setPreference(getApplicationContext(), "cartAmountstr", "");
        SharedPreference.setPreference(getApplicationContext(), "convenienceFeestr", "");
        SharedPreference.setPreference(getApplicationContext(), "bookingTypeShow", "");
        SharedPreference.setPreference(getApplicationContext(), "AfterCreditsApplied", "");
        //SharedPreference.setPreference(getApplicationContext(), "couponCodeAmount", "");
        SharedPreference.setPreference(getApplicationContext(), "credits", "");
    }

    private JSONObject createBookingGroupInServer(ArrayList<CartDataTable> cartList, String subTotal, String _fee, String _couponCode, String _couponAmount) throws JSONException {
        JSONObject jResult = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (int i = 0; i < cartList.size(); i++) {
            //if (mDBHelper.getSyncingOrNot(cartList.get(i).get_Identifier()).equals("NO")) {
            JSONObject jGroup = new JSONObject();
            jGroup.put("courtID", cartList.get(i).get_cartPickedCourtID());
            jGroup.put("slot", cartList.get(i).get_cartSlotTime());
            jGroup.put("date", cartList.get(i).get_cartDate());
            jGroup.put("price", cartList.get(i).get_cartSlotPrice());
            // etc

            jArray.put(jGroup);
        }

        jResult.put("booking", jArray);
        jResult.putOpt("total", "" + subTotal);
        jResult.putOpt("extra", "" + _fee);
        jResult.putOpt("coupon_code", "" + _couponCode);
        jResult.putOpt("coupon_amount", "" + _couponAmount);
        return jResult;
    }

    public void setupNavigation() {
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);
        dataBaseHelper = new AppDataBaseHelper(getApplication());
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
        titleTV.setText("CHECKOUT");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);

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

    private void onBackPressedAnimationByCHK() {
        refreshSharedPreferenceData();
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<String> mTabsTitlesList;

        public MyPagerAdapter(FragmentManager fm, ArrayList<String> _mTabsTitlesList) {
            super(fm);
            this.mTabsTitlesList = _mTabsTitlesList;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (mTabsTitlesList != null) {
                if (mTabsTitlesList.size() > 0) {
                    return mTabsTitlesList.get(position);
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTabsTitlesList.size();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return PlaycerCreditsFragment.newInstance(cartAmount, convenienceFee);
            } else {
                return CouponCodeFragment.newInstance(cartAmount, convenienceFee, bookingTypeShow);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(ScreenCheckoutActivity.this).reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(ScreenCheckoutActivity.this).reportActivityStop(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void sendConfirmationRequesttoServer(String confirmationURL, RequestParams _params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(confirmationURL, _params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status").toString();

                        if (status.equalsIgnoreCase("ok")) {
                            int booking_id = Integer.parseInt(jsonResponse.getString("booking_id").toString());
                            Utility.ORDER_ID = "" + booking_id + "";
                            setupCitrusConfigs(booking_id);
                            String formatDate = Utility.PickedDate.replace(" ", "");
                            Utility.bookingDate = formatDate.replace(",", "");
                            Utility.bookingPickedSlotTime = Utility.PickedSlotTime;
                            Utility.bookingCourtID = String.valueOf(Utility.pickedCourtID);
                            CitrusFlowManager.startShoppingFlowStyle(ScreenCheckoutActivity.this, "" + dataBaseHelper.getRegisteredEmailID(), "" + dataBaseHelper.getRegisteredMobileNumber(), "" + SharedPreference.getPreferences(ScreenCheckoutActivity.this, "PAY_AMOUNT"), R.style.MyMaterialTheme, ScreenCheckoutActivity.this, "" + bookingTypeShow);
                        } else if (status.contains("Slot is unavailable")) {
                            Intent confirmScreen = new Intent(ScreenCheckoutActivity.this, SuccessfullySlotConfirmedActivity.class);
                            confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                            startActivity(confirmScreen);
                            dataBaseHelper.deleteCart();
                            finish();
                        }
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                            ringProgressDialog = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ringProgressDialog != null) {
                        ringProgressDialog.dismiss();
                        ringProgressDialog = null;
                    }
                    Utility.showCustomToast(getResources().getString(R.string.request_failed), ScreenCheckoutActivity.this);
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog = null;
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog = null;
                }
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ScreenCheckoutActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else if (content != null && content.contains("Slot is unavailable")) {
                    Intent confirmScreen = new Intent(ScreenCheckoutActivity.this, SuccessfullySlotConfirmedActivity.class);
                    confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                    startActivity(confirmScreen);
                    finish();
                } else {
                    if (statusCode == 404) {
                        Utility.showCustomToast(getResources().getString(R.string.request_not_found), ScreenCheckoutActivity.this);
                    } else if (statusCode == 500) {
                        Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), ScreenCheckoutActivity.this);
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.unexpected_error), ScreenCheckoutActivity.this);
                    }
                }
            }
        });
    }

    private void setupCitrusConfigs(int orderID) {
        // Sandbox Testing Company
        /*CitrusFlowManager.initCitrusConfig("kl6eb8au9r-signup", "3c8d0ac0839cecba3972dcbf3bcb3e69", "kl6eb8au9r-signin",
                "b3b416a6e0ce6c0a910f636d3e6cc340", getResources().getColor(R.color.white),
                CartPlaceOrdersListActivity.this, Environment.SANDBOX, "tarani", AppConstants.PAYMENT_GATEWAY_BILL_GENERATOR_URL + "orderId=" + orderID,
                AppConstants.PAYMENT_GATEWAY_RETURN_URL);*/

        //Production Citrus Details Company
        /*CitrusFlowManager.initCitrusConfig("7jeb45fj85-signup", "d464a8e802a7ae6987754a0acb6c8480", "7jeb45fj85-signin",
                "4cb8a934f9a9af772ed6f3476a350525", getResources().getColor(R.color.white),
                ScreenCheckoutActivity.this, Environment.PRODUCTION, "tarani", AppConstants.PAYMENT_GATEWAY_BILL_GENERATOR_URL + "orderId=" + orderID,
                AppConstants.PAYMENT_GATEWAY_RETURN_URL);*/

        // Sandbox Testing Client
        /*CitrusFlowManager.initCitrusConfig("e4mi8a7ayk-signup", "e22ef611e307e041e07a7f60bded975e", "e4mi8a7ayk-signin",
                "f3add71b4d0b24fe0904ef464a3726f9", getResources().getColor(R.color.white),
                ScreenCheckoutActivity.this, Environment.SANDBOX, "e4mi8a7ayk", AppConstants.PAYMENT_GATEWAY_BILL_GENERATOR_URL + "orderId=" + orderID,
                AppConstants.PAYMENT_GATEWAY_RETURN_URL);*/

        // Production Citrus Client
        CitrusFlowManager.initCitrusConfig("0zuyh6j5fn-signup", "15cf2d40ea573c74f8233ae8a69f3c40", "0zuyh6j5fn-signin",
                "36f396859f8e6e31218a1cb6f46d04a8", getResources().getColor(R.color.white),
                ScreenCheckoutActivity.this, Environment.PRODUCTION, "0zuyh6j5fn", AppConstants.PAYMENT_GATEWAY_BILL_GENERATOR_URL + "orderId=" + orderID,
                AppConstants.PAYMENT_GATEWAY_RETURN_URL);
    }

    private void sendConfirmationRequesttoServerEVENTS(String confirmationURL, RequestParams _params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(confirmationURL, _params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status").toString();

                        if (status.equalsIgnoreCase("ok")) {
                            int booking_id = Integer.parseInt(jsonResponse.getString("booking_id").toString());
                            Utility.ORDER_ID = "" + booking_id + "";
                            setupCitrusConfigs(booking_id);
                            CitrusFlowManager.startShoppingFlowStyle(ScreenCheckoutActivity.this, "" + dataBaseHelper.getRegisteredEmailID(), "" + dataBaseHelper.getRegisteredMobileNumber(), "" + SharedPreference.getPreferences(ScreenCheckoutActivity.this, "PAY_AMOUNT"), R.style.MyMaterialTheme, ScreenCheckoutActivity.this, "FROM_EVENTS");
                        } else if (status.contains("Slot is unavailable")) {
                            Intent confirmScreen = new Intent(ScreenCheckoutActivity.this, SuccessfullySlotConfirmedActivity.class);
                            confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                            startActivity(confirmScreen);
                            dataBaseHelper.deleteCart();
                            finish();
                        }
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                            ringProgressDialog = null;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ringProgressDialog != null) {
                        ringProgressDialog.dismiss();
                        ringProgressDialog = null;
                    }
                    Utility.showCustomToast(getResources().getString(R.string.request_failed), ScreenCheckoutActivity.this);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog = null;
                }
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ScreenCheckoutActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    finish();
                } else if (content != null && content.contains("Slot is unavailable")) {
                    Intent confirmScreen = new Intent(ScreenCheckoutActivity.this, SuccessfullySlotConfirmedActivity.class);
                    confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                    startActivity(confirmScreen);
                    finish();
                } else {
                    if (statusCode == 404) {
                        Utility.showCustomToast(getResources().getString(R.string.request_not_found), ScreenCheckoutActivity.this);
                    } else if (statusCode == 500) {
                        Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), ScreenCheckoutActivity.this);
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.unexpected_error), ScreenCheckoutActivity.this);
                    }
                }
            }
        });
    }

    private JSONObject createBookingGroupInServerEVENTS(ArrayList<EventsDataTable> cartList, String subTotal, String _fee, String eventID, String eventDate, String _couponCode, String _couponAmount) throws JSONException {
        JSONObject jResult = new JSONObject();
        jResult.putOpt("status", "OK");
        jResult.putOpt("event_id", "" + eventID);
        jResult.putOpt("event_date", "" + eventDate);

        JSONArray jArray = new JSONArray();

        for (int i = 0; i < cartList.size(); i++) {
            //if (mDBHelper.getSyncingOrNot(cartList.get(i).get_Identifier()).equals("NO")) {
            JSONObject jGroup = new JSONObject();
            jGroup.put("ticket_id", cartList.get(i).get_eventID());
            jGroup.put("ticket_price", cartList.get(i).get_eventPrice());
            // etc

            jArray.put(jGroup);
        }

        jResult.put("tickets", jArray);
        jResult.putOpt("total", "" + subTotal);
        jResult.putOpt("extra", "" + _fee);
        jResult.putOpt("coupon_code", "" + _couponCode);
        jResult.putOpt("coupon_amount", "" + _couponAmount);
        return jResult;
    }
}
