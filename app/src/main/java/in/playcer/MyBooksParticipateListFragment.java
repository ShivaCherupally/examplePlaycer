package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.playcer.adapters.MyEventsBookingsAllListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.MyEventsBookings;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/24/2015.
 * at CaretTech
 */
@SuppressLint("InflateParams")
public class MyBooksParticipateListFragment extends Fragment {
    Activity activity;
    ArrayList<MyEventsBookings> myBookingsList = new ArrayList<MyEventsBookings>();
    ListView myBookingsListView;
    TextView noBooksAvailableTV;
    LinearLayout noBookingsNetLL;
    View bookingsView;
    private int countList = 0;
    private ProgressWheel progressWheel_CENTER_BOOKINGS;
    //My Bookings Member variables
    private int myBookingscountList = 0;
    AppDataBaseHelper dataBaseHelper;

    public static MyBooksParticipateListFragment newInstance() {
        MyBooksParticipateListFragment f = new MyBooksParticipateListFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataBaseHelper = new AppDataBaseHelper(getActivity());
        bookingsView = inflater.inflate(R.layout.frag_my_bookings_list, null);

        progressWheel_CENTER_BOOKINGS = (ProgressWheel) bookingsView.findViewById(R.id.progress_wheel1);
        progressWheel_CENTER_BOOKINGS.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER_BOOKINGS.setRimColor(Color.LTGRAY);

        noBooksAvailableTV = (TextView) bookingsView.findViewById(R.id.noBooksAvailableTVID);
        myBookingsListView = (ListView) bookingsView.findViewById(R.id.myBookingsListViewLVID);

        //myBookingsListView.setHasFixedSize(true);
        // LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        // llm.setOrientation(LinearLayoutManager.VERTICAL);
        //myBookingsListView.setLayoutManager(llm);

        myBookingsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Intent newsIntent = new Intent(getActivity(), MyBookingsSingleOrderListActivity.class);
                newsIntent.putExtra("ORDER_ID", "" + myBookingsList.get(arg2).get_BookingID());
                newsIntent.putExtra("EVENT_ID", "" + myBookingsList.get(arg2).get_event_id());
                newsIntent.putExtra("FROM_EVENTS_BOOKINGS", "EVENTS");
                getActivity().startActivity(newsIntent);
            }
        });

        if (Utility.isOnline(getActivity())) {
            ifNetisAvilableGetBookings(bookingsView);
        } else {
            getBookingsTroubleConnecting(bookingsView);
        }
        return bookingsView;
    }

    private void getBookingsTroubleConnecting(final View _bookingsView) {
        noBookingsNetLL = (LinearLayout) _bookingsView.findViewById(R.id.noNetworkConnectRLID);
        noBookingsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) _bookingsView.findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noBookingsNetLL.setVisibility(View.GONE);
                progressWheel_CENTER_BOOKINGS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(getActivity())) {
                            ifNetisAvilableGetBookings(_bookingsView);
                        } else {
                            progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                            noBookingsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void ifNetisAvilableGetBookings(View _bookingsListView) {
        sendRequestToGetMyBookingsList(AppConstants.GET_MY_EVENTS_BOOKINGS_LIST_URL, _bookingsListView);
    }

    private void sendRequestToGetMyBookingsList(String getMybookingsListUrl, final View _bookingsListView) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String cookieStr = dataBaseHelper.getRegisteredCookie();
        params.put("cookie", cookieStr);
        client.post(getMybookingsListUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        JSONObject jsonResponse;

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            String status = jsonResponse.getString("status").toString();

                            if (status.equalsIgnoreCase("ok")) {
                                /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                                /*******  Returns null otherwise.  *******/
                                // JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");
                                JSONArray jsonMainNode = jsonResponse.getJSONArray("my_booking_events");

                                /*********** Process each JSON Node ************/
                                myBookingscountList = jsonMainNode.length();

                                if (myBookingscountList != 0) {
                                    myBookingsList = new ArrayList<MyEventsBookings>();
                                    for (int i = 0; i < myBookingscountList; i++) {
                                        /****** Get Object for each JSON node.***********/
                                        JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                        /******* Fetch node values **********/
                                        int booked_id = Integer.parseInt(jsonChildNode.optString("booking_id").toString());
                                        String event_id = jsonChildNode.optString("event_id").toString();
                                        String event_name = jsonChildNode.optString("event_name").toString();
                                        String event_date = jsonChildNode.optString("date").toString();
                                        String total = String.valueOf(jsonChildNode.optString("total"));

                                        myBookingsList.add(new MyEventsBookings(booked_id, event_id, event_name, event_date, total));

                                        OutputData += "Node : \n\n     " + booked_id + " | " + event_date;
                                        Log.i("JSON parse", OutputData);
                                    }

                                    /************ Show Output on screen/activity **********/
                                    new Handler().postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            // This method will be executed once the timer is over
                                            if (myBookingscountList != 0) {
                                                MyEventsBookingsAllListAdapter ca = new MyEventsBookingsAllListAdapter(getActivity(), myBookingscountList, myBookingsList);
                                                myBookingsListView.setAdapter(ca);
                                                progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                            } else {
                                                myBookingsListView.setVisibility(View.GONE);
                                                progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                                noBooksAvailableTV.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }, 1000);
                                } else {
                                    myBookingsListView.setVisibility(View.GONE);
                                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                                    noBooksAvailableTV.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                            noBooksAvailableTV.setVisibility(View.VISIBLE);
                            if (e != null) {
                                e.printStackTrace();
                            }
                            //getBookingsTroubleConnecting(_bookingsListView);
                        }
                    } catch (Exception e) {
                        progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                        noBooksAvailableTV.setVisibility(View.VISIBLE);
                        if (e != null) {
                            e.printStackTrace();
                        }
                        //getBookingsTroubleConnecting(_bookingsListView);
                    }
                } else {
                    myBookingsListView.setVisibility(View.GONE);
                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                    noBooksAvailableTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    progressWheel_CENTER_BOOKINGS.setVisibility(View.GONE);
                    noBooksAvailableTV.setVisibility(View.GONE);
                    if (content != null && content.contains("authfailed")) {
                        Intent authFailed = new Intent(getActivity(), VerificationExistingActivity.class);
                        authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(authFailed);
                        getActivity().finish();
                    } else {
                        getBookingsTroubleConnecting(_bookingsListView);
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    @SuppressWarnings("static-access")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}