package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.adapters.ClubsListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.CartDataTable;
import in.playcer.model.ClubsListData;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class ClubsActivity extends Activity implements AdapterView.OnItemClickListener {
    public ClubsListAdapter mAdapter;
    public ArrayList<ClubsListData> mClubsListDataListData = new ArrayList<ClubsListData>();
    ListView clubsListviewLV;
    String singleSportURL = null;
    String singleSportName = "";
    String sportSlug = "";
    int MainSingleSportSlotsListCount = 0;
    private ProgressWheel progressWheel_CENTER;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(this);

    TextView noClubsAvailableTV;

    View balanceContainerHeader;
    TextView amountTextHeaderTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clubs_listview);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(ClubsActivity.this, DashboardActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.setDimensions(this);
                    cartItemsInitilization();

                    progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
                    progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
                    progressWheel_CENTER.setRimColor(Color.LTGRAY);

                    noClubsAvailableTV = (TextView) findViewById(R.id.noClubsAvailableTVID);

                    clubsListviewLV = (ListView) findViewById(R.id.clubsListviewLVID);
                    clubsListviewLV.setOnItemClickListener(ClubsActivity.this);

                    singleSportURL = dataBundle.getString(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL);
                    singleSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    sportSlug = dataBundle.getString(Utility.KEY_SLUG);

                    if (singleSportURL != null) {
                        try {
                            if (Utility.isOnline(this)) {
                                loadClubs();
                            } else {
                                loadingSportsTroubleConnecting();
                            }
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    }
                    setupNavigation();
                }
            }
        }
    }

    private void cartItemsInitilization() {
        amountTextHeaderTV = (TextView) findViewById(R.id.balance_amount);
        balanceContainerHeader = findViewById(R.id.balance_container);
        balanceContainerHeader.setBackgroundResource(R.drawable.ic_add_shopping_cart_white_48dp);
        balanceContainerHeader.getLayoutParams().width = (int) (Utility.screenHeight / 14.0);
        balanceContainerHeader.getLayoutParams().height = (int) (Utility.screenHeight / 14.0);

        cartTotalAmount();

        balanceContainerHeader.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    Intent cartPageTogo = new Intent(ClubsActivity.this, CartPlaceOrdersListActivity.class);
                    cartPageTogo.putExtra(Utility.KEY_FROM_TO_CART, "FROM_CLUBS");
                    startActivity(cartPageTogo);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), ClubsActivity.this);
                }
            }
        });
    }

    private void cartTotalAmount() {
        ArrayList<CartDataTable> cartList = dataBaseHelper.getCartDataTableList();
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
    }

    private void loadClubs() {
        String cookieStr = dataBaseHelper.getRegisteredCookie();
        String cityStr = dataBaseHelper.getCurrentCity();
        RequestParams params = new RequestParams();
        params.put("city", cityStr);
        params.put("cookie", cookieStr);
        getSlotsDetails(singleSportURL, params);
        Log.w("HARI--Clubs URL--", "" + singleSportURL);
        Log.w("HARI--params--", "" + params);
    }

    private void loadingSportsTroubleConnecting() {
        noClubsAvailableTV.setVisibility(View.GONE);
        clubsListviewLV.setVisibility(View.GONE);
        progressWheel_CENTER.setVisibility(View.GONE);
        final LinearLayout noNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);
        noNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noNetLL.setVisibility(View.GONE);
                clubsListviewLV.setVisibility(View.GONE);
                progressWheel_CENTER.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(ClubsActivity.this)) {
                            loadClubs();
                        } else {
                            progressWheel_CENTER.setVisibility(View.GONE);
                            noNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    public void getSlotsDetails(String url, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;
                        /******* Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        jsonResponse = new JSONObject(response);
                        int count = Integer.parseInt(jsonResponse.getString("count"));
                        if (count > 0) {
                            /*****
                             * Returns the value mapped by name if it exists and is a JSONArray.
                             ***/
                            /******* Returns null otherwise. *******/
                            JSONArray jsonMainNode = jsonResponse.getJSONArray("clubs");

                            /*********** Process each JSON Node ************/
                            MainSingleSportSlotsListCount = jsonMainNode.length();

                            if (MainSingleSportSlotsListCount != 0) {
                                mClubsListDataListData = new ArrayList<ClubsListData>();

                                for (int i = 0; i < MainSingleSportSlotsListCount; i++) {
                                    /****** Get Object for each JSON node. ***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    int club_id = Integer.parseInt(jsonChildNode.optString("id").toString());
                                    String _clubID = jsonChildNode.optString("name").toString();

                                    int availability = Integer.parseInt(jsonChildNode.optString("availability").toString());
                                    String address = jsonChildNode.optString("address").toString();
                                    String location = jsonChildNode.optString("location").toString();
                                    String city = jsonChildNode.optString("city").toString();
                                    String phone = jsonChildNode.optString("phone").toString();
                                    String latlng = jsonChildNode.optString("latlng").toString();
                                    mClubsListDataListData.add(new ClubsListData(club_id, _clubID, availability, address, location, city, latlng, phone));

                                    OutputData += "Node : \n\n     " + club_id + " | " + _clubID + " | " + " \n\n ";
                                    Log.i("JSON parse", OutputData);
                                }

                                Log.w("Main", "Main Count = " + MainSingleSportSlotsListCount);

                                mAdapter = new ClubsListAdapter(ClubsActivity.this, mClubsListDataListData);
                                clubsListviewLV.setAdapter(mAdapter);
                                clubsListviewLV.setVisibility(View.VISIBLE);
                                noClubsAvailableTV.setVisibility(View.GONE);
                            } else {
                                noClubsAvailableTV.setVisibility(View.VISIBLE);
                                clubsListviewLV.setVisibility(View.GONE);
                            }
                        } else {
                            noClubsAvailableTV.setVisibility(View.VISIBLE);
                            clubsListviewLV.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        loadingSportsTroubleConnecting();
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        loadingSportsTroubleConnecting();
                    }
                } else {
                    noClubsAvailableTV.setVisibility(View.VISIBLE);
                    clubsListviewLV.setVisibility(View.GONE);
                }
                progressWheel_CENTER.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                progressWheel_CENTER.setVisibility(View.GONE);
                if (content!= null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ClubsActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else {
                    loadingSportsTroubleConnecting();
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mClubsListDataListData.get(position).get_availability() == 1) {
            Intent sportIntent = new Intent(ClubsActivity.this, SportsPickVenueActivity.class);
            sportIntent.putExtra(Utility.KEY_Confirm, "AVAILABLE");
            sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL, singleSportURL);
            sportIntent.putExtra(Utility.KEY_SPORT_NAME, singleSportName);
            sportIntent.putExtra(Utility.KEY_SLUG, sportSlug);
            sportIntent.putExtra(Utility.KEY_pickedClubName, mClubsListDataListData.get(position).get_clubName());
            sportIntent.putExtra(Utility.KEY_pickedClubID, mClubsListDataListData.get(position).get_clubID());
            sportIntent.putExtra(Utility.KEY_address, mClubsListDataListData.get(position).get_address());
            sportIntent.putExtra(Utility.KEY_phone, mClubsListDataListData.get(position).get_phone());
            sportIntent.putExtra(Utility.KEY_latlng, mClubsListDataListData.get(position).get_latlng());
            sportIntent.putExtra(Utility.KEY_location, mClubsListDataListData.get(position).get_location());
            sportIntent.putExtra(Utility.KEY_city, mClubsListDataListData.get(position).get_city());
            startActivity(sportIntent);
        } else {
            Intent sportIntent = new Intent(ClubsActivity.this, SlotConfirmationUnAvaiActivity.class);
            sportIntent.putExtra(Utility.KEY_Confirm, "UNAVAILABLE");
            sportIntent.putExtra(Utility.KEY_pickedClubName, mClubsListDataListData.get(position).get_clubName());
            sportIntent.putExtra(Utility.KEY_SPORT_NAME, singleSportName);
            sportIntent.putExtra(Utility.KEY_pickedDate, "");
            sportIntent.putExtra(Utility.KEY_phone, mClubsListDataListData.get(position).get_phone());
            sportIntent.putExtra(Utility.KEY_address, mClubsListDataListData.get(position).get_address());
            sportIntent.putExtra(Utility.KEY_latlng, mClubsListDataListData.get(position).get_latlng());
            sportIntent.putExtra(Utility.KEY_location, mClubsListDataListData.get(position).get_location());
            sportIntent.putExtra(Utility.KEY_city, mClubsListDataListData.get(position).get_city());
            startActivity(sportIntent);
        }

        MyApplication.getInstance().trackEvent("Facility click", "Facility click", "Facility : "+mClubsListDataListData.get(position).get_clubName());
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

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        titleTV.setText("PLAYCES");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.VISIBLE);
        subTitleTV.setText(""+singleSportName.toString().toUpperCase());

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
        MyApplication.getInstance().trackScreenView("Facilities");

        cartTotalAmount();
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