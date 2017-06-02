package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import in.playcer.utilities.AppConstants;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/29/2015.
 */
public class RegistrationActivity extends Activity {
    private ImageView logoIV;
    private EditText unameET;
    private EditText eemailET;
    private EditText phoneNumET;
    private EditText referralCodeET;

    public String nameStr = "";
    public String phoneStr = "";
    public String emailStr = "";
    public String referralCodeStr = "";

    private String nonce = "";
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, input_layout_refferal_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        Utility.setDimensions(this);

        logoIV = (ImageView) findViewById(R.id.logo);
        logoIV.getLayoutParams().height = (int) (Utility.screenHeight / 4.2);

        unameET = (EditText) findViewById(R.id.unameETID);
        unameET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        unameET.setHintTextColor(getResources().getColor(R.color.grey));

        eemailET = (EditText) findViewById(R.id.eemailETID);
        eemailET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        eemailET.setHintTextColor(getResources().getColor(R.color.grey));

        phoneNumET = (EditText) findViewById(R.id.phoneNumETID);
        phoneNumET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        phoneNumET.setHintTextColor(getResources().getColor(R.color.grey));

        referralCodeET = (EditText) findViewById(R.id.referralCodeETID);
        referralCodeET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        referralCodeET.setHintTextColor(getResources().getColor(R.color.grey));

        Button registerBtn = (Button) findViewById(R.id.registerBtnID);
        registerBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

        ///Materal Design For EditText Fields - 23/03/16
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        input_layout_refferal_code = (TextInputLayout) findViewById(R.id.input_layout_refferal_code);

        unameET.addTextChangedListener(new MyTextWatcher(unameET));
        eemailET.addTextChangedListener(new MyTextWatcher(eemailET));
        phoneNumET.addTextChangedListener(new MyTextWatcher(phoneNumET));
        referralCodeET.addTextChangedListener(new MyTextWatcher(referralCodeET));
        ///

        boolean isNetAvailable = Utility.isOnline(RegistrationActivity.this);
        if (isNetAvailable) {
            sendGetNonceRequesttoServer(AppConstants.GET_NONCE_URL);
        } else {
            Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), RegistrationActivity.this);
            finish();
        }

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nameStr = unameET.getText().toString();
                emailStr = eemailET.getText().toString();
                phoneStr = phoneNumET.getText().toString();
                referralCodeStr = referralCodeET.getText().toString();

                boolean isNetAvailable = Utility.isOnline(RegistrationActivity.this);
                if (isNetAvailable) {
                    if (nameStr.trim().length() > 0 && emailStr.trim().length() > 0 && phoneStr.trim().length() > 0) {
                        if (Utility.isEmailValid(emailStr)) {
                            if (phoneStr.length() >= 10) {
                                RequestParams params = new RequestParams();
                                params.put("u", phoneStr);
                                sendOtpRequesttoServer(AppConstants.OTP_REQUEST_URL, params);
                                MyApplication.getInstance().trackEvent("Register", "Register Tapped", "Registering");
                            } else {
                                Utility.showCustomToast(getResources().getString(R.string.invalid_no), RegistrationActivity.this);
                            }
                        } else {
                            Utility.showCustomToast(getResources().getString(R.string.invalid_email), RegistrationActivity.this);
                        }
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.all_fields_mandatory), RegistrationActivity.this);
                    }
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), RegistrationActivity.this);
                }
            }
        });

        Button existingUserBtn = (Button) findViewById(R.id.existingUserBtnID);
        existingUserBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

        existingUserBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent verifyIntent = new Intent(RegistrationActivity.this, VerificationExistingActivity.class);
                startActivity(verifyIntent);
                finish();
            }
        });
    }

    private void sendGetNonceRequesttoServer(String getNonceUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(getNonceUrl, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);
                        String statuss = jsonResponse.optString("status").toString();
                        String controller = jsonResponse.optString("controller").toString();
                        String register = jsonResponse.optString("method").toString();
                        nonce = jsonResponse.optString("nonce").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // sCServiceListener.onServiceFailed(statusCode, error, content, REQUEST_EVENT_TYPE);
            }
        });
    }

    public void sendOtpRequesttoServer(String _Url, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(_Url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);
                        int status = Integer.parseInt(jsonResponse.optString("status").toString());
                        if (status == 1) {
                            Intent verifyIntent = new Intent(RegistrationActivity.this, VerificationActivity.class);
                            verifyIntent.putExtra(Utility.KEY_PHONE_NUM, phoneStr);
                            verifyIntent.putExtra(Utility.KEY_NAME, nameStr);
                            verifyIntent.putExtra(Utility.KEY_EMAIL_ID, emailStr);
                            verifyIntent.putExtra(Utility.KEY_REFERRAL_CODE, referralCodeStr);
                            verifyIntent.putExtra(Utility.KEY_NONCE, nonce);
                            verifyIntent.putExtra(Utility.KEY_COMING_FROM, "NewRegisration");
                            startActivity(verifyIntent);
                            finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                // sCServiceListener.onServiceFailed(statusCode, error, content, REQUEST_EVENT_TYPE);
            }
        });
    }

    ///Materal Design
    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.unameETID:
                    validateName();
                    break;
                case R.id.eemailETID:
                    validateEmail();
                    break;
                case R.id.phoneNumETID:
                    validatePhoneNo();
                    break;
                case R.id.referralCodeETID:
                    validateRefferalCode();
                    break;
            }
        }
    }


    private boolean validateName() {
        if (unameET.getText().toString().trim().isEmpty()) {
            //inputLayoutName.setError(getString(R.string.err_msg_name));
            inputLayoutName.setError(Html.fromHtml("<font color='white'>Enter your full name</font>"));
            requestFocus(unameET);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = eemailET.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(eemailET);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhoneNo() {
        if (phoneNumET.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_phoneno)); //err_msg_refferalcode
            requestFocus(phoneNumET);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRefferalCode() {
        if (referralCodeET.getText().toString().trim().isEmpty()) {
            // input_layout_refferal_code.setError(getString(R.string.err_msg_refferalcode));
            requestFocus(referralCodeET);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    ///


    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Registration");
    }
}