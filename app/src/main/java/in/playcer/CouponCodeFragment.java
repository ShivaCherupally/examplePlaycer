package in.playcer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import in.playcer.libs.ProgressWheel;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.SharedPreference;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 3/17/2016.
 */
public class CouponCodeFragment extends Fragment {
    View couponPageView;
    AppDataBaseHelper dataBaseHelper;
    String couponcodestr = "";
    String message = "";
    EditText couponcodeETID;
    Button applyBtnID;
    Button appliedBtnID;
    Button cancelBtnID;
    TextView haveCouponTVID;

    LinearLayout couponCodeLVID;
    LinearLayout cancelLVID;

    private static int cartAmountValue;
    private static int convenienceFeeValue;
    private static String bookingTypeShow;

    private static String KEY_ARG_cartAmountValue = "cartAmountValue";

    private static String KEY_AGR_convenienceFeeValue = "convenienceFeeValue";

    private static String KEY_ARG_bookingTypeShow = "bookingTypeShow";

    private ProgressWheel progressWheel_CENTER;

    InputMethodManager imm;


    public static CouponCodeFragment newInstance(int cartAmount, int convenienceFee, String bookingType) {
        CouponCodeFragment f = new CouponCodeFragment();
        Bundle b = new Bundle();
        b.putInt(KEY_ARG_cartAmountValue, cartAmount);
        b.putInt(KEY_AGR_convenienceFeeValue, convenienceFee);
        b.putString(KEY_ARG_bookingTypeShow, bookingType);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartAmountValue = getArguments().getInt(KEY_ARG_cartAmountValue);
        convenienceFeeValue = getArguments().getInt(KEY_AGR_convenienceFeeValue);
        bookingTypeShow = getArguments().getString(KEY_ARG_bookingTypeShow);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            couponPageView = inflater.inflate(R.layout.fragment_coupon_code_screen, null);
            dataBaseHelper = new AppDataBaseHelper(getActivity());

            couponCodeLVID = (LinearLayout) couponPageView.findViewById(R.id.couponCodeLVID);
            cancelLVID = (LinearLayout) couponPageView.findViewById(R.id.cancelLVID);

            couponcodeETID = (EditText) couponPageView.findViewById(R.id.couponcodeETID);
            couponcodeETID.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

            couponcodeETID.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    return false;
                }
            });

            imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            haveCouponTVID = (TextView) couponPageView.findViewById(R.id.haveCouponTVID);

            applyBtnID = (Button) couponPageView.findViewById(R.id.applyBtnID);
            applyBtnID.getLayoutParams().height = (int) (Utility.screenWidth / 8);
            applyBtnID.getLayoutParams().width = (int) (Utility.screenWidth / 8);

            appliedBtnID = (Button) couponPageView.findViewById(R.id.appliedBtnID);
            appliedBtnID.getLayoutParams().height = (int) (Utility.screenWidth / 8);

            cancelBtnID = (Button) couponPageView.findViewById(R.id.cancelBtnID);
            cancelBtnID.getLayoutParams().height = (int) (Utility.screenWidth / 8);
            cancelBtnID.getLayoutParams().width = (int) (Utility.screenWidth / 8);

            progressWheel_CENTER = (ProgressWheel) couponPageView.findViewById(R.id.progress_wheel1);
            progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel_CENTER.setRimColor(Color.LTGRAY);

            applyBtnID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    couponcodestr = couponcodeETID.getText().toString();
                    boolean isNetAvailable = Utility.isOnline(getActivity());
                    if (isNetAvailable) {
                        if (couponcodestr.trim().length() > 0) {
                            if (couponcodestr.trim().length() > 2) {
                                progressWheel_CENTER.setVisibility(View.VISIBLE);
                                String cookieStr = dataBaseHelper.getRegisteredCookie();
                                RequestParams params = new RequestParams();
                                params.put("cookie", "" + cookieStr);
                                params.put("coupon_code", "" + couponcodestr);
                                params.put("cart_value", "" + String.valueOf(cartAmountValue));
                                params.put("booking_type", bookingTypeShow);
                                sendCouponCodeRequesttoServer(AppConstants.COUPON_CODE_AMOUNT_REQUEST_URL, params, couponcodestr);
                            } else {
                                //Utility.showCustomToast("Please Enter Valid Coupon Code", getActivity());
                                Utility.showCustomToast(getResources().getString(R.string.pls_enter_valid_coupon), getActivity());
                            }
                        } else {
                            Utility.showCustomToast(getResources().getString(R.string.pls_enter_coupon_code), getActivity());
                        }
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), getActivity());
                    }
                    imm.hideSoftInputFromWindow(couponcodeETID.getWindowToken(), 0);
                }
            });

            cancelBtnID.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    couponcodeETID.setText("");
                    progressWheel_CENTER.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            String couponCodeAmount = "00";
                            int actualCouponAmount = Integer.parseInt(couponCodeAmount);
                            String AfterCreditsAppliedStr= SharedPreference.getPreferences(getActivity(), "AfterCreditsApplied");
                            if (AfterCreditsAppliedStr!=null && !AfterCreditsAppliedStr.isEmpty()){
                                int finalCartAmount = Integer.parseInt(AfterCreditsAppliedStr);
                                int AfterCouponApplied = finalCartAmount - actualCouponAmount;    //
                                int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                ScreenCheckoutActivity.couponAmountTVID.setText("- " +getResources().getString(R.string.rs) +" "+ actualCouponAmount);
                                ScreenCheckoutActivity.payAmountTVID.setText(""+getResources().getString(R.string.rs) +" "+ finalPayAmount);
                                SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmount);
                            } else {
                                int finalCartAmount = cartAmountValue;
                                int AfterCouponApplied = finalCartAmount - actualCouponAmount;    //
                                int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                ScreenCheckoutActivity.couponAmountTVID.setText(" - " + getResources().getString(R.string.rs) +" "+ actualCouponAmount);

                                ScreenCheckoutActivity.payAmountTVID.setText(getResources().getString(R.string.rs) +" "+ String.valueOf(finalPayAmount));
                                SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + String.valueOf(finalPayAmount));
                            }
                            SharedPreference.setPreference(getActivity(), "couponBalance", "");
                            progressWheel_CENTER.setVisibility(View.GONE);
                            ScreenCheckoutActivity.couponCodeRLID.setVisibility(View.GONE);
                            ScreenCheckoutActivity.couponDividerVID.setVisibility(View.GONE);
                            couponCodeLVID.setVisibility(View.VISIBLE);
                            cancelLVID.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            });
            return couponPageView;
        } catch (Exception ee){
            ee.printStackTrace();
            return  null;
        }
    }

    private void sendCouponCodeRequesttoServer(String confirmationURL, RequestParams _params, final String _couponcodestr) {
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
                            message = jsonResponse.getString("message").toString();
                            if (message.contains("Invalid")) {
                                progressWheel_CENTER.setVisibility(View.GONE);
                                Utility.showCustomToast(getResources().getString(R.string.invalid_coupon), getActivity());
                            } else if (message.contains("Not applicable")) {
                                progressWheel_CENTER.setVisibility(View.GONE);
                                //Utility.showCustomToast("", getActivity());
                                Utility.showCustomToast(getResources().getString(R.string.coupon_not_applicable), getActivity());
                            } else if (message.equalsIgnoreCase("Valid")) {
                                final String amount = jsonResponse.getString("amount").toString();
                                if(amount!=null){
                                    couponCodeLVID.setVisibility(View.GONE);
                                    cancelLVID.setVisibility(View.VISIBLE);
                                    progressWheel_CENTER.setVisibility(View.VISIBLE);

                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            String couponCodeAmount = amount;
                                            if (!couponCodeAmount.isEmpty() && !couponCodeAmount.equals("0")) {
                                                SharedPreference.setPreference(getActivity(), "couponCode", _couponcodestr);
                                                SharedPreference.setPreference(getActivity(), "couponAmount", amount);

                                                ScreenCheckoutActivity.couponAmountlTV.setText("Coupon-" + _couponcodestr);
                                                ScreenCheckoutActivity.couponCodeRLID.setVisibility(View.VISIBLE);
                                                ScreenCheckoutActivity.couponDividerVID.setVisibility(View.VISIBLE);
                                                if (couponCodeAmount.contains("%")){
                                                    String actualCouponAmountStr = couponCodeAmount.substring(0, couponCodeAmount.length() - 1);
                                                    int actualCouponAmount = Integer.parseInt(actualCouponAmountStr);
                                                    String AfterCreditsAppliedStr= SharedPreference.getPreferences(getActivity(), "AfterCreditsApplied");
                                                    if (AfterCreditsAppliedStr!=null && !AfterCreditsAppliedStr.isEmpty()){
                                                        int finalCartAmount = Integer.parseInt(AfterCreditsAppliedStr);
                                                        int percentageOff = (finalCartAmount * actualCouponAmount) / 100;
                                                        int AfterCouponApplied = finalCartAmount - percentageOff;    //
                                                        int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                                        ScreenCheckoutActivity.couponAmountTVID.setText("- " + getResources().getString(R.string.rs) +" "+ percentageOff);
                                                        ScreenCheckoutActivity.payAmountTVID.setText(getResources().getString(R.string.rs) +" "+ finalPayAmount);
                                                        SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmount);
                                                        SharedPreference.setPreference(getActivity(), "couponBalance", String.valueOf(percentageOff));
                                                    } else {
                                                        int finalCartAmount = cartAmountValue;
                                                        int percentageOff = (finalCartAmount * actualCouponAmount) / 100;
                                                        int AfterCouponApplied = finalCartAmount - percentageOff;    //
                                                        int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                                        ScreenCheckoutActivity.couponAmountTVID.setText(" - " + getResources().getString(R.string.rs) +" "+ percentageOff);
                                                        ScreenCheckoutActivity.payAmountTVID.setText(getResources().getString(R.string.rs) +" "+ String.valueOf(finalPayAmount));
                                                        SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + String.valueOf(finalPayAmount));
                                                        SharedPreference.setPreference(getActivity(), "couponBalance", String.valueOf(percentageOff));
                                                    }
                                                } else {
                                                    int actualCouponAmount = Integer.parseInt(couponCodeAmount);
                                                    String AfterCreditsAppliedStr= SharedPreference.getPreferences(getActivity(), "AfterCreditsApplied");
                                                    if (AfterCreditsAppliedStr!=null && !AfterCreditsAppliedStr.isEmpty()){
                                                        int finalCartAmount = Integer.parseInt(AfterCreditsAppliedStr);
                                                        int AfterCouponApplied = finalCartAmount - actualCouponAmount;    //
                                                        int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                                        ScreenCheckoutActivity.couponAmountTVID.setText("- " +getResources().getString(R.string.rs) +" "+ actualCouponAmount);
                                                        ScreenCheckoutActivity.payAmountTVID.setText(""+getResources().getString(R.string.rs) +" "+ finalPayAmount);
                                                        SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmount);
                                                        SharedPreference.setPreference(getActivity(), "couponBalance", String.valueOf(actualCouponAmount));
                                                    } else {
                                                        int finalCartAmount = cartAmountValue;
                                                        int AfterCouponApplied = finalCartAmount - actualCouponAmount;    //
                                                        int finalPayAmount = AfterCouponApplied + convenienceFeeValue;
                                                        ScreenCheckoutActivity.couponAmountTVID.setText(" - " + getResources().getString(R.string.rs) +" "+ actualCouponAmount);

                                                        ScreenCheckoutActivity.payAmountTVID.setText(getResources().getString(R.string.rs) +" "+ String.valueOf(finalPayAmount));
                                                        SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + String.valueOf(finalPayAmount));
                                                        SharedPreference.setPreference(getActivity(), "couponBalance", String.valueOf(actualCouponAmount));
                                                    }
                                                }
                                            }  else {
                                                Utility.showCustomToast(getResources().getString(R.string.coupon_expire), getActivity());
                                                SharedPreference.setPreference(getActivity(), "couponCode", "");
                                                SharedPreference.setPreference(getActivity(), "couponAmount", "");
                                            }
                                            progressWheel_CENTER.setVisibility(View.GONE);
                                        }
                                    }, 500);
                                } else {
                                    progressWheel_CENTER.setVisibility(View.GONE);
                                    Utility.showCustomToast(getResources().getString(R.string.coupon_expire), getActivity());
                                    SharedPreference.setPreference(getActivity(), "couponCode", "");
                                    SharedPreference.setPreference(getActivity(), "couponAmount", "");
                                }
                            } else {
                                progressWheel_CENTER.setVisibility(View.GONE);
                                Utility.showCustomToast(getResources().getString(R.string.request_failed), getActivity());
                            }
                        } else {
                            progressWheel_CENTER.setVisibility(View.GONE);
                            Utility.showCustomToast(getResources().getString(R.string.request_failed), getActivity());
                        }
                    } catch (JSONException e) {
                        Utility.showCustomToast(getResources().getString(R.string.request_failed), getActivity());
                        progressWheel_CENTER.setVisibility(View.GONE);
                        e.printStackTrace();
                    } catch (Exception e) {
                        Utility.showCustomToast(getResources().getString(R.string.request_failed), getActivity());
                        progressWheel_CENTER.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    if (progressWheel_CENTER != null) {
                        progressWheel_CENTER.setVisibility(View.GONE);
                        progressWheel_CENTER = null;
                    }
                    Utility.showCustomToast(getResources().getString(R.string.request_failed), getActivity());
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (progressWheel_CENTER != null) {
                    progressWheel_CENTER.setVisibility(View.GONE);
                    progressWheel_CENTER = null;
                }
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(getActivity(), DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    getActivity().finish();
                } else {
                    if (statusCode == 404) {
                        Utility.showCustomToast(getResources().getString(R.string.request_not_found), getActivity());
                    } else if (statusCode == 500) {
                        Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), getActivity());
                    } else {
                        Utility.showCustomToast(getResources().getString(R.string.unexpected_error), getActivity());
                    }
                }
            }
        });
    }


}