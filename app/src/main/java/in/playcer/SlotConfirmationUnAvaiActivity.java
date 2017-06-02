package in.playcer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SlotConfirmationUnAvaiActivity extends FragmentActivity {
    GoogleMap mGoogleMap;

    ProgressDialog ringProgressDialog;

    private TextView amountText;
    public LinearLayout balanceContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slots_unavailable);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);

        amountText = (TextView) findViewById(R.id.balance_amount);
        amountText.setBackgroundResource(R.drawable.amount_background);
        balanceContainer = (LinearLayout) findViewById(R.id.balance_container);
        amountText.setVisibility(View.GONE);
        balanceContainer.setVisibility(View.GONE);

        //TextView pickedCourtTV = (TextView) findViewById(R.id.clubNameTVID);
        TextView sportNameTV = (TextView) findViewById(R.id.sportNameTVID);
        TextView pickeddateTV = (TextView) findViewById(R.id.pickeddateTVID);
        TextView PickedSlotTimeTV = (TextView) findViewById(R.id.PickedSlotTimeTVID);
        TextView addressTVID = (TextView) findViewById(R.id.fullAddressTVID);

        TextView locationTV = (TextView) findViewById(R.id.locationTVID);
        TextView cityTV = (TextView) findViewById(R.id.cityTVID);
        TextView titleTV = (TextView) findViewById(R.id.titleTVID);

        Button callBtn = (Button) findViewById(R.id.callBtnID);
        callBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
        callBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        callBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + Utility.number));
                startActivity(callIntent);
                MyApplication.getInstance().trackEvent("Call Facility", "Call Button Tapped", "Facility : Call Facility " );
            }
        });

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(SlotConfirmationUnAvaiActivity.this, SingleSportSlotBookingActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    Utility.availability = dataBundle.getString(Utility.KEY_Confirm);
                    Utility.PickedCourtName = dataBundle.getString(Utility.KEY_pickedClubName);
                    Utility.pickedSportName = dataBundle.getString(Utility.KEY_SPORT_NAME);
                    Utility.PickedDate = dataBundle.getString(Utility.KEY_pickedDate);
                    Utility.address = dataBundle.getString(Utility.KEY_address);
                    Utility.latLng = dataBundle.getString(Utility.KEY_latlng);
                    Utility.location = dataBundle.getString(Utility.KEY_location);
                    Utility.city = dataBundle.getString(Utility.KEY_city);
                    Utility.number = dataBundle.getString(Utility.KEY_phone);

                    if (Utility.availability.equalsIgnoreCase("UNAVAILABLE")) {
                        PickedSlotTimeTV.setVisibility(View.GONE);
                        titleTV.setText("PLAYCES");
                        balanceContainer.setVisibility(View.GONE);
                    } else {
                        balanceContainer.setVisibility(View.VISIBLE);
                        PickedSlotTimeTV.setVisibility(View.VISIBLE);
                        Utility.pickedCourtID = dataBundle.getInt("pickedCourtID");
                        Utility.PickedSlotTime = dataBundle.getString("PickedSlotTime");
                        PickedSlotTimeTV.setText("" + Utility.PickedSlotTime);
                        titleTV.setText("CONFIRM");
                        //showAmountByHeader(payAmount);
                    }

                    if (Utility.PickedCourtName != null) {
                        //pickedCourtTV.setText("" + Utility.PickedCourtName);
                    } else {
                        //pickedCourtTV.setVisibility(View.GONE);
                    }

                    if (Utility.number != null && Utility.number.trim().length() > 3) {
                        callBtn.setVisibility(View.VISIBLE);
                    } else {
                        callBtn.setVisibility(View.GONE);
                    }

                    //sportNameTV.setText("" + Utility.pickedSportName.toString().toUpperCase());
                    sportNameTV.setText("" + Html.fromHtml(Utility.PickedCourtName.toString()));
                    pickeddateTV.setText("" + Html.fromHtml(Utility.PickedDate));
                    addressTVID.setText("" + Html.fromHtml(Utility.address));
                    locationTV.setText("" + Html.fromHtml(Utility.location));
                    cityTV.setText("" + Html.fromHtml(Utility.city));

                    if (Utility.latLng != null) {
                        if (Utility.latLng.trim().length() > 0) {
                           try {
                               Utility.latLng = Utility.latLng.replace(" ", "");
                               String[] parts1 = Utility.latLng.split(",");
                               String lat = parts1[0];
                               String lng = parts1[1];
                               double latL = Double.parseDouble(lat);
                               double lngL = Double.parseDouble(lng);
                               setUpMap(latL, lngL);
                           } catch (IndexOutOfBoundsException e){
                               e.printStackTrace();
                           } catch (Exception e) {
                               e.printStackTrace();
                           }
                        }
                    }
                    setupNavigation();
                }
            }
        }
    }

    private void setUpMap(double _lat, double _lng) {
        try {
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) SlotConfirmationUnAvaiActivity.this.getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            mGoogleMap = fm.getMap();

            // Enabling MyLocation in Google Map
            mGoogleMap.setMyLocationEnabled(false);

            gotoCurrentPlacePin(12, _lat, _lng);

            LatLng latLng1 = new LatLng(_lat, _lng);
            MarkerOptions entryMarkerOpt = new MarkerOptions().position(latLng1).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_book));
            mGoogleMap.addMarker(entryMarkerOpt);
            mGoogleMap.getUiSettings().setCompassEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void gotoCurrentPlacePin(float zoom, double currentMapLati, double currentMapLongi) {
        LatLng latLong = new LatLng(currentMapLati, currentMapLongi);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(zoom).tilt(20).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        subTitleTV.setVisibility(View.VISIBLE);
        subTitleTV.setText("" + Html.fromHtml(Utility.pickedSportName.toString().toUpperCase()));

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
        MyApplication.getInstance().trackScreenView("Facility");
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