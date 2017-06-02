package in.playcer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/29/2015.
 */
public class VerificationActivity extends Activity {
    private EditText otpET;
    private String mobileNumber = "";
    private AppDataBaseHelper dbHelper = new AppDataBaseHelper(this);

    private String nameStr = "";
    private String emailStr = "";
    private String nonceStr = "";
    public String referralCodeStr = "";

    private String COMING_FROM = "";

    ProgressDialog ringProgressDialog;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_mobile_verification_register);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        Utility.setDimensions(this);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mobileNumber = bundle.getString(Utility.KEY_PHONE_NUM);
                nameStr = bundle.getString(Utility.KEY_NAME);
                emailStr = bundle.getString(Utility.KEY_EMAIL_ID);
                nonceStr = bundle.getString(Utility.KEY_NONCE);
                COMING_FROM = bundle.getString(Utility.KEY_COMING_FROM);
                if (COMING_FROM == null){
                    COMING_FROM = "";
                }
                referralCodeStr = bundle.getString(Utility.KEY_REFERRAL_CODE);

                TextView mb = (TextView) findViewById(R.id.mobileTVID);
                mb.setText("We have sent an OTP via SMS to "+mobileNumber+" please enter it here to continue.");
            }
        }
        setupNavigation();

        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        otpET = (EditText) findViewById(R.id.phoneNumETID);
        otpET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        otpET.setHintTextColor(getResources().getColor(R.color.grey));

        RelativeLayout parentPanelRL = (RelativeLayout) findViewById(R.id.parentPanelRLID);
        parentPanelRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(otpET.getWindowToken(), 0);
            }
        });

        Button verifyBtn = (Button) findViewById(R.id.verifyBtnID);
        verifyBtn.getLayoutParams().height = (int) (Utility.screenWidth / 8);
        verifyBtn.getLayoutParams().width = (int) (Utility.screenWidth / 8);

        verifyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mobileNumber.trim().length() > 2) {
                    String OTPstr = otpET.getText().toString();
                    boolean isNetAvailable = Utility.isOnline(VerificationActivity.this);
                    if (isNetAvailable) {
                        if (OTPstr.trim().length() > 0) {
                            try {
                                ringProgressDialog = ProgressDialog.show(VerificationActivity.this, "Please wait ...", "Verifying OTP...", true);
                                ringProgressDialog.setCancelable(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            RequestParams params = new RequestParams();
                            params.put("u", mobileNumber);
                            params.put("otp", OTPstr);
                            sendOtpRequesttoServer(AppConstants.GET_AUTH_OTP_URL, params);

                            MyApplication.getInstance().trackEvent("OTP Verification", "Tapped OTP", "Verifying");
                        } else {
                            Utility.showCustomToast(getResources().getString(R.string.otp_must_not_empty), VerificationActivity.this);
                        }
                    }  else {
                        Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), VerificationActivity.this);
                    }
                } else {
                    startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                    onBackPressedAnimationByCHK();
                    Utility.showCustomToast(getResources().getString(R.string.pls_enter_mbl_no_for_otp), VerificationActivity.this);
                }
                imm.hideSoftInputFromWindow(otpET.getWindowToken(), 0);
            }
        });
    }

    public void setupNavigation() {
        {
            RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
            headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);

            RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
            backAllRL.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (COMING_FROM.equalsIgnoreCase("Existing")) {
                        startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                    } else {
                        startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                    }
                    onBackPressedAnimationByCHK();
                }
            });

            TextView titleTV = (TextView) findViewById(R.id.titleTVID);
            titleTV.setText("VERIFICATION");

            TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
            subTitleTV.setVisibility(View.GONE);

            Button backBtn = (Button) findViewById(R.id.backBtnID);
            backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
            backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (COMING_FROM.equalsIgnoreCase("Existing")) {
                        startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                    } else {
                        startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                    }
                    onBackPressedAnimationByCHK();
                }
            });

            Button moreBtn = (Button) findViewById(R.id.moreBtnID);
            moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
            moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        }
    }

    public void sendOtpRequesttoServer(String _Url, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(_Url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        if (response.contains("ok")) {
                            JSONObject jsonResponse = new JSONObject(response);
                            String statusStr = jsonResponse.optString("status").toString();
                            if (statusStr.equalsIgnoreCase("ok")) {
                                Utility.COOKIE= jsonResponse.optString("cookie").toString();
                                Utility.USER_ID = Integer.parseInt(jsonResponse.optString("user_id").toString());
                                Utility.ROLE = jsonResponse.optString("role").toString();
                                String email_id = jsonResponse.optString("email_id").toString();
                                String mobile = jsonResponse.optString("mobile").toString();
                                //startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                                startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                                String deviceId = Settings.Secure.getString(VerificationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                                dbHelper.addRegisteredUDID(deviceId, Utility.COOKIE,  String.valueOf(Utility.USER_ID), email_id, mobile, "", Utility.ROLE);
                                VerificationActivity.this.finish();
                                if (ringProgressDialog != null) {
                                    ringProgressDialog.dismiss();
                                    ringProgressDialog=null;
                                }
                            }
                        } else {
                            if (response.equalsIgnoreCase("1")) {
                                if (emailStr != null) {
                                    if (emailStr.trim().length() > 1) {
                                        if (ringProgressDialog != null) {
                                            ringProgressDialog.dismiss();
                                            ringProgressDialog=null;
                                        }
                                        ringProgressDialog = ProgressDialog.show(VerificationActivity.this, "Please wait ...", "Authenticating Wait...", true);
                                        ringProgressDialog.setCancelable(true);
                                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                                        RequestParams params = new RequestParams();
                                        params.put("username",mobileNumber);
                                        params.put("email",emailStr);
                                        params.put("nonce",nonceStr);
                                        params.put("referral_key",referralCodeStr);
                                        params.put("display_name",nameStr);
                                        //username=9550412688&email=santosh.surabi@gmail.com&nonce=aed40cf2b6&display_name=Santosh
                                        registerValidUserRequest(AppConstants.REGISTER_REQUEST_URL, params);
                                    } else {
                                        Utility.showCustomToast(getResources().getString(R.string.user_does_not_exists), VerificationActivity.this);
                                        if (COMING_FROM.equalsIgnoreCase("Existing")) {
                                            startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                                        } else {
                                            startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                                        }
                                        onBackPressedAnimationByCHK();
                                    }
                                } else {
                                    Utility.showCustomToast(getResources().getString(R.string.user_does_not_exists), VerificationActivity.this);
                                    if (COMING_FROM.equalsIgnoreCase("Existing")) {
                                        startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                                    } else {
                                        startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                                    }
                                    onBackPressedAnimationByCHK();
                                }
                            } else  if (response.equalsIgnoreCase("0")) {
                                if (ringProgressDialog != null) {
                                    ringProgressDialog.dismiss();
                                    ringProgressDialog=null;
                                }
                                otpET.setText("");
                                Utility.showCustomToast(getResources().getString(R.string.invalid_otp), VerificationActivity.this);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Utility.showCustomToast(getResources().getString(R.string.request_not_found), VerificationActivity.this);
                } else if (statusCode == 500) {
                    Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), VerificationActivity.this);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.unexpected_error), VerificationActivity.this);
                }
            }
        });
    }

    private void registerValidUserRequest(String registerRequestUrl, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(registerRequestUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);
                        String statusStr = jsonResponse.optString("status").toString();
                        if (statusStr.equalsIgnoreCase("ok")) {
                            Utility.COOKIE= jsonResponse.optString("cookie").toString();
                            Utility.USER_ID = Integer.parseInt(jsonResponse.optString("user_id").toString());
                            String email_id = jsonResponse.optString("email_id").toString();
                            String mobile = jsonResponse.optString("mobile").toString();
                            //startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                            startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                            String deviceId = Settings.Secure.getString(VerificationActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            dbHelper.addRegisteredUDID(deviceId, Utility.COOKIE,  String.valueOf(Utility.USER_ID), email_id, mobile, "", Utility.ROLE);
                            VerificationActivity.this.finish();
                            if (ringProgressDialog != null) {
                                ringProgressDialog.dismiss();
                                ringProgressDialog=null;
                            }
                        } else if(statusStr.equalsIgnoreCase("error")) {
                            String error = jsonResponse.optString("error").toString();
                            Utility.showCustomToast(getResources().getString(R.string.username_already_exists), VerificationActivity.this);
                            if (COMING_FROM.equalsIgnoreCase("Existing")) {
                                startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                            } else {
                                startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                            }
                            onBackPressedAnimationByCHK();
                            if (ringProgressDialog != null) {
                                ringProgressDialog.dismiss();
                                ringProgressDialog=null;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content)  {
                try {
                    Log.w("statusCode",""+statusCode);
                    Log.w("error",""+error);
                    Log.w("content", "" + content);
                    if (content != null) {
                        if (content.contains("E-mail address is already in use.")) {
                            Utility.showCustomToast("E-mail address is already exists.", VerificationActivity.this);
                        } else if (content.contains("already exists")) {
                            Utility.showCustomToast("Username already exists.", VerificationActivity.this);
                        } else {
                            if (statusCode == 404) {
                                Utility.showCustomToast("Requested resource not found", VerificationActivity.this);
                        } else if (statusCode == 500) {
                                Utility.showCustomToast("Something went wrong at server end", VerificationActivity.this);
                        } else {
                                Utility.showCustomToast("Unexpected Error occurred! Try again.", VerificationActivity.this);
                            }
                        }
                    }
                    if (COMING_FROM.equalsIgnoreCase("Existing")) {
                        startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                    } else {
                        startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                    }
                    onBackPressedAnimationByCHK();
                    if (ringProgressDialog != null) {
                        ringProgressDialog.dismiss();
                        ringProgressDialog=null;
                    }
                } catch (Exception e) {
                        e.printStackTrace();
                    if (COMING_FROM.equalsIgnoreCase("Existing")) {
                        startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
                    } else {
                        startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
                    }
                    onBackPressedAnimationByCHK();
                        if (ringProgressDialog != null) {
                             ringProgressDialog.dismiss();
                             ringProgressDialog=null;
                    }
                    Utility.showCustomToast("Unexpected Error occurred! Try again.", VerificationActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (COMING_FROM.equalsIgnoreCase("Existing")) {
                startActivity(new Intent(VerificationActivity.this, VerificationExistingActivity.class));
            } else {
                startActivity(new Intent(VerificationActivity.this, RegistrationActivity.class));
            }
            onBackPressedAnimationByCHK();
            return false;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("OTP");
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}
