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
 * Created by OFFICE on 3/15/2016.
 */
public class ScreenInviteFriendsActivity extends Activity {
    Button inviteBtnID;
    TextView moreinfoTV, referralCodeTV;

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(ScreenInviteFriendsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_invite_friends_activity);
        Utility.setDimensions(this);
        setupNavigation();

        moreinfoTV = (TextView) findViewById(R.id.moreinfoTVID);

        referralCodeTV = (TextView) findViewById(R.id.referralCodeTVID);

        inviteBtnID = (Button) findViewById(R.id.inviteBtnID);
        inviteBtnID.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

        if (Utility.isOnline(this)){
            String cookieStr = dataBaseHelper.getRegisteredCookie();
            RequestParams params = new RequestParams();
            params.put("cookie", "" + cookieStr);
            params.put("promokey", "referral");
            sendInviteFrinedsRequesttoServer(AppConstants.INVITE_FRIENDS_REQUEST_URL, params);
        } else {
            Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), ScreenInviteFriendsActivity.this);
        }

        inviteBtnID.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isNetAvailable = Utility.isOnline(ScreenInviteFriendsActivity.this);
                if (isNetAvailable) {
                    appShareScreen("" + referralCodeTV.getText().toString());
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), ScreenInviteFriendsActivity.this);
                }
            }
        });
    }

    private void sendInviteFrinedsRequesttoServer(String inviteFriendsRequestUrl, RequestParams _params) {
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
                        if (status.equalsIgnoreCase("ok"))
                        {
                            JSONObject ReferralOjc = jsonResponse.getJSONObject("Referral");
                            String referralCodeStr = ReferralOjc.getString("referral_key").toString();
                            String contentStr = ReferralOjc.getString("content").toString();

                            referralCodeTV.setText(""+ Html.fromHtml(referralCodeStr));

                            if (contentStr!=null && !contentStr.isEmpty()){
                                moreinfoTV.setText(""+ Html.fromHtml(contentStr));
                            } else {
                                moreinfoTV.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (content != null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ScreenInviteFriendsActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    finish();
                }
            }
        });
    }

    private void appShareScreen(String _inviteCode) {
        String shareBody = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "PLAYCER INVITATION CODE");

        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Use my PLAYCER code, "+_inviteCode+", and get "+getResources().getString(R.string.rs)+"50 off your first slot booking. \nInstall the app at "+shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
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
        titleTV.setText("" + getResources().getString(R.string.nav_item_Refferal));

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


