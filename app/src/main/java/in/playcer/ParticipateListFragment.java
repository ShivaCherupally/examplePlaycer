package in.playcer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.playcer.adapters.EventEventsListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.EventsListItemData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;


public class ParticipateListFragment extends Fragment {

    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    //@Bind(R.id.loading)
    //FrameLayout loading;

    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private EventEventsListAdapter mAdapter;
    //private Toolbar toolbar;

    private static final String ARG_KEY_SORT = "arg_key_sort";

    private String sort;

    private int countList = 0;
    private ProgressWheel progressWheel_CENTER_EVENTS;
    ArrayList<EventsListItemData> eventssList = new ArrayList<EventsListItemData>();
    Activity activity;
    TextView noSportsAvailableTV;

    AppDataBaseHelper dataBaseHelper;
    LinearLayout noNewsNetLL;
    View view;

    public static ParticipateListFragment newInstance(String sort) {
        ParticipateListFragment fragment = new ParticipateListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_SORT, sort);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_KEY_SORT)) {
            sort = arguments.getString(ARG_KEY_SORT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.event_places_list_activity_main, container, false);
        ButterKnife.bind(this, view);

        dataBaseHelper = new AppDataBaseHelper(getActivity());

        progressWheel_CENTER_EVENTS = (ProgressWheel) view.findViewById(R.id.progress_wheel1);
        progressWheel_CENTER_EVENTS.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER_EVENTS.setRimColor(Color.LTGRAY);

        noSportsAvailableTV = (TextView) view.findViewById(R.id.noSportsAvailableTVID);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        if (Utility.isOnline(getActivity())) {
            ifNetisAvilableSports();
        } else {
            loadingSportsTroubleConnecting(view);
        }

        return view;
    }

    private void loadingSportsTroubleConnecting(final View _View) {
        noNewsNetLL = (LinearLayout) _View.findViewById(R.id.noNetworkConnectRLID);
        noNewsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) _View.findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noNewsNetLL.setVisibility(View.GONE);
                progressWheel_CENTER_EVENTS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(getActivity())) {
                            ifNetisAvilableSports();
                        } else {
                            progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                            noNewsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void ifNetisAvilableSports() {
        sendRequestToGetSportsList(AppConstants.EVENTS_LIST_URL);
    }

    private void sendRequestToGetSportsList(String eventsListUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String cookieStr = dataBaseHelper.getRegisteredCookie();
        params.put("cookie", cookieStr);
        params.put("city", ""+dataBaseHelper.getCurrentCity());
        client.post(eventsListUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    try {
                        System.out.println(response);

                        String OutputData = "";
                        JSONObject jsonResponse;

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            // JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");
                            JSONArray jsonMainNode = jsonResponse.getJSONArray("Events");

                            /*********** Process each JSON Node ************/
                            countList = jsonMainNode.length();

                            if (countList != 0) {
                                eventssList = new ArrayList<EventsListItemData>();
                                for (int i = 0; i < countList; i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    int event_ID = Integer.parseInt(jsonChildNode.optString("event_id"));
                                    String event_name = jsonChildNode.optString("event_name");
                                    String event_date = jsonChildNode.optString("event_date");
                                    String event_imageURL = jsonChildNode.optString("image");

                                    eventssList.add(new EventsListItemData(event_ID,event_name, event_date, event_imageURL));

                                    OutputData += "Node : \n\n     " + event_name + " | " + event_imageURL + " | " + event_ID;
                                    Log.i("JSON parse", OutputData);
                                }

                                /************ Show Output on screen/activity **********/
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        // This method will be executed once the timer is over
                                        if (countList != 0) {
                                            if (activity != null) {
                                                // mRecyclerView.setVisibility(View.VISIBLE);
                                                //mAdapter = new SportListAdapter(activity, sportsList);
                                                mAdapter = new EventEventsListAdapter(activity, eventssList);
                                                mRecyclerView.setAdapter(mAdapter);
                                                mAdapter.setOnItemClickListener(onItemClickListener);
                                            } else {
                                                // mRecyclerView.setVisibility(View.GONE);
                                                noSportsAvailableTV.setVisibility(View.VISIBLE);
                                            }
                                            //}
                                            progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                                        } else {
                                            progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                                            //mRecyclerView.setVisibility(View.GONE);
                                            noSportsAvailableTV.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }, 900);
                            } else {
                                progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                                //mRecyclerView.setVisibility(View.GONE);
                                noSportsAvailableTV.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                            noSportsAvailableTV.setVisibility(View.GONE);
                            e.printStackTrace();
                           // loadingSportsTroubleConnecting(_sportsView);
                        }
                    } catch (Exception e) {
                        progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                        noSportsAvailableTV.setVisibility(View.GONE);
                        e.printStackTrace();
                        //loadingSportsTroubleConnecting(_sportsView);
                    }
                } else {
                    //mRecyclerView.setVisibility(View.GONE);
                    noSportsAvailableTV.setVisibility(View.VISIBLE);
                    progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    if (content!= null && content.contains("authfailed")) {
                        Intent authFailed = new Intent(getActivity(), DashboardActivity.class);
                        authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        authFailed.putExtra("EXIT", true);
                        authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                        startActivity(authFailed);
                        getActivity().finish();
                    } else {
                        loadingSportsTroubleConnecting(view);
                    }

                    progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                    noSportsAvailableTV.setVisibility(View.GONE);
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
        //EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        //EventBus.getDefault().unregister(this);
    }

    EventEventsListAdapter.OnItemClickListener onItemClickListener = new EventEventsListAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View v, int position) {
            Intent transitionIntent = new Intent(getActivity(), ParticipateFullDetailActivity.class);
            transitionIntent.putExtra("EVENT_ID",""+eventssList.get(position).get_event_ID());
            getActivity().startActivity(transitionIntent);

        }
    };
}
