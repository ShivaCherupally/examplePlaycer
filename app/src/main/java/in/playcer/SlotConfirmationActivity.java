package in.playcer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrus.sdk.Environment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.model.CartDataTable;
import in.playcer.ui.utils.CitrusFlowManager;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SlotConfirmationActivity extends FragmentActivity {
    GoogleMap mGoogleMap;
    ProgressDialog ringProgressDialog;

    //private TextView amountText;
    //public LinearLayout balanceContainer;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(SlotConfirmationActivity.this);

    String payAmount = "0";

    View balanceContainerHeader;
    TextView amountTextHeaderTV;
    Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slot_booking_confirm);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(SlotConfirmationActivity.this, SingleSportSlotBookingActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.setDimensions(this);
                    cartItemsInitilization();

                    //amountText = (TextView) findViewById(R.id.balance_amount);
                    //amountText.setBackgroundResource(R.drawable.amount_background);
                    //balanceContainer = (LinearLayout) findViewById(R.id.balance_container);

                    TextView pickedCourtTV = (TextView) findViewById(R.id.clubNameTVID);
                    TextView sportNameTV = (TextView) findViewById(R.id.sportNameTVID);
                    TextView pickeddateTV = (TextView) findViewById(R.id.pickeddateTVID);
                    TextView PickedSlotTimeTV = (TextView) findViewById(R.id.PickedSlotTimeTVID);
                    TextView addressTVID = (TextView) findViewById(R.id.fullAddressTVID);

                    TextView locationTV = (TextView) findViewById(R.id.locationTVID);
                    TextView cityTV = (TextView) findViewById(R.id.cityTVID);
                    TextView titleTV = (TextView) findViewById(R.id.titleTVID);

                    TextView slotPriceAmountLabelTV = (TextView) findViewById(R.id.slotPriceAmountLabelTVID);
                    TextView slotPriceAmountTV = (TextView) findViewById(R.id.slotPriceAmountTVID);
                    TextView feeAmountLabelTV = (TextView) findViewById(R.id.feeAmountLabelTVID);
                    TextView feeAmountTV = (TextView) findViewById(R.id.feeAmountTVID);
                    //TextView totalAmountLabelTV = (TextView) findViewById(R.id.totalAmountLabelTVID);
                    TextView totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

                    RelativeLayout feeLableRL = (RelativeLayout) findViewById(R.id.feeLableRLID);
                    RelativeLayout totalLableRL = (RelativeLayout) findViewById(R.id.totalLableRLID);
                    View viewLineTV = (View) findViewById(R.id.viewLineTVID);

                    Button callBtn = (Button) findViewById(R.id.callBtnID);
                    callBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
                    callBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
                    callBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + Utility.number));
                            startActivity(callIntent);
                        }
                    });

                    Button confirmBtn = (Button) findViewById(R.id.confirmBtnID);
                    confirmBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
                    confirmBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                           /* dataBaseHelper.addToCartListItems(new CartDataTable("", Utility.pickedSportName, Utility.PickedCourtName, Utility.PickedDate, Utility.PickedDate, Utility.PickedDate, Utility.PickedDate));
                            ArrayList<CartDataTable> cartList1 = dataBaseHelper.getCartDataTableList();
                            if (cartList1.size() > 0) {
                                balanceContainerHeader.setVisibility(View.VISIBLE);
                                amountTextHeaderTV.setVisibility(View.VISIBLE);
                                amountTextHeaderTV.setTextColor(getResources().getColor(R.color.white));
                                amountTextHeaderTV.setBackgroundResource(R.drawable.amount_background_cart);
                                amountTextHeaderTV.setText("" + cartList1.size());
                            } else {
                                balanceContainerHeader.setVisibility(View.VISIBLE);
                                amountTextHeaderTV.setVisibility(View.GONE);
                            }
                            */

                            Utility.showCustomToast(getResources().getString(R.string.under_constrution), SlotConfirmationActivity.this);
                            /*if (Utility.isOnline(SlotConfirmationActivity.this)) {
                                try {
                                    ringProgressDialog = ProgressDialog.show(SlotConfirmationActivity.this, "Please wait ...", "Confirming your booking...", true);
                                    ringProgressDialog.setCancelable(true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String cookieStr = dataBaseHelper.getRegisteredCookie();
                                String formatDate = Utility.PickedDate.replace(" ", "");
                                formatDate = formatDate.replace(",", "");
                                RequestParams params = new RequestParams();
                                params.put("court_id", String.valueOf(Utility.pickedCourtID));
                                params.put("cookie", cookieStr);
                                params.put("slot", Utility.PickedSlotTime);
                                params.put("date", formatDate);
                                params.put("type", "save");

                                Log.w("Request Parameters -->", "" + params);
                                sendConfirmationRequesttoServer(AppConstants.CONFIRMATION_URL, params);

                                MyApplication.getInstance().trackEvent("Pay Button", "Tapped Pay Button", "Pay Button");
                            } else {
                                Utility.showCustomToast("Please connect your internet!", SlotConfirmationActivity.this);
                            }*/
                        }
                    });

                    Utility.VALUE_POSITION = dataBundle.getInt(Utility.KEY_pickedPosition);
                    Utility.availability = dataBundle.getString(Utility.KEY_Confirm);
                    Utility.PickedClubName = dataBundle.getString(Utility.KEY_pickedClubName);
                    Utility.PickedCourtName = dataBundle.getString(Utility.KEY_PickedCourtName);
                    Utility.pickedSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    Utility.PickedDate = dataBundle.getString(Utility.KEY_pickedDate);
                    Utility.address = dataBundle.getString(Utility.KEY_address);
                    Utility.latLng = dataBundle.getString(Utility.KEY_latlng);
                    Utility.location = dataBundle.getString(Utility.KEY_location);
                    Utility.city = dataBundle.getString(Utility.KEY_city);
                    Utility.number = dataBundle.getString(Utility.KEY_phone);
                    Utility.slotPrice = dataBundle.getString(Utility.KEY_price_interval);
                    Utility.VALUE_convenience = dataBundle.getString(Utility.KEY_convenience);
                    Utility.VALUE_sclabel = dataBundle.getString(Utility.KEY_sclabel);

                    int price = Integer.parseInt(Utility.slotPrice);
                    int fee = 0;
                    try {
                        fee = Integer.parseInt(Utility.VALUE_convenience);
                    } catch (NumberFormatException ee) {
                        ee.printStackTrace();
                    }

                    int total = price + fee;
                    payAmount = "" + total + "";

                    slotPriceAmountTV.setText("" + getString(R.string.rs) + "" + price);
                    slotPriceAmountLabelTV.setText("PRICE ");

                    feeAmountTV.setText("+ " + getString(R.string.rs) + "" + fee);
                    feeAmountLabelTV.setText("" + Utility.VALUE_sclabel.toUpperCase());

                    totalAmountTV.setText("" + getString(R.string.rs) + "" + total);
                    if (fee <= 0) {
                        feeLableRL.setVisibility(View.GONE);
                        totalLableRL.setVisibility(View.GONE);
                        viewLineTV.setVisibility(View.GONE);
                        totalAmountTV.setText("" + getString(R.string.rs) + "" + price);
                    }

                    if (Utility.availability.equalsIgnoreCase("UNAVAILABLE")) {
                        confirmBtn.setVisibility(View.GONE);
                        PickedSlotTimeTV.setVisibility(View.GONE);
                        titleTV.setText("LOCATION DETAILS");
                        //balanceContainer.setVisibility(View.GONE);
                    } else {
                        // balanceContainer.setVisibility(View.VISIBLE);
                        confirmBtn.setVisibility(View.VISIBLE);
                        PickedSlotTimeTV.setVisibility(View.VISIBLE);
                        Utility.pickedCourtID = dataBundle.getInt(Utility.KEY_pickedCourtID);
                        Utility.PickedSlotTime = dataBundle.getString(Utility.KEY_PickedSlotTime);
                        PickedSlotTimeTV.setText("" + Html.fromHtml(Utility.PickedSlotTime));
                        titleTV.setText("CONFIRM");
                        //showAmountByHeader(payAmount);

                        ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                        if (cartList.size() > 0) {
                            for (int i = 0; i < cartList.size(); i++) {
                                if (Utility.pickedSportName.equalsIgnoreCase(cartList.get(i).get_cartSportName())
                                        && Utility.PickedClubName.equalsIgnoreCase(cartList.get(i).get_pickedClubName_CLUB())
                                        && Utility.PickedCourtName.equalsIgnoreCase(cartList.get(i).get_cartCourtName())
                                        && Utility.PickedDate.equalsIgnoreCase(cartList.get(i).get_cartDate())
                                        && Utility.PickedSlotTime.equalsIgnoreCase(cartList.get(i).get_cartSlotTime())
                                        && Utility.VALUE_POSITION == cartList.get(i).get_position()) {
                                    confirmBtn.setVisibility(View.GONE);
                                    break;
                                }
                            }
                        }
                    }

                    if (Utility.PickedCourtName != null) {
                        pickedCourtTV.setText("" + Html.fromHtml(Utility.PickedCourtName));
                    } else {
                        pickedCourtTV.setVisibility(View.GONE);
                    }

                    if (Utility.number != null && Utility.number.trim().length() > 3) {
                        callBtn.setVisibility(View.VISIBLE);
                    } else {
                        callBtn.setVisibility(View.GONE);
                    }

                    sportNameTV.setText("" + Html.fromHtml(Utility.pickedSportName));
                    pickeddateTV.setText("" + Html.fromHtml(Utility.PickedDate));
                    addressTVID.setText("" + Html.fromHtml(Utility.address));
                    locationTV.setText("" + Html.fromHtml(Utility.location));
                    cityTV.setText("" + Html.fromHtml(Utility.city));

                    if (Utility.latLng != null) {
                        if (Utility.latLng.trim().length() > 0) {
                            Utility.latLng = Utility.latLng.replace(" ", "");
                            String[] parts1 = Utility.latLng.split(",");
                            String lat = parts1[0];
                            String lng = parts1[1];
                            double latL = Double.parseDouble(lat);
                            double lngL = Double.parseDouble(lng);
                            setUpMap(latL, lngL);
                        }
                    }
                    setupNavigation();
                }
            }
        }
    }

    private void cartItemsInitilization() {
        ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
        amountTextHeaderTV = (TextView) findViewById(R.id.balance_amount);
        balanceContainerHeader = findViewById(R.id.balance_container);
        balanceContainerHeader.setBackgroundResource(R.drawable.ic_add_shopping_cart_white_48dp);
        balanceContainerHeader.getLayoutParams().width = (int) (Utility.screenHeight / 14.0);
        balanceContainerHeader.getLayoutParams().height = (int) (Utility.screenHeight / 14.0);

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

        balanceContainerHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    Intent cartPageTogo = new Intent(SlotConfirmationActivity.this, CartPlaceOrdersListActivity.class);
                    cartPageTogo.putExtra(Utility.KEY_FROM_TO_CART, "FROM_SLOTS_CONFIRM");
                    startActivity(cartPageTogo);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), SlotConfirmationActivity.this);
                }
            }
        });
    }

    /*private void showAmountByHeader(String amount) {
        amountText.setVisibility(View.VISIBLE);
        balanceContainer.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(amount)) {
            if (amount.equals("0")) {
                amountText.setVisibility(View.GONE);
                balanceContainer.setVisibility(View.GONE);
            } else {
                amountText.setText(getString(R.string.rs) + " " + amount);
            }
        } else {
            if (!TextUtils.isEmpty(payAmount)) {
                if (payAmount.equals("0")) {
                    amountText.setVisibility(View.GONE);
                    balanceContainer.setVisibility(View.GONE);
                } else {
                    amountText.setText(getString(R.string.rs) + " " + payAmount);
                }
            } else {
                amountText.setVisibility(View.GONE);
                balanceContainer.setVisibility(View.GONE);
            }
        }
    }*/

    private void setupCitrusConfigs(int orderID) {
        // Sandbox Testing
        CitrusFlowManager.initCitrusConfig("7jeb45fj85-signup", "d464a8e802a7ae6987754a0acb6c8480", "7jeb45fj85-signin",
                "4cb8a934f9a9af772ed6f3476a350525", getResources().getColor(R.color.white),
                SlotConfirmationActivity.this, Environment.PRODUCTION, "tarani", AppConstants.PAYMENT_GATEWAY_BILL_GENERATOR_URL + "orderId=" + orderID,
                AppConstants.PAYMENT_GATEWAY_RETURN_URL);
    }

    private void setUpMap(double _lat, double _lng) {
        try {
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) SlotConfirmationActivity.this.getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            mGoogleMap = fm.getMap();

            // Enabling MyLocation in Google Map
            mGoogleMap.setMyLocationEnabled(false);

            gotoCurrentPlacePin(12, _lat, _lng);

            LatLng latLng1 = new LatLng(_lat, _lng);
            MarkerOptions entryMarkerOpt = new MarkerOptions().position(latLng1).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_book));
            mGoogleMap.addMarker(entryMarkerOpt);
            mGoogleMap.getUiSettings().setCompassEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoCurrentPlacePin(float zoom, double currentMapLati, double currentMapLongi) {
        LatLng latLong = new LatLng(currentMapLati, currentMapLongi);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(zoom).tilt(20).build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
                            CitrusFlowManager.startShoppingFlowStyle(SlotConfirmationActivity.this, "" + dataBaseHelper.getRegisteredEmailID(), "" + dataBaseHelper.getRegisteredMobileNumber(), payAmount, R.style.MyMaterialTheme, SlotConfirmationActivity.this, "FROM_SLOTS");
                        } else if (status.equalsIgnoreCase("Court is not available")) {
                            Intent confirmScreen = new Intent(SlotConfirmationActivity.this, SuccessfullySlotConfirmedActivity.class);
                            confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                            startActivity(confirmScreen);
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
                    Utility.showCustomToast(getResources().getString(R.string.request_failed), SlotConfirmationActivity.this);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(SlotConfirmationActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else {
                    if (statusCode == 404) {
                        Utility.showCustomToast(getResources().getString(R.string.request_not_found), SlotConfirmationActivity.this);
                    } else if (statusCode == 500) {
                        Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), SlotConfirmationActivity.this);
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.unexpected_error), SlotConfirmationActivity.this);
                    }
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
                onBackPressedAnimationByCHK();
            }
        });

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

        Button moreBtn = (Button) findViewById(R.id.moreBtnID);
        moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Pay");
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