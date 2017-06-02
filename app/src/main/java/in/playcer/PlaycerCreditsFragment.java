package in.playcer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
public class PlaycerCreditsFragment extends Fragment {
    private static int cartAmountValue;
    private static int convenienceFeeValue;

    private static String KEY_ARG_cartAmountValue = "cartAmountValue";

    private static String KEY_AGR_convenienceFeeValue = "convenienceFeeValue";

    private ProgressWheel progressWheel_CENTER;

    View playcerCreditPageView;
    String checkedValue;
    int creditsBalance = 0;
    TextView checkBox;
    TextView totalCreditsTV;

    RelativeLayout playcerCreditsRL;

    TextView playcerCreditsValueTV;

    AppDataBaseHelper dataBaseHelper;

    String usable_credits=null;
    String totalCredits=null;


    public static PlaycerCreditsFragment newInstance(int cartAmount, int convenienceFee) {
        PlaycerCreditsFragment f = new PlaycerCreditsFragment();
        Bundle b = new Bundle();
        b.putInt(KEY_ARG_cartAmountValue, cartAmount);
        b.putInt(KEY_AGR_convenienceFeeValue, convenienceFee);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartAmountValue = getArguments().getInt(KEY_ARG_cartAmountValue);
        convenienceFeeValue = getArguments().getInt(KEY_AGR_convenienceFeeValue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        playcerCreditPageView = inflater.inflate(R.layout.fragment_playce_credits, null);

        progressWheel_CENTER = (ProgressWheel) playcerCreditPageView.findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);

        playcerCreditsRL = (RelativeLayout) playcerCreditPageView.findViewById(R.id.playcerCreditsRLID);

        checkBox = (TextView) playcerCreditPageView.findViewById(R.id.creditCHID);

        totalCreditsTV = (TextView) playcerCreditPageView.findViewById(R.id.totalCreditsTVID);

        playcerCreditsValueTV = (TextView) playcerCreditPageView.findViewById(R.id.playcerCreditsValueTVID);

        if (Utility.isOnline(getActivity())){
            dataBaseHelper = new AppDataBaseHelper(getActivity());
            String cookieStr = dataBaseHelper.getRegisteredCookie();
            RequestParams params = new RequestParams();
            params.put("cookie", "" + cookieStr);
            sendWalletBalanceRequesttoServer(AppConstants.WALLET_BALANCE_REQUEST_URL, params);
        } else {
            Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), getActivity());
        }

        playcerCreditsRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (playcerCreditsValueTV.getText().toString().equalsIgnoreCase("0")) {
                    Utility.showCustomToast(getResources().getString(R.string.sorry_dont_have_credits), getActivity());
                } else {
                    if (creditsBalance == 0) {
                        creditsBalance = Integer.parseInt(usable_credits);
                        if (ScreenCheckoutActivity.cartAmount > creditsBalance){
                            int totalCC = Integer.parseInt(totalCredits);
                            if (creditsBalance > totalCC){
                                usable_credits = String.valueOf(totalCC);
                            }
                            checkedValue = ""+usable_credits;
                            SharedPreference.setPreference(getActivity(), "checkedValue", checkedValue);
                            progressWheel_CENTER.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    ScreenCheckoutActivity.creditsAmountTVID.setText(" - " + " \u20B9 " + checkedValue);
                                    ScreenCheckoutActivity.creditsRLID.setVisibility(View.VISIBLE);
                                    ScreenCheckoutActivity.creditDividerVID.setVisibility(View.VISIBLE);
                                    String couponBalancestr = SharedPreference.getPreferences(getActivity(), "couponBalance");
                                    if (couponBalancestr != null && !couponBalancestr.isEmpty()) {
                                        int couponBalanceint = Integer.parseInt(couponBalancestr);
                                        int deductCreditsFromCartAmount = cartAmountValue - couponBalanceint;
                                        int finalPayAmount = deductCreditsFromCartAmount + convenienceFeeValue;
                                        int PlaycerCreditsDefault = Integer.parseInt(checkedValue);
                                        if (finalPayAmount > PlaycerCreditsDefault){
                                            int deductCreditsPlusCoupon = deductCreditsFromCartAmount-PlaycerCreditsDefault;
                                            finalPayAmount = deductCreditsPlusCoupon + convenienceFeeValue;
                                            String finalPayAmountStr = String.valueOf(finalPayAmount);
                                            ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                                            SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                                            SharedPreference.setPreference(getActivity(), "AfterCreditsApplied", "" + String.valueOf(deductCreditsFromCartAmount));
                                        } else {
                                            Utility.showCustomToast(getResources().getString(R.string.sorry_didnt_apply), getActivity());
                                            String finalPayAmountStr = String.valueOf(finalPayAmount);
                                            ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                                            SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                                        }
                                    } else {
                                        int PlaycerCreditsDefault = Integer.parseInt(checkedValue);
                                        int deductCreditsFromCartAmount = cartAmountValue - PlaycerCreditsDefault;
                                        int finalPayAmount = deductCreditsFromCartAmount+convenienceFeeValue;
                                        String finalPayAmountStr = String.valueOf(finalPayAmount);
                                        ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                                        SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                                        SharedPreference.setPreference(getActivity(), "AfterCreditsApplied", "" +String.valueOf(deductCreditsFromCartAmount));
                                    }

                                    progressWheel_CENTER.setVisibility(View.GONE);
                                    checkBox.setBackgroundResource(R.drawable.city_tick);
                                }
                            }, 500);
                        } else {
                            Utility.showCustomToast(getResources().getString(R.string.sorry_your_credits_less), getActivity());
                        }
                    } else {
                        checkedValue = "0";
                        SharedPreference.setPreference(getActivity(), "checkedValue", checkedValue);
                        creditsBalance = 0;
                        ScreenCheckoutActivity.creditsRLID.setVisibility(View.GONE);
                        ScreenCheckoutActivity.creditDividerVID.setVisibility(View.GONE);
                        checkBox.setBackgroundResource(R.drawable.city_untick);
                        SharedPreference.setPreference(getActivity(), "AfterCreditsApplied", "");

                        String couponBalancestr = SharedPreference.getPreferences(getActivity(), "couponBalance");
                        if (couponBalancestr != null && !couponBalancestr.isEmpty()) {
                            int couponBalanceint = Integer.parseInt(couponBalancestr);
                            int deductCreditsFromCartAmount = cartAmountValue - couponBalanceint;
                            int finalPayAmount = deductCreditsFromCartAmount + convenienceFeeValue;
                            int PlaycerCreditsDefault = Integer.parseInt(checkedValue);
                            if (finalPayAmount > PlaycerCreditsDefault){
                                int deductCreditsPlusCoupon = deductCreditsFromCartAmount-PlaycerCreditsDefault;
                                finalPayAmount = deductCreditsPlusCoupon + convenienceFeeValue;
                                String finalPayAmountStr = String.valueOf(finalPayAmount);
                                ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                                SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                            } else {
                                Utility.showCustomToast(getResources().getString(R.string.sorry_didnt_apply), getActivity());
                                String finalPayAmountStr = String.valueOf(finalPayAmount);
                                ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                                SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                            }
                        } else {
                            int PlaycerCreditsDefault = Integer.parseInt(checkedValue);
                            int deductCreditsFromCartAmount = cartAmountValue - PlaycerCreditsDefault;
                            int finalPayAmount = deductCreditsFromCartAmount+convenienceFeeValue;
                            String finalPayAmountStr = String.valueOf(finalPayAmount);
                            ScreenCheckoutActivity.payAmountTVID.setText(" \u20B9 " + finalPayAmountStr);
                            SharedPreference.setPreference(getActivity(), "PAY_AMOUNT", "" + finalPayAmountStr);
                        }
                    }
                }
            }
        });

        return playcerCreditPageView;
    }

    private void sendWalletBalanceRequesttoServer(String inviteFriendsRequestUrl, RequestParams _params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(inviteFriendsRequestUrl, _params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    JSONObject jsonResponse;
                    try {
                        jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status").toString();
                        if (status.equalsIgnoreCase("ok")) {
                            totalCredits = jsonResponse.getString("Credits").toString();
                            usable_credits = jsonResponse.getString("usable_credits").toString();
                            if (totalCredits != null && !totalCredits.isEmpty()) {
                                totalCreditsTV.setText("Total available credits: "+totalCredits);
                            } else {
                                totalCreditsTV.setText("0");
                            }

                            if (usable_credits != null && !usable_credits.isEmpty()) {
                                playcerCreditsValueTV.setText("" + Html.fromHtml(usable_credits));
                            } else {
                                playcerCreditsValueTV.setText("0");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        totalCreditsTV.setText("0");
                        playcerCreditsValueTV.setText("0");
                    } catch (Exception e) {
                        e.printStackTrace();
                        totalCreditsTV.setText("0");
                        playcerCreditsValueTV.setText("0");
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                playcerCreditsValueTV.setText("0");
                totalCreditsTV.setText("0");
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(getActivity(), DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    getActivity().finish();
                }
            }
        });
    }
}