package in.playcer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.playcer.ui.widgets.CitrusTextView;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 9/14/2015.
 */
public class SuccessfullySlotConfirmedActivity extends FragmentActivity {
    /*String PickedCourtName = "";
    String pickedSportName = "";
    String PickedDate = "";
    String PickedSlotTime = "";
    String pickedCourtID = "";
    String location = "";
    String city = "";
   */

    int bookingID = -1;
    String availability = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_result_manager);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);

        TextView transaction_id_label = (TextView) findViewById(R.id.transaction_id_label);
        TextView bookingIDTV = (TextView) findViewById(R.id.transaction_id_text);

        TextView amount_paid_label = (TextView) findViewById(R.id.amount_paid_label);
        amount_paid_label.setVisibility(View.GONE);

        TextView amount_paid_text = (TextView) findViewById(R.id.amount_paid_text);
        amount_paid_text.setVisibility(View.GONE);

        ImageView paymentResultImage = (ImageView) findViewById(R.id.payment_result_image);

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);

        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                availability = dataBundle.getString("Confirm");
                if (availability.equalsIgnoreCase("AVAILABLE")) {
                    bookingID = dataBundle.getInt("booking_id");

                    titleTV.setText("CONFIRMED");
                    bookingIDTV.setText("#"+bookingID);
                } else {
                    paymentResultImage.setImageResource(R.drawable.img_cross_red);
                    titleTV.setText("UNAVAILABLE");
                    CitrusTextView thankYouTV = (CitrusTextView) findViewById(R.id.payment_result_text);
                    TextView confirmedBookingTV = (TextView) findViewById(R.id.confirmedBookingTVID);
                    transaction_id_label.setVisibility(View.GONE);
                    bookingIDTV.setVisibility(View.GONE);

                    thankYouTV.setText("Sorry!");
                    confirmedBookingTV.setText("Slot is unavailable.");
                }
            }
        }
        setupNavigation();
    }

    public void setupNavigation() {
        {
            RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
            headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);
            RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
            backAllRL.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (availability.equalsIgnoreCase("AVAILABLE")) {
                        Intent bookingDoneIntent = new Intent(SuccessfullySlotConfirmedActivity.this, SlotConfirmationManagerActivity.class);
                        bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        bookingDoneIntent.putExtra("EXIT", true);
                        startActivity(bookingDoneIntent);
                    } else {
                        onBackPressedAnimationByCHK();
                    }
                }
            });

            TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
            subTitleTV.setVisibility(View.GONE);

            Button backBtn = (Button) findViewById(R.id.backBtnID);
            backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
            backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
            backBtn.setVisibility(View.VISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (availability.equalsIgnoreCase("AVAILABLE")) {
                        Intent bookingDoneIntent = new Intent(SuccessfullySlotConfirmedActivity.this, SlotConfirmationManagerActivity.class);
                        bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        bookingDoneIntent.putExtra("EXIT", true);
                        startActivity(bookingDoneIntent);
                    } else {
                        onBackPressedAnimationByCHK();
                    }
                }
            });

            Button moreBtn = (Button) findViewById(R.id.moreBtnID);
            moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
            moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (availability.equalsIgnoreCase("AVAILABLE")) {
                Intent bookingDoneIntent = new Intent(SuccessfullySlotConfirmedActivity.this, SlotConfirmationManagerActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                onBackPressedAnimationByCHK();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Tracking the screen view
        MyApplication.getInstance().trackScreenView("Manager Slot Confirmed Successful");
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}