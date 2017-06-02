package in.playcer.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.citrus.sdk.TransactionResponse;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;

import in.playcer.MyApplication;
import in.playcer.R;
import in.playcer.ui.events.FragmentCallbacks;
import in.playcer.ui.utils.ResultModel;
import in.playcer.ui.widgets.CitrusTextView;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;


public class ResultFragment extends Fragment {
    private static final String ARG_IS_SUCCESS = "is_success";
    private static final String ARG_FROM_EVENTS_OR_SLOTS = "from_events_or_slots";
    private static final String ARG_RESULT = "result";
    private static final String TAG = "ResultFragment$";
    ImageView paymentResultImage;
    CitrusTextView paymentResultText;
    TextView transactionIdLabel;
    TextView transactionIdText;
    TextView amountPaidLabel;
    TextView amountPaidText;
    boolean success = false;
    ResultModel resultModel;
    String value_from_events_or_slots;
    private FragmentCallbacks mListener;
    ProgressDialog ringProgressDialog;

    TextView bookingIDTV;
    TextView pickedCourtTV;
    TextView sportNameTV;
    TextView  pickeddateTV;
    TextView PickedSlotTimeTV;
    TextView locationTV;
    TextView cityTV;
    public static boolean isSuccessORFailure = false;

    public static ResultFragment newInstance(boolean success, ResultModel resultModel, String _from_Events_or_slots) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_SUCCESS, success);
        args.putParcelable(ARG_RESULT, resultModel);
        args.putString(ARG_FROM_EVENTS_OR_SLOTS, _from_Events_or_slots);
        fragment.setArguments(args);
        return fragment;
    }

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            success = getArguments().getBoolean(ARG_IS_SUCCESS);
            resultModel = getArguments().getParcelable(ARG_RESULT);
            value_from_events_or_slots = getArguments().getString(ARG_FROM_EVENTS_OR_SLOTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener.toggleAmountVisibility(View.GONE);
        View layout = inflater.inflate(R.layout.fragment_result, container, false);

        bookingIDTV = (TextView) layout.findViewById(R.id.bookingIDTVID);
        pickedCourtTV = (TextView) layout.findViewById(R.id.clubNameTVID);
        sportNameTV = (TextView) layout.findViewById(R.id.sportNameTVID);
        pickeddateTV = (TextView) layout.findViewById(R.id.pickeddateTVID);
        PickedSlotTimeTV = (TextView) layout.findViewById(R.id.PickedSlotTimeTVID);
        locationTV = (TextView) layout.findViewById(R.id.locationTVID);
        cityTV = (TextView) layout.findViewById(R.id.cityTVID);

        paymentResultImage = (ImageView) layout.findViewById(R.id.payment_result_image);
        paymentResultText = (CitrusTextView) layout.findViewById(R.id.payment_result_text);
        transactionIdLabel = (TextView) layout.findViewById(R.id.transaction_id_label);
        transactionIdText = (TextView) layout.findViewById(R.id.transaction_id_text);
        amountPaidLabel = (TextView) layout.findViewById(R.id.amount_paid_label);
        amountPaidText = (TextView) layout.findViewById(R.id.amount_paid_text);

        TextView confirmedBookingTV = (TextView) layout.findViewById(R.id.confirmedBookingTVID);

        if (!resultModel.isWithdraw()) {
            if (resultModel.getTransactionResponse() != null) {
                if (resultModel.getTransactionResponse().getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    isSuccessORFailure = true;
                    paymentResultImage.setImageResource(R.drawable.img_checkmark_green);
                    paymentResultText.setText(getString(R.string.text_payment_success));
                    transactionIdLabel.setText(getString(R.string.text_transaction_id));
                    amountPaidLabel.setText(getString(R.string.text_amount_paid));
                    confirmedBookingTV.setText("Your booking is confirmed.");
                    amountPaidText.setText(getString(R.string.rs)+resultModel.getTransactionResponse().getTransactionAmount().getValue());
                    transactionIdText.setText("" + Utility.ORDER_ID);

                    orderCompletedDetails("Completed");

                    // Tracking the screen view
                    MyApplication.getInstance().trackScreenView("Payment Successful");
                }else{
                    isSuccessORFailure = false;
                    paymentResultImage.setImageResource(R.drawable.img_cross_red);
                    paymentResultText.setText(getString(R.string.text_payment_failure));
                    transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                    amountPaidLabel.setText(getString(R.string.text_error_message));
                    amountPaidLabel.setVisibility(View.GONE);
                    amountPaidText.setText(resultModel.getTransactionResponse().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getTransactionResponse().getMessage());
                    confirmedBookingTV.setText("Your booking is cancelled.");
                    orderCompletedDetails("Cancelled");

                    // Tracking the screen view
                    MyApplication.getInstance().trackScreenView("Payment Failed");
                }
            } else {
                isSuccessORFailure = false;
                paymentResultImage.setImageResource(R.drawable.img_cross_red);
                paymentResultText.setText(getString(R.string.text_payment_failure));
                transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_error_message));

                transactionIdText.setText(""+Utility.ORDER_ID);

                if (resultModel.getError() != null) {
                    amountPaidText.setText(resultModel.getError().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getError().getMessage());
               //transactionIdText.setText(resultModel.getError().);
                }
                orderCompletedDetails("Failed");
                confirmedBookingTV.setText("Your booking is failed.");

                // Tracking the screen view
                MyApplication.getInstance().trackScreenView("Payment Failed");
            }
        }else{
            if(resultModel.getPaymentResponse() !=null){
                isSuccessORFailure = true;
                paymentResultImage.setImageResource(R.drawable.img_checkmark_green);
                paymentResultText.setText(getString(R.string.text_withdraw_success));
                transactionIdLabel.setText(getString(R.string.text_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_amount_paid));
                amountPaidText.setText(getString(R.string.rs)+Double.toString(resultModel.getPaymentResponse()
                        .getTransactionAmount()
                        .getValueAsDouble()));
                transactionIdText.setText(""+Utility.ORDER_ID);
                orderCompletedDetails("Completed");

                confirmedBookingTV.setText("Your booking is confirmed.");

                AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(getActivity());
                // Tracking the screen view
                MyApplication.getInstance().trackScreenView("Payment Successful");
            } else {
                isSuccessORFailure = false;
                paymentResultImage.setImageResource(R.drawable.img_cross_red);
                paymentResultText.setText(getString(R.string.text_withdraw_failure));
                transactionIdLabel.setText(getString(R.string.text_error_transaction_id));
                amountPaidLabel.setText(getString(R.string.text_error_message));
                transactionIdText.setText(""+Utility.ORDER_ID);
                transactionIdLabel.setVisibility(View.GONE);
                //walletAvailableLayout.setVisibility(View.GONE);

                if (resultModel.getError() != null) {
                    amountPaidText.setText(resultModel.getError().getMessage());
                    Logger.d(TAG + " Transaction Error " + resultModel.getError().getMessage());
                    //                transactionIdText.setText(resultModel.getError().);
                }

                // Tracking the screen view
                MyApplication.getInstance().trackScreenView("Payment Failed");

                confirmedBookingTV.setText("Your booking is failed.");
                orderCompletedDetails("Failed");
            }
        }
        return layout;
    }

    private void orderCompletedDetails(String _orderstatus) {
        try {
            if (ringProgressDialog != null) {
                ringProgressDialog.dismiss();
                ringProgressDialog = null;
            }
            ringProgressDialog = ProgressDialog.show(getActivity(), "Booking...", "Your order confirming wait...", true);
            ringProgressDialog.setCancelable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(getActivity());
        String cookieStr = dataBaseHelper.getRegisteredCookie();
        RequestParams params = new RequestParams();
        params.put("cookie", cookieStr);
        params.put("orderId", Utility.ORDER_ID);
        params.put("orderstatus", _orderstatus);

        Log.w("Request Parameters -->", "" + params);

        if (value_from_events_or_slots.equalsIgnoreCase("FROM_EVENTS")){
            if (_orderstatus.equalsIgnoreCase("Completed")) {
                dataBaseHelper.deleteEventsCart();
            }
            sendConfirmationRequesttoServer(AppConstants.EVENT_ORDER_UPDATE_URL, params);
        } else {
            if (_orderstatus.equalsIgnoreCase("Completed")) {
                dataBaseHelper.deleteCart();
            }
            sendConfirmationRequesttoServer(AppConstants.ORDER_UPDATE_URL, params);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement FragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void sendConfirmationRequesttoServer(String confirmationURL, RequestParams _params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(confirmationURL, _params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                            ringProgressDialog = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ringProgressDialog != null) {
                        ringProgressDialog.dismiss();
                        ringProgressDialog = null;
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Utility.showCustomToast("Requested resource not found", getActivity());
                } else if (statusCode == 500) {
                    Utility.showCustomToast("Something went wrong at server end", getActivity());
                } else {
                    Utility.showCustomToast("Unexpected Error occurred! Please try again !", getActivity());
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog = null;
                }
            }
        });
    }
}