package in.playcer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/29/2015.
 */
public class VerificationExistingActivity extends Activity {
    private EditText phoneNumET;
    private String mobileNumber = "";
    ProgressDialog ringProgressDialog;

    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_login);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        Utility.setDimensions(this);
        setupNavigation();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        phoneNumET = (EditText) findViewById(R.id.phoneNumETID);
        phoneNumET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        phoneNumET.setHintTextColor(getResources().getColor(R.color.grey));

        Button registerBtn = (Button) findViewById(R.id.verifyBtnID);
        registerBtn.getLayoutParams().height = (int) (Utility.screenWidth / 8);
        registerBtn.getLayoutParams().width = (int) (Utility.screenWidth / 8);

        RelativeLayout parentPanelRL = (RelativeLayout) findViewById(R.id.parentPanelRLID);
        parentPanelRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(phoneNumET.getWindowToken(), 0);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mobileNumber = phoneNumET.getText().toString();
                boolean isNetAvailable = Utility.isOnline(VerificationExistingActivity.this);
                if (isNetAvailable) {
                    if (mobileNumber.trim().length() > 0) {
                        if (mobileNumber.length() >= 10) {
                            ringProgressDialog = ProgressDialog.show(VerificationExistingActivity.this, "Please wait ...", "Verifying Mobile Number...", true);
                            ringProgressDialog.setCancelable(true);
                            RequestParams params = new RequestParams();
                            params.put("u", mobileNumber);
                            sendOtpRequesttoServer(AppConstants.OTP_REQUEST_URL, params, mobileNumber);

                            MyApplication.getInstance().trackEvent("Existing User Login", "Tapped Existing Login", "Existing user logging");
                        } else {
                            Utility.showCustomToast(getResources().getString(R.string.invalid_no), VerificationExistingActivity.this);
                        }
                    } else {

                        Utility.showCustomToast(getResources().getString(R.string.mobile_no_must_not_empty), VerificationExistingActivity.this);
                    }
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), VerificationExistingActivity.this);
                }
                imm.hideSoftInputFromWindow(phoneNumET.getWindowToken(), 0);
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
                startActivity(new Intent(VerificationExistingActivity.this, RegistrationActivity.class));
                onBackPressedAnimationByCHK();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        titleTV.setText("MOBILE VERIFICATION");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);

        Button backBtn = (Button) findViewById(R.id.backBtnID);
        backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(VerificationExistingActivity.this, RegistrationActivity.class));
                onBackPressedAnimationByCHK();
            }
        });

        //Button moreBtn = (Button) findViewById(R.id.moreBtnID);
        //moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        //moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
    }

    public void sendOtpRequesttoServer(String _Url, RequestParams params, final String _mobileNumber) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(_Url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                            ringProgressDialog = null;
                        }
                        JSONObject jsonResponse = new JSONObject(response);
                        int status = Integer.parseInt(jsonResponse.optString("status").toString());
                        if (status == 1) {
                            Intent verifyIntent = new Intent(VerificationExistingActivity.this, VerificationActivity.class);
                            verifyIntent.putExtra(Utility.KEY_PHONE_NUM, _mobileNumber);
                            verifyIntent.putExtra(Utility.KEY_NAME, "");
                            verifyIntent.putExtra(Utility.KEY_EMAIL_ID, "");
                            verifyIntent.putExtra(Utility.KEY_NONCE, "");
                            verifyIntent.putExtra(Utility.KEY_COMING_FROM, "Existing");
                            startActivity(verifyIntent);
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Utility.showCustomToast(getResources().getString(R.string.request_not_found), VerificationExistingActivity.this);
                } else if (statusCode == 500) {
                    Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), VerificationExistingActivity.this);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.unexpected_error), VerificationExistingActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(VerificationExistingActivity.this, RegistrationActivity.class));
            onBackPressedAnimationByCHK();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Existing Login");
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}
