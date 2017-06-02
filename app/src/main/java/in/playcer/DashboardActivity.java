package in.playcer;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.playcer.adapters.DashboardTabPagerAdapter;
import in.playcer.adapters.CitiesListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.CitiesListItem;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

public class DashboardActivity extends AppCompatActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    @Bind(R.id.navigation_view)NavigationView navigationView;
    @Bind(R.id.drawer_layout)DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)Toolbar toolbar;
    @Bind(R.id.tab_layout)TabLayout tabLayout;
    @Bind(R.id.view_pager)ViewPager viewPager;

    private ActionBarDrawerToggle drawerToggle;

    // Cities
    LinearLayout citiesSelectionRelativeRL;
    ListView citiesListView;
    ArrayList<CitiesListItem> citiesList = new ArrayList<CitiesListItem>();
    //Dialog citiesDialog;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(this);

    TextView currentCityTVID;
    CitiesListAdapter Adapter;
    public static  TextView managerClubNameTV;

    private ProgressWheel progressWheel_CENTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_main);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        ButterKnife.bind(this);
        Utility.setDimensions(this);

        try{
            setSupportActionBar(toolbar);
            final ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

            citiesInitiateHere();

            initNavigationView();
            initTabLayout();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void citiesInitiateHere() {
        progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);
        citiesSelectionRelativeRL = (LinearLayout) findViewById(R.id.citiesSelectionRelativeRLID);
        if (getIntent() != null && getIntent().getBooleanExtra("EXIT", false)) {
            if (getIntent().getExtras().getString("EXIT_AUTH_FAILED") != null && getIntent().getExtras().getString("EXIT_AUTH_FAILED").equalsIgnoreCase("authfailed")) {
                Utility.showCustomToast(getResources().getString(R.string.your_session_expire), DashboardActivity.this);
                Intent bookingDoneIntent = new Intent(DashboardActivity.this, VerificationExistingActivity.class);
                startActivity(bookingDoneIntent);
                finish();
            } else {
                Intent bookingDoneIntent = new Intent(DashboardActivity.this, DashboardActivity.class);
                bookingDoneIntent.putExtra("BOOKED", true);
                startActivity(bookingDoneIntent);
                finish();
            }
        } else {
            String role = dataBaseHelper.getRegisteredRole();
            if (role != null && role.equalsIgnoreCase("manager")) {
               // managerClubNameRL.setVisibility(View.VISIBLE);
                citiesSelectionRelativeRL.setVisibility(View.GONE);
            } else {
                if (dataBaseHelper.getCurrentCity() != null && !dataBaseHelper.getCurrentCity().equalsIgnoreCase("empty")) {
                    citiesSelectionRelativeRL.setVisibility(View.GONE);
                } else {
                    displayCitiesView();
                }
            }
        }
    }

    private void displayCitiesView() {
        citiesSelectionRelativeRL.setVisibility(View.VISIBLE);
        currentCityTVID = (TextView) findViewById(R.id.currentCityTVID);
        RelativeLayout chooseCityRL = (RelativeLayout) findViewById(R.id.chooseCityRLID);
        chooseCityRL.getLayoutParams().height = (int) (Utility.screenWidth / 5.1);
        Button closeBtn = (Button) findViewById(R.id.closeBtnID);
        if (dataBaseHelper.getCurrentCity() != null && !dataBaseHelper.getCurrentCity().equalsIgnoreCase("empty")) {
            closeBtn.setVisibility(View.VISIBLE);
        } else {
            closeBtn.setVisibility(View.GONE);
        }
        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                citiesSelectionRelativeRL.setVisibility(View.GONE);
            }
        });

        citiesListView = (ListView) findViewById(R.id.listView1);
        citiesListView.setOnItemClickListener(this);
        citiesListView.setVisibility(View.VISIBLE);

        // } else {
        if (citiesList.size() > 0) {
            //citiesDialog.show();
            citiesSelectionRelativeRL.setVisibility(View.VISIBLE);
            Adapter = new CitiesListAdapter(DashboardActivity.this, citiesList);
            citiesListView.setAdapter(Adapter);
            Adapter.notifyDataSetChanged();
            progressWheel_CENTER.setVisibility(View.GONE);
        } else {
            if (Utility.isOnline(DashboardActivity.this)) {
                getCitiesRequest(AppConstants.GET_CITIES_LIST_URL);
            } else {
                Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), DashboardActivity.this);
            }
        }
        //}

        if (dataBaseHelper.getCurrentCity().equalsIgnoreCase("empty")) {
            currentCityTVID.setVisibility(View.GONE);
        } else {
            currentCityTVID.setVisibility(View.VISIBLE);
            currentCityTVID.setText("You're in " + dataBaseHelper.getCurrentCity());
        }

        MyApplication.getInstance().trackEvent("Select City", "Select City Tapped", "Selecting City");
    }

    public void getCitiesRequest(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;
                        try {
                            /******* Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            /*****
                             * Returns the value mapped by name if it exists and is a JSONArray.
                             ***/
                            /******* Returns null otherwise. *******/
                            JSONArray jsonMainNode = jsonResponse.getJSONArray("cities");

                            /*********** Process each JSON Node ************/
                            citiesList = new ArrayList<CitiesListItem>();

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                /****** Get Object for each JSON node. ***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                String city_name = jsonChildNode.optString("city").toString();

                                citiesList.add(new CitiesListItem(city_name));

                                OutputData += "Node : \n\n     " + city_name + " | " + " \n\n ";
                                Log.i("JSON parse", OutputData);
                            }

                            Log.w("Main", "Main Count = " + citiesList.size());
                            if (citiesList.size() > 0) {
                                //citiesDialog.show();
                                Adapter = new CitiesListAdapter(DashboardActivity.this, citiesList);
                                citiesListView.setAdapter(Adapter);
                                Adapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                    }
                }
                progressWheel_CENTER.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                progressWheel_CENTER.setVisibility(View.GONE);
                if (statusCode == 404) {
                    Utility.showCustomToast(getResources().getString(R.string.request_not_found), DashboardActivity.this);
                } else if (statusCode == 500) {
                    Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), DashboardActivity.this);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.unexpected_error), DashboardActivity.this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView arg0, View v, int position, long arg3) {
        dataBaseHelper.addCityToDB(citiesList.get(position).get_cityName());
        //Utility.showCustomToast("You're in " + dataBaseHelper.getCurrentCity() + "", DashboardActivity.this);
        Utility.showCustomToast(getResources().getString(R.string.your_in) + dataBaseHelper.getCurrentCity() + "", DashboardActivity.this);
        //citiesDialog.dismiss();
        citiesSelectionRelativeRL.setVisibility(View.GONE);
        Intent bookingDoneIntent = new Intent(DashboardActivity.this, DashboardActivity.class);
        startActivity(bookingDoneIntent);
        finish();
    }

    private void initTabLayout() {
        DashboardTabPagerAdapter adapter = new DashboardTabPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initNavigationView() {
        managerClubNameTV = (TextView) findViewById(R.id.userName);
        TextView numberTV = (TextView) findViewById(R.id.number);
        String role = dataBaseHelper.getRegisteredRole();
        if (role != null && role.equalsIgnoreCase("manager")) {
            TextView clubNameTVID = (TextView) findViewById(R.id.clubNameTVID);
            clubNameTVID.setVisibility(View.VISIBLE);
            clubNameTVID.setText("" + dataBaseHelper.getRegisteredMobileNumber());
            numberTV.setText("" + dataBaseHelper.getRegisteredEmailID());

            navigationView.inflateMenu(R.menu.drawer_manager);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // menuItem.setChecked(true);
                    drawerLayout.closeDrawers();

                    int itemId = menuItem.getItemId();
                    switch (itemId) {
                        case R.id.nav_home:
                            return true;
                        case R.id.nav_bookings:
                            startActivity(new Intent(DashboardActivity.this, MyBookingsEventsSlotsActivity.class));
                            return true;
                        case R.id.nav_email:
                            emailSendingPostion("");
                            return true;
                        case R.id.nav_whatsapp:
                            whatsAppsConnect("");
                            return true;
                        case R.id.nav_share_app:
                            appShareScreen("");
                            return true;
                        case R.id.nav_rate_app:
                            rateThisApp("");
                            return true;
                    }
                    return false;
                }
            });

        } else {
            numberTV.setText("" + dataBaseHelper.getRegisteredMobileNumber());
            managerClubNameTV.setText("" + dataBaseHelper.getRegisteredEmailID());

            navigationView.inflateMenu(R.menu.drawer);

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // menuItem.setChecked(true);
                    drawerLayout.closeDrawers();

                    int itemId = menuItem.getItemId();
                    switch (itemId) {
                        case R.id.nav_home:
                            return true;
                        case R.id.nav_bookings:
                            startActivity(new Intent(DashboardActivity.this, MyBookingsEventsSlotsActivity.class));
                            return true;
                        case R.id.nav_refferal:
                            startActivity(new Intent(DashboardActivity.this, ScreenInviteFriendsActivity.class));
                            return true;
                        case R.id.nav_plycer_credits:
                            startActivity(new Intent(DashboardActivity.this, ScreenPlaycerCreditsActivity.class));
                            return true;
                        case R.id.nav_email:
                            emailSendingPostion("");
                            return true;
                        case R.id.nav_whatsapp:
                            whatsAppsConnect("");
                            return true;
                        case R.id.nav_share_app:
                            appShareScreen("");
                            return true;
                        case R.id.nav_rate_app:
                            rateThisApp("");
                            return true;
                    }
                    return false;
                }
            });
        }

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void rateThisApp(String title) {
         /* This code assumes you are inside an activity */
        final Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);

        if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
            startActivity(rateAppIntent);
        } else {
            /* handle your error case: the device has no way to handle market urls */
        }
    }

    private void appShareScreen(String title) {
        String shareBody = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "PLAYCER (Open it in Google Play Store to Download the Application)");

        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void whatsAppsConnect(String title) {
       try {
           Uri mUri = Uri.parse("smsto:+919845812841");
           Intent waIntent = new Intent(Intent.ACTION_SENDTO, mUri);
           waIntent.setPackage("com.whatsapp");
           if (waIntent != null) {
               waIntent.putExtra("sms_body", "");
               waIntent.putExtra("chat", true);
               startActivity(waIntent);
           } else {
               Utility.showCustomToast(getResources().getString(R.string.whatsapp_not_found), DashboardActivity.this);
           }
       } catch (Exception ee){
           ee.printStackTrace();
           Utility.showCustomToast(getResources().getString(R.string.pblm_to_open_whatsapp), DashboardActivity.this);
       }
    }

    private void emailSendingPostion(String title) {
        sendEmail();
    }

    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {"support@playcer.in"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DashboardActivity.this, getResources().getString(R.string.there_is_no_email), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.citiesSelection:
                displayCitiesView();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        String role = dataBaseHelper.getRegisteredRole();
        if (role != null && role.equalsIgnoreCase("manager")) {
            menu.removeItem(R.id.citiesSelection);
        }
        return true;
    }

   /* */
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     *//*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.citiesSelection:
                displayCitiesView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (citiesSelectionRelativeRL.getVisibility() == View.VISIBLE) {
            if (dataBaseHelper.getCurrentCity().equalsIgnoreCase("empty")) {
                Utility.showCustomToast(getResources().getString(R.string.pls_select_city),DashboardActivity.this);
            } else {
                citiesSelectionRelativeRL.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }
}