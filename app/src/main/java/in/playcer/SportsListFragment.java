package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

import in.playcer.adapters.EventsSportsNewListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.SportsListItemData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/24/2015.
 * at CaretTech
 */
@SuppressLint("InflateParams")
public class SportsListFragment extends Fragment implements OnItemClickListener {
    public EventsSportsNewListAdapter mAdapter;
    Activity activity;
    RecyclerView sportsListLV;
    ArrayList<SportsListItemData> sportsList = new ArrayList<SportsListItemData>();
    TextView noSportsAvailableTV;
    LinearLayout noSportsNetLL;
    View sportsView;
    private int countList = 0;
    private ProgressWheel progressWheel_CENTER_SPORTS;
    AppDataBaseHelper dataBaseHelper;
    String pickedClubName = "";

    private static final String ARG_KEY_SORT = "arg_key_sort";
    private String sort;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    public static SportsListFragment newInstance(String sort) {
        SportsListFragment f = new SportsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_SORT, sort);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(ARG_KEY_SORT)) {
            sort = arguments.getString(ARG_KEY_SORT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dataBaseHelper= new AppDataBaseHelper(getActivity());
        sportsView = inflater.inflate(R.layout.frag_sports_list_new, null);

            progressWheel_CENTER_SPORTS = (ProgressWheel) sportsView.findViewById(R.id.progress_wheel1);
            progressWheel_CENTER_SPORTS.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel_CENTER_SPORTS.setRimColor(Color.LTGRAY);

            noSportsAvailableTV = (TextView) sportsView.findViewById(R.id.noSportsAvailableTVID);

            sportsListLV = (RecyclerView) sportsView.findViewById(R.id.sportsListViewLVID);
            //sportsListLV.setOnItemClickListener(this);

            mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            sportsListLV.setLayoutManager(mStaggeredGridLayoutManager);

            if (Utility.isOnline(getActivity())) {
                ifNetisAvilableSports(sportsView);
            } else {
                loadingSportsTroubleConnecting(sportsView);
            }
            return sportsView;
    }

    private void loadingSportsTroubleConnecting(final View _sportsView) {
        noSportsNetLL = (LinearLayout) _sportsView.findViewById(R.id.noNetworkConnectRLID);
        noSportsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) _sportsView.findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noSportsNetLL.setVisibility(View.GONE);
                sportsListLV.setVisibility(View.GONE);
                progressWheel_CENTER_SPORTS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(getActivity())) {
                            ifNetisAvilableSports(_sportsView);
                        } else {
                            progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                            noSportsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void ifNetisAvilableSports(View _sportsListView) {
        sendRequestToGetSportsList(AppConstants.SPORTS_LIST_URL, _sportsListView);
    }

    private void sendRequestToGetSportsList(String sportsListUrl, final View _sportsView) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String cookieStr = dataBaseHelper.getRegisteredCookie();
        params.put("cookie", cookieStr);
        client.post(sportsListUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    sportsListLV.setVisibility(View.VISIBLE);
                    try {
                        System.out.println(response);

                        String OutputData = "";
                        JSONObject jsonResponse;

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            String role = dataBaseHelper.getRegisteredRole();
                            if (role != null && role.equalsIgnoreCase("manager")) {
                                pickedClubName = jsonResponse.getString("club").toString();

                                if (pickedClubName.trim().length() > 1) {
                                    if (DashboardActivity.managerClubNameTV !=null) {
                                        DashboardActivity.managerClubNameTV.setText("" + pickedClubName);
                                    }
                                }
                            }
                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            // JSONArray jsonMainNode = jsonResponse.optJSONArray("posts");
                            JSONArray jsonMainNode = jsonResponse.getJSONArray("terms");

                            /*********** Process each JSON Node ************/
                            countList = jsonMainNode.length();

                            if (countList != 0) {
                                sportsList = new ArrayList<SportsListItemData>();
                                for (int i = 0; i < countList; i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    int sport_id = Integer.parseInt(jsonChildNode.optString("id").toString());
                                    String sport_name = jsonChildNode.optString("name").toString();
                                    String sport_imageURL = jsonChildNode.optString("image").toString();
                                    String slug = jsonChildNode.optString("slug").toString();
                                    String sport_ulr = jsonChildNode.optString("ulr").toString();

                                    sportsList.add(new SportsListItemData(sport_id, sport_name, slug, sport_imageURL, sport_ulr));

                                    OutputData += "Node : \n\n     " + sport_id + " | " + sport_name + " | " + sport_imageURL + " | " + sport_ulr;
                                    Log.i("JSON parse", OutputData);
                                }

                                /************ Show Output on screen/activity **********/
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        // This method will be executed once the timer is over
                                        if (countList != 0) {
                                            if (activity != null) {
                                               // sportsListLV.setVisibility(View.VISIBLE);
                                                //mAdapter = new SportListAdapter(activity, sportsList);
                                                mAdapter = new EventsSportsNewListAdapter(activity, sportsList);
                                                sportsListLV.setAdapter(mAdapter);
                                                mAdapter.setOnItemClickListener(onItemClickListener);
                                            } else {
                                               // sportsListLV.setVisibility(View.GONE);
                                                noSportsAvailableTV.setVisibility(View.VISIBLE);
                                            }
                                            //}
                                            progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                                        } else {
                                            progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                                            //sportsListLV.setVisibility(View.GONE);
                                            noSportsAvailableTV.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }, 800);
                            } else {
                                progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                                //sportsListLV.setVisibility(View.GONE);
                                noSportsAvailableTV.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                            noSportsAvailableTV.setVisibility(View.GONE);
                            e.printStackTrace();
                            loadingSportsTroubleConnecting(_sportsView);
                        }
                    } catch (Exception e) {
                        progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                        noSportsAvailableTV.setVisibility(View.GONE);
                        e.printStackTrace();
                        loadingSportsTroubleConnecting(_sportsView);
                    }
                } else {
                    //sportsListLV.setVisibility(View.GONE);
                    noSportsAvailableTV.setVisibility(View.VISIBLE);
                    progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try{
                    progressWheel_CENTER_SPORTS.setVisibility(View.GONE);
                    noSportsAvailableTV.setVisibility(View.GONE);
                    if (content!= null && content.contains("authfailed")) {
                        Intent authFailed = new Intent(getActivity(), DashboardActivity.class);
                        authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        authFailed.putExtra("EXIT", true);
                        authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                        startActivity(authFailed);
                        getActivity().finish();
                    } else {
                        loadingSportsTroubleConnecting(_sportsView);
                    }
                }catch (Exception ee){
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

    EventsSportsNewListAdapter.OnItemClickListener onItemClickListener = new EventsSportsNewListAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(getActivity());
            String role = dataBaseHelper.getRegisteredRole();
            if (role != null && role.equalsIgnoreCase("manager")) {
                Intent sportIntent = new Intent(getActivity(), SportsPickVenueActivity.class);
                sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL, sportsList.get(position).get_sportDetailsURL());
                sportIntent.putExtra(Utility.KEY_SPORT_NAME, sportsList.get(position).get_sportName());
                sportIntent.putExtra(Utility.KEY_SLUG, sportsList.get(position).get_slug());
                sportIntent.putExtra(Utility.KEY_pickedClubName, pickedClubName);
                sportIntent.putExtra(Utility.KEY_pickedClubID, "");
                sportIntent.putExtra(Utility.KEY_address, "");
                sportIntent.putExtra(Utility.KEY_phone, "");
                sportIntent.putExtra(Utility.KEY_latlng, "");
                sportIntent.putExtra(Utility.KEY_location, "");
                sportIntent.putExtra(Utility.KEY_city, "");

                startActivity(sportIntent);
            } else {
                String city = dataBaseHelper.getCurrentCity();
                if (city != null && !city.equalsIgnoreCase("empty")) {
                    Intent sportIntent = new Intent(getActivity(), ClubsActivity.class);
                    sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL, sportsList.get(position).get_sportDetailsURL());
                    sportIntent.putExtra(Utility.KEY_SPORT_NAME, sportsList.get(position).get_sportName());
                    sportIntent.putExtra(Utility.KEY_SLUG, sportsList.get(position).get_slug());
                    startActivity(sportIntent);
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.pls_select_city), getActivity());
                }
            }
            MyApplication.getInstance().trackEvent("Sports", "Tapped Sport", "Sport Name is " + sportsList.get(position).get_sportName());
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(getActivity());
        String role = dataBaseHelper.getRegisteredRole();
        if (role != null && role.equalsIgnoreCase("manager")) {
            Intent sportIntent = new Intent(getActivity(), SportsPickVenueActivity.class);
            sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL, sportsList.get(position).get_sportDetailsURL());
            sportIntent.putExtra(Utility.KEY_SPORT_NAME, sportsList.get(position).get_sportName());
            sportIntent.putExtra(Utility.KEY_SLUG, sportsList.get(position).get_slug());
            sportIntent.putExtra(Utility.KEY_pickedClubName, pickedClubName);
            sportIntent.putExtra(Utility.KEY_pickedClubID, "");
            sportIntent.putExtra(Utility.KEY_address, "");
            sportIntent.putExtra(Utility.KEY_phone, "");
            sportIntent.putExtra(Utility.KEY_latlng, "");
            sportIntent.putExtra(Utility.KEY_location, "");
            sportIntent.putExtra(Utility.KEY_city, "");

            startActivity(sportIntent);
        } else {
            String city = dataBaseHelper.getCurrentCity();
            if (city != null && !city.equalsIgnoreCase("empty")) {
                Intent sportIntent = new Intent(getActivity(), ClubsActivity.class);
                sportIntent.putExtra(Utility.KEY_SINGLE_SPORT_ALL_SLOTS_URL, sportsList.get(position).get_sportDetailsURL());
                sportIntent.putExtra(Utility.KEY_SPORT_NAME, sportsList.get(position).get_sportName());
                sportIntent.putExtra(Utility.KEY_SLUG, sportsList.get(position).get_slug());
                startActivity(sportIntent);
            } else {
                Utility.showCustomToast(getResources().getString(R.string.pls_select_city), getActivity());
            }
        }

        MyApplication.getInstance().trackEvent("Sports", "Tapped Sport", "Sport Name is " + sportsList.get(position).get_sportName());
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}