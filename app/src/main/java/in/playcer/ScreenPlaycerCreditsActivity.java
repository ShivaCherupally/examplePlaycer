package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
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
 * Created by OFFICE on 3/16/2016.
 */
public class ScreenPlaycerCreditsActivity extends Activity {
    TextView walletBalanceTV;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(ScreenPlaycerCreditsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_playcer_credits);
        Utility.setDimensions(this);

        walletBalanceTV = (TextView) findViewById(R.id.walletBalanceTVID);

        if (Utility.isOnline(this)){
            String cookieStr = dataBaseHelper.getRegisteredCookie();
            RequestParams params = new RequestParams();
            params.put("cookie", "" + cookieStr);
            sendWalletBalanceRequesttoServer(AppConstants.WALLET_BALANCE_REQUEST_URL, params);
        } else {
            Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), ScreenPlaycerCreditsActivity.this);
        }
        setupNavigation();
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
                            String Credits = jsonResponse.getString("Credits").toString();
                            if (Credits != null && !Credits.isEmpty()) {
                                walletBalanceTV.setText("" + Html.fromHtml(Credits));
                            }
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
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ScreenPlaycerCreditsActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    finish();
                }
            }
        });
    }

    public void setupNavigation() {
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

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
        titleTV.setText("PLAYCER CREDITS");

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
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}