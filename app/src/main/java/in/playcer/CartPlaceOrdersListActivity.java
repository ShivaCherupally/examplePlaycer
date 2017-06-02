package in.playcer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import in.playcer.libs.ProgressWheel;
import in.playcer.model.CartDataTable;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by OFFICE on 1/25/2016.
 */
public class CartPlaceOrdersListActivity extends Activity {
    public CartPlaceOrdersListAdapter mAdapter;
    ListView cartListviewLV;

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(CartPlaceOrdersListActivity.this);
    ArrayList<CartDataTable> cartList;
    TextView noCartListAvailableTV;
    private ProgressWheel progressWheel_CENTER;

    int count = 0;
    View balanceContainerHeader;
    TextView amountTextHeaderTV;
    ProgressDialog ringProgressDialog;

    TextView slotPriceAmountTV;
    TextView totalAmountTV;
    int fee = 0;
    int total = 0;

    TextView slotPriceAmountLabelTV;
    TextView feeAmountLabelTV;
    TextView feeAmountTV;

    RelativeLayout feeLableRL;
    RelativeLayout totalLableRL;
    View viewLineTV;

    //String sendMultipleCartsData = "";
    TextView haveCouponTVID; // Have a Coupon Code
    Button deleteCouponBtnID;  // Delete Coupon Amount
    String couponCodeAmountApplied = "";   // Subtracting Coupon Code
    //String finalAmount = "";        // Final Amount After Coupon Code Applied

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_place_order_list_screen);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);

        noCartListAvailableTV = (TextView) findViewById(R.id.noCartListAvailableTVID);
        cartListviewLV = (ListView) findViewById(R.id.cartListViewLVID);

        progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);

        cartList = dataBaseHelper.getCartDataTableList();

        amountTextHeaderTV = (TextView) findViewById(R.id.balance_amount);
        balanceContainerHeader = findViewById(R.id.balance_container);

        balanceContainerHeader.setVisibility(View.VISIBLE);
        amountTextHeaderTV.setVisibility(View.VISIBLE);
        amountTextHeaderTV.setTextColor(getResources().getColor(R.color.white));
        amountTextHeaderTV.setBackgroundResource(R.drawable.amount_background_cart_total);

        slotPriceAmountTV = (TextView) findViewById(R.id.slotPriceAmountTVID);
        totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

        slotPriceAmountLabelTV = (TextView) findViewById(R.id.slotPriceAmountLabelTVID);
        feeAmountLabelTV = (TextView) findViewById(R.id.feeAmountLabelTVID);
        feeAmountTV = (TextView) findViewById(R.id.feeAmountTVID);

        feeLableRL = (RelativeLayout) findViewById(R.id.feeLableRLID);
        totalLableRL = (RelativeLayout) findViewById(R.id.totalLableRLID);
        viewLineTV = (View) findViewById(R.id.viewLineTVID);

        haveCouponTVID = (TextView) findViewById(R.id.haveCouponTVID);
        deleteCouponBtnID = (Button) findViewById(R.id.deleteCouponBtnID);
        deleteCouponBtnID.setVisibility(View.GONE);


        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();
            if (dataBundle != null) {
                if (getIntent().getBooleanExtra("EXIT", false)) {
                    Intent bookingDoneIntent = new Intent(CartPlaceOrdersListActivity.this, SingleSportSlotBookingActivity.class);
                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bookingDoneIntent.putExtra("EXIT", true);
                    startActivity(bookingDoneIntent);
                    onBackPressedAnimationByCHK();
                } else {
                    String FROM_ = dataBundle.getString(Utility.KEY_FROM_TO_CART);
                }
                try {
                    couponCodeAmountApplied = dataBundle.getString("AFTER_APPLIED_COUPON_CODE");
                    total = dataBundle.getInt("TOTAL_AMOUNT");
                    if (couponCodeAmountApplied != null) {
                        haveCouponTVID.setText("APPLIED"); //Coupon Applied
                        haveCouponTVID.setBackgroundResource(R.drawable.buttonbackground);
                        slotPriceAmountTV.setText(getResources().getString(R.string.rs) + " " + couponCodeAmountApplied);
                        deleteCouponBtnID.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        Utility.VALUE_convenience = dataBaseHelper.getConvenienceFeeDB();
        Utility.VALUE_sclabel = dataBaseHelper.getconvenienceLabelDB();

        try {
            fee = Integer.parseInt(Utility.VALUE_convenience);
        } catch (NumberFormatException ee) {
            ee.printStackTrace();
        } catch (Exception eee) {
            eee.printStackTrace();
        }

        slotPriceAmountLabelTV.setText("SUBTOTAL");

        feeAmountTV.setText("+ " + getString(R.string.rs) + "" + fee);
        feeAmountLabelTV.setText("" + Utility.VALUE_sclabel.toUpperCase());

        //totalAmountTV.setText(""+getString(R.string.rs)+""+total);
        if (fee <= 0) {
            feeLableRL.setVisibility(View.GONE);
            totalLableRL.setVisibility(View.GONE);
            viewLineTV.setVisibility(View.GONE);
            //totalAmountTV.setText("" + getString(R.string.rs) + "" + price);
            slotPriceAmountLabelTV.setText("TOTAL");
        }

        if (cartList.size() > 0) {
            mAdapter = new CartPlaceOrdersListAdapter(CartPlaceOrdersListActivity.this, cartList);
            cartListviewLV.setAdapter(mAdapter);
            progressWheel_CENTER.setVisibility(View.GONE);
            for (int i = 0; i < cartList.size(); i++) {
                int cc = Integer.parseInt(cartList.get(i).get_cartSlotPrice());
                count = count + cc;
            }
            total = fee + count;
            amountTextHeaderTV.setText(getResources().getString(R.string.rs) + " " + total);
            totalAmountTV.setText("" + getString(R.string.rs) + " " + total);
            //Condition Checking  Coupon Code Applied or not
            try {
                if (couponCodeAmountApplied == null) {
                    slotPriceAmountTV.setText("" + getString(R.string.rs) + " " + count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            noCartListAvailableTV.setVisibility(View.VISIBLE);
            progressWheel_CENTER.setVisibility(View.GONE);
            amountTextHeaderTV.setText(getResources().getString(R.string.rs) + " " + total);
        }

        haveCouponTVID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        deleteCouponBtnID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveCouponTVID.setText("Have a Coupon Code?"); //applied
               // haveCouponTVID.setBackgroundResource(R.drawable.textlines);
                slotPriceAmountTV.setText(getResources().getString(R.string.rs) + " " + total);
                deleteCouponBtnID.setVisibility(View.GONE);
            }
        });

        Button confirmBtn = (Button) findViewById(R.id.checkOutBtnID);
        confirmBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        confirmBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cartList = dataBaseHelper.getCartDataTableList();
                if (cartList.size() > 0) {
                    Intent in = new Intent(CartPlaceOrdersListActivity.this, ScreenCheckoutActivity.class);
                    //in.putExtra("CART_AMOUNT", count);  //Pay Amount
                    in.putExtra("CART_AMOUNT", total);  //Sub Total
                    in.putExtra("CONVENIENCE_FEE", fee); //Tax or Other
                    //in.putExtra("CONVENIENCE_FEE", 30); //Tax or Other
                    in.putExtra("BOOKING_TYPE", "slots");   // // Booking Type
                   /// in.putExtra("COUPON_CODE_AMOUNT", "0");   // Coupon Code
                    in.putExtra("CREDITS", "0");   // Credits
                    in.putExtra("EVENT_ID", "");
                    in.putExtra("EVENT_DATE", "");
                    startActivity(in);
                } else{
                    Utility.showCustomToast(getResources().getString(R.string.your_cart_empty), CartPlaceOrdersListActivity.this);
                }
            }
        });
        setupNavigation();
    }

    public class CartPlaceOrdersListAdapter extends BaseAdapter {
        ArrayList<CartDataTable> mCartListData;
        private LayoutInflater layoutInflater = null;
        Context mContext;

        public CartPlaceOrdersListAdapter(Context context, ArrayList<CartDataTable> _ClubsListData) {
            this.mContext = context;
            this.mCartListData = _ClubsListData;
            layoutInflater = LayoutInflater.from(context);//7331167946
        }

        @Override
        public int getCount() {
            return mCartListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_items_row_cart_place_order_list, null, false);

                holder.cardView = (RelativeLayout) convertView.findViewById(R.id.sportsCardCVID);

                holder.courtNameTVID = (TextView) convertView.findViewById(R.id.courtNameTVID);

                holder.sportNameTV = (TextView) convertView.findViewById(R.id.sportNameTVID);

                holder.slotDateTV = (TextView) convertView.findViewById(R.id.slotDateTVID);

                holder.slotTimeTV = (TextView) convertView.findViewById(R.id.slotTimeTVID);

                holder.slotPriceTV = (TextView) convertView.findViewById(R.id.slotPriceTVID);

                holder.deleteCartItemBtn = (Button) convertView.findViewById(R.id.deleteCartItemBtnID);

                // This will now execute only for the first time of each row
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.courtNameTVID.setText("" + Html.fromHtml(mCartListData.get(position).get_cartCourtName()) + "-" + Html.fromHtml(mCartListData.get(position).get_cartSportName()));
            holder.sportNameTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartSportName()));
            holder.slotDateTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartDate()) + ", " + Html.fromHtml(mCartListData.get(position).get_cartSlotTime()));
            holder.slotTimeTV.setText("" + Html.fromHtml(mCartListData.get(position).get_cartSlotTime()));
            holder.slotPriceTV.setText(getResources().getString(R.string.rs) + " " + Html.fromHtml(mCartListData.get(position).get_cartSlotPrice()));

            switch (position % 2) {
                case 0:
                    holder.cardView.setBackgroundResource(R.color.row1);
                    break;
                case 1:
                    holder.cardView.setBackgroundResource(R.color.white);
                    break;
            }

            holder.deleteCartItemBtn.setFocusable(false);
            holder.deleteCartItemBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    count = 0;
                    total = 0;
                    dataBaseHelper.deleteCartItem(mCartListData.get(position).get_cartDate(), mCartListData.get(position).get_cartSlotTime());

                    cartList = dataBaseHelper.getCartDataTableList();
                    mAdapter = null;
                    mAdapter = new CartPlaceOrdersListAdapter(CartPlaceOrdersListActivity.this, cartList);
                    cartListviewLV.setAdapter(mAdapter);
                    progressWheel_CENTER.setVisibility(View.GONE);

                    for (int i = 0; i < cartList.size(); i++) {
                        int cc = Integer.parseInt(cartList.get(i).get_cartSlotPrice());
                        count = count + cc;
                    }

                    total = fee + count;
                    slotPriceAmountTV.setText("" + getString(R.string.rs) + " " + count);
                    amountTextHeaderTV.setText(getResources().getString(R.string.rs) + " " + total);
                    totalAmountTV.setText("" + getString(R.string.rs) + " " + total);

                    if (cartList.size() == 0) {
                        total = 0;
                        noCartListAvailableTV.setVisibility(View.VISIBLE);
                        progressWheel_CENTER.setVisibility(View.GONE);
                        amountTextHeaderTV.setText(getResources().getString(R.string.rs) + " " + total);
                        amountTextHeaderTV.setVisibility(View.GONE);
                        feeLableRL.setVisibility(View.GONE);
                        totalLableRL.setVisibility(View.GONE);
                        viewLineTV.setVisibility(View.GONE);
                        //totalAmountTV.setText("" + getString(R.string.rs) + "" + price);
                        slotPriceAmountLabelTV.setText("TOTAL");
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            RelativeLayout cardView;
            TextView courtNameTVID;//courtTitleTV, clubNameTV;
            TextView sportNameTV;
            TextView slotDateTV, slotTimeTV, slotPriceTV;
            Button deleteCartItemBtn;
        }
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
        titleTV.setText("YOUR ORDER");

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