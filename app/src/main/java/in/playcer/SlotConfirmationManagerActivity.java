package in.playcer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SlotConfirmationManagerActivity extends FragmentActivity {
    /*String PickedClubName = "";
    String PickedCourtName = "";
    String pickedSportName = "";
    String PickedDate = "";
    String PickedSlotTime = "";
    String location = "";
    String city = "";
    int pickedCourtID = -1;*/

    private EditText unameET;
    private EditText eemailET;
    private EditText phoneNumET;

    ProgressDialog ringProgressDialog;

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(this);

    String nameStr = "";
    String emailStr = "";

    String sendMultipleCartsData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slot_booking_confirm_manager);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(SlotConfirmationManagerActivity.this, CartPlaceOrdersListActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.setDimensions(this);
                    unameET = (EditText) findViewById(R.id.unameETID);
                    unameET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
                    unameET.setHintTextColor(getResources().getColor(R.color.grey));

                    eemailET = (EditText) findViewById(R.id.eemailETID);
                    eemailET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
                    eemailET.setHintTextColor(getResources().getColor(R.color.grey));

                    phoneNumET = (EditText) findViewById(R.id.phoneNumETID);
                    phoneNumET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
                    phoneNumET.setHintTextColor(getResources().getColor(R.color.grey));

                    TextView pickedCourtTV = (TextView) findViewById(R.id.clubNameTVID);
                    TextView sportNameTV = (TextView) findViewById(R.id.sportNameTVID);
                    TextView pickeddateTV = (TextView) findViewById(R.id.pickeddateTVID);
                    TextView PickedSlotTimeTV = (TextView) findViewById(R.id.PickedSlotTimeTVID);

                    TextView locationTV = (TextView) findViewById(R.id.locationTVID);
                    TextView cityTV = (TextView) findViewById(R.id.cityTVID);

                    TextView titleTV = (TextView) findViewById(R.id.titleTVID);

                    Button confirmBtn = (Button) findViewById(R.id.confirmBtnID);
                    confirmBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

                    String availability = dataBundle.getString(Utility.KEY_Confirm);
                    sendMultipleCartsData = dataBundle.getString(Utility.KEY_CART_LIST_JSON);
                   /* PickedCourtName = dataBundle.getString(Utility.KEY_PickedCourtName);
                    PickedClubName = dataBundle.getString(Utility.KEY_pickedClubName);
                    pickedSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    PickedDate = dataBundle.getString(Utility.KEY_pickedDate);
                    location = dataBundle.getString(Utility.KEY_location);
                    city = dataBundle.getString(Utility.KEY_city);*/

                    if (availability != null && availability.equalsIgnoreCase("UNAVAILABLE")) {
                        confirmBtn.setVisibility(View.GONE);
                        unameET.setVisibility(View.GONE);
                        eemailET.setVisibility(View.GONE);
                        phoneNumET.setVisibility(View.GONE);
                        PickedSlotTimeTV.setVisibility(View.GONE);
                        titleTV.setText("UNAVAILABLE");
                    } else {
                        confirmBtn.setVisibility(View.VISIBLE);
                        PickedSlotTimeTV.setVisibility(View.VISIBLE);
                        //pickedCourtID = dataBundle.getInt(Utility.KEY_pickedCourtID);
                        //PickedSlotTime = dataBundle.getString(Utility.KEY_PickedSlotTime);
                        //PickedSlotTimeTV.setText("" + Html.fromHtml(PickedSlotTime));
                        titleTV.setText("CONFIRM");
                    }

                    //pickedCourtTV.setText("" + Html.fromHtml(PickedCourtName));
                    //sportNameTV.setText("" + Html.fromHtml(pickedSportName));
                    //pickeddateTV.setText("" + Html.fromHtml(PickedDate));
                    // locationTV.setText("" + Html.fromHtml(location));
                    //cityTV.setText("" + Html.fromHtml(city));

                    confirmBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            nameStr = unameET.getText().toString();
                            emailStr = eemailET.getText().toString();
                            String phoneStr = phoneNumET.getText().toString();

                            boolean isNetAvailable = Utility.isOnline(SlotConfirmationManagerActivity.this);
                            if (isNetAvailable) {
                                if (phoneStr.trim().length() > 0) {
                                    if (phoneStr.length() >= 10) {
                                        try {
                                            ringProgressDialog = ProgressDialog.show(SlotConfirmationManagerActivity.this, "Please wait ...", "Confirming your booking...", true);
                                            ringProgressDialog.setCancelable(true);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        /*String cookieStr = dataBaseHelper.getRegisteredCookie();
                                        String formatDate = PickedDate.replace(" ", "");
                                        formatDate = formatDate.replace(",", "");
                                        RequestParams params = new RequestParams();
                                        params.put("court_id", String.valueOf(pickedCourtID));
                                        params.put("cookie", cookieStr);
                                        params.put("slot", PickedSlotTime);
                                        params.put("date", formatDate);
                                        params.put("type", "save");
                                        params.put("name", nameStr);
                                        params.put("email", emailStr);
                                        params.put("phone", phoneStr);

                                        Log.w("Request Parameters -->", "" + params);

                                        sendConfirmationRequesttoServer(AppConstants.CONFIRMATION_URL, params);*/

                                        String cookieStr = dataBaseHelper.getRegisteredCookie();
                                        RequestParams params = new RequestParams();
                                        params.put("cookie", cookieStr);
                                        params.put("type", "save");
                                        params.put("name", nameStr);
                                        params.put("email", emailStr);
                                        params.put("phone", phoneStr);
                                        params.put("booking_cart", "" + sendMultipleCartsData);

                                        Log.w("Request Parameters -->", "" + params);
                                        sendConfirmationRequesttoServer(AppConstants.CONFIRMATION_URL, params);

                                        MyApplication.getInstance().trackEvent("Slot Confirmation", "Tapped", "Slot Paying");
                                    } else {
                                        Utility.showCustomToast(getResources().getString(R.string.invalid_no), SlotConfirmationManagerActivity.this);
                                    }
                                } else {
                                    Utility.showCustomToast(getResources().getString(R.string.monile_no_mandatory), SlotConfirmationManagerActivity.this);
                                }
                            } else {
                                Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), SlotConfirmationManagerActivity.this);
                            }
                        }
                    });
                }
            }
        }
        setupNavigation();
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
                            Intent confirmScreen = new Intent(SlotConfirmationManagerActivity.this, SuccessfullySlotConfirmedActivity.class);
                            confirmScreen.putExtra("Confirm", "AVAILABLE");
                            confirmScreen.putExtra("PickedCourtName", "");
                            confirmScreen.putExtra("pickedSportName", "");
                            confirmScreen.putExtra("pickedDate", "");
                            confirmScreen.putExtra("pickedCourtID", "");
                            confirmScreen.putExtra("PickedSlotTime", "");
                            confirmScreen.putExtra("booking_id", booking_id);
                            confirmScreen.putExtra("location", "");
                            confirmScreen.putExtra("city", "");
                            startActivity(confirmScreen);
                        } else if (status.equalsIgnoreCase("Court is not available")) {
                            Intent confirmScreen = new Intent(SlotConfirmationManagerActivity.this, SuccessfullySlotConfirmedActivity.class);
                            confirmScreen.putExtra("Confirm", "UNAVAILABLE");
                            startActivity(confirmScreen);
                        }
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                            ringProgressDialog = null;
                        }
                        dataBaseHelper.deleteCart();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Utility.showCustomToast(getResources().getString(R.string.request_not_found), SlotConfirmationManagerActivity.this);
                } else if (statusCode == 500) {
                    Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), SlotConfirmationManagerActivity.this);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.unexpected_error), SlotConfirmationManagerActivity.this);
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
        MyApplication.getInstance().trackScreenView("Manager Slot Confirmation Screen");
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