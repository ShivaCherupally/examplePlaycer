package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import in.playcer.adapters.EventsSportsCafeNewsListAdapter;
import in.playcer.libs.ProgressWheel;
import in.playcer.model.NewsListData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/24/2015.
 * at CaretTech
 */
@SuppressLint("InflateParams")
public class SportsCafeNewsListFragment extends Fragment {
    public EventsSportsCafeNewsListAdapter mNewsAdapter;
    Activity activity;
    RecyclerView newsListViewLV;
    ArrayList<NewsListData> newsDataList = new ArrayList<NewsListData>();
    ArrayList<NewsListData> newsDataListTemp = new ArrayList<NewsListData>();
    TextView noNewssAvailableTV;
    LinearLayout noNewsNetLL;
    View newsView;
    private ProgressWheel progressWheel_CENTER_NEWS;
    private ProgressWheel progressWheel_BOTTOM;
    //My Bookings Member variables
    private int myNewsCountList = 0;
    AppDataBaseHelper dataBaseHelper;

    int page_Num = 1;
    int newsListCount = 0;
    //private int mvisibleItemCount = -1;
    private String fetchDirectionUP = Utility.FETCH_DIRECTION_UP;
    private String fetchDirection = "";

    private static final String ARG_KEY_SORT = "arg_key_sort";
    private String sort;
    private LinearLayoutManager mStaggeredGridLayoutManager;

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    public static SportsCafeNewsListFragment newInstance(String sort) {
        SportsCafeNewsListFragment f = new SportsCafeNewsListFragment();
        Bundle b = new Bundle();
        b.putString(ARG_KEY_SORT, sort);
        f.setArguments(b);
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
        dataBaseHelper = new AppDataBaseHelper(getActivity());
        newsView = inflater.inflate(R.layout.frag_news_list_new, null);

        progressWheel_CENTER_NEWS = (ProgressWheel) newsView.findViewById(R.id.progress_wheel1);
        progressWheel_CENTER_NEWS.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER_NEWS.setRimColor(Color.LTGRAY);

        progressWheel_BOTTOM = (ProgressWheel) newsView.findViewById(R.id.progress_wheel);
        progressWheel_BOTTOM.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_BOTTOM.setRimColor(Color.LTGRAY);

        noNewssAvailableTV = (TextView) newsView.findViewById(R.id.noBooksAvailableTVID);
        newsListViewLV = (RecyclerView) newsView.findViewById(R.id.newsListViewLVID);

        mStaggeredGridLayoutManager = new LinearLayoutManager(getActivity());
        mStaggeredGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        newsListViewLV.setLayoutManager(mStaggeredGridLayoutManager);

        newsListViewLV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mStaggeredGridLayoutManager.getChildCount();
                    totalItemCount = mStaggeredGridLayoutManager.getItemCount();
                    pastVisiblesItems = mStaggeredGridLayoutManager.findFirstVisibleItemPosition();
                    // mvisibleItemCount = visibleItemCount;

                    if (loading) {
                        fetchDirection = fetchDirectionUP;
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            sendRequestWithScrollDirection(fetchDirectionUP, totalItemCount);
                            loading = true;
                            Log.v("...", "Last Item Wow !");
                            //Do pagination.. i.e. fetch new data
                            //Utility.showCustomToast("Loading more news....",getActivity());
                        }
                    }
                }
            }
        });

        if (Utility.isOnline(getActivity())) {
            page_Num = 1;
            mNewsAdapter = null;
            ifNetisAvilableGetNews(newsView, page_Num);
        } else {
            getNewsTroubleConnecting(newsView);
        }
        return newsView;
    }

    private void getNewsTroubleConnecting(final View _newsView) {
        noNewsNetLL = (LinearLayout) _newsView.findViewById(R.id.noNetworkConnectRLID);
        noNewsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) _newsView.findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noNewsNetLL.setVisibility(View.GONE);
                progressWheel_CENTER_NEWS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(getActivity())) {
                            page_Num = 1;
                            ifNetisAvilableGetNews(_newsView, page_Num);
                        } else {
                            progressWheel_CENTER_NEWS.setVisibility(View.GONE);
                            noNewsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void ifNetisAvilableGetNews(View _newsListView, int _page_Num) {
        sendRequestToGetNewsList(AppConstants.GET_NEWS_LIST_URL, _newsListView, _page_Num);
    }

    private void sendRequestToGetNewsList(String getNewsListUrl, final View _newsListView, int pageNo) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = getNewsListUrl + "" + pageNo;
        client.post(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println(response);

                    String OutputData = "";
                    JSONObject jsonResponse;

                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        jsonResponse = new JSONObject(response);

                        /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                        /*******  Returns null otherwise.  *******/
                        JSONArray jsonMainNode = jsonResponse.getJSONArray("posts");

                        /*********** Process each JSON Node ************/
                        myNewsCountList = jsonMainNode.length();
                        newsListViewLV.setVisibility(View.VISIBLE);
                        if (myNewsCountList != 0) {
                            newsDataList = new ArrayList<NewsListData>();
                        } else {
                            progressWheel_BOTTOM.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < myNewsCountList; i++) {
                            /****** Get Object for each JSON node.***********/
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                            /******* Fetch node values **********/
                            int news_id = Integer.parseInt(jsonChildNode.optString("id"));
                            String news_type = jsonChildNode.optString("type");
                            String news_slug = jsonChildNode.optString("slug");
                            String news_url = jsonChildNode.optString("url");
                            String news_title = jsonChildNode.optString("title");
                            String news_content = jsonChildNode.optString("content");
                            String news_excerpt = jsonChildNode.optString("excerpt");
                            String news_modified = String.valueOf(jsonChildNode.optString("modified"));
                            String news_thumbnail = String.valueOf(jsonChildNode.optString("thumbnail"));

                            newsDataList.add(new NewsListData(news_id, news_type, news_slug, news_url, news_title, news_content, news_excerpt, news_modified, news_thumbnail, 0));

                            OutputData += "Node : \n\n     " + news_id + " | " + news_type + " | " + news_slug + " | " + news_title;
                            Log.i("JSON parse", OutputData);
                        }

                        newsListCount = newsListCount + newsDataList.size();

                        /************ Show Output on screen/activity **********/
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    // This method will be executed once the timer is over
                                    if (myNewsCountList != 0) {
                                        if (fetchDirection.equalsIgnoreCase(fetchDirectionUP)) {

                                            //int position = newsDataListTemp.size();
                                            newsDataListTemp.addAll(newsDataList);
                                            newsDataList = newsDataListTemp;

                                            //List<String> al = new ArrayList<>();
                                            // add elements to al, including duplicates
                                            Set<NewsListData> hs = new HashSet<>();
                                            hs.addAll(newsDataList);
                                            newsDataList.clear();
                                            newsDataList.addAll(hs);

                                            if (activity != null) {
                                                mNewsAdapter = new EventsSportsCafeNewsListAdapter(activity, newsDataList);
                                                newsListViewLV.setAdapter(mNewsAdapter);
                                                mNewsAdapter.setOnItemClickListener(onItemClickListener);
                                            } else {
                                                //newsListViewLV.setVisibility(View.GONE);
                                                progressWheel_CENTER_NEWS.setVisibility(View.VISIBLE);
                                            }

                                            /*newsListViewLV.setScrollingCacheEnabled(false);

                                            if (newsDataList.size() > 0 && mvisibleItemCount != -1) {
                                                 newsListViewLV.setSelection(position- mvisibleItemCount + 2);
                                            } else {
                                                newsListViewLV.setSelection(position);
                                            }*/
                                        }

                                        if (mNewsAdapter != null) {
                                            mNewsAdapter.notifyDataSetChanged();
                                        } else {
                                            if (activity != null) {
                                                Set<NewsListData> hs = new HashSet<>();
                                                hs.addAll(newsDataList);
                                                newsDataList.clear();
                                                newsDataList.addAll(hs);

                                                mNewsAdapter = new EventsSportsCafeNewsListAdapter(activity, newsDataList);
                                                newsListViewLV.setAdapter(mNewsAdapter);
                                                mNewsAdapter.setOnItemClickListener(onItemClickListener);
                                            } else {
                                                //newsListViewLV.setVisibility(View.GONE);
                                                progressWheel_CENTER_NEWS.setVisibility(View.VISIBLE);
                                            }
                                        }
                                        progressWheel_CENTER_NEWS.setVisibility(View.GONE);
                                        progressWheel_BOTTOM.setVisibility(View.GONE);
                                    } else {
                                        progressWheel_BOTTOM.setVisibility(View.GONE);
                                    }
                                } catch (OutOfMemoryError oom) {
                                    oom.printStackTrace();
                                } catch (Exception ee) {
                                    ee.printStackTrace();
                                }
                            }
                        }, 700);

                    } catch (JSONException e) {
                        progressWheel_BOTTOM.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    progressWheel_BOTTOM.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    progressWheel_CENTER_NEWS.setVisibility(View.GONE);
                    noNewssAvailableTV.setVisibility(View.GONE);
                    getNewsTroubleConnecting(_newsListView);
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

    EventsSportsCafeNewsListAdapter.OnItemClickListener onItemClickListener = new EventsSportsCafeNewsListAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            try {
                Intent newsIntent = new Intent(getActivity(), SingleArticleActivity.class);
                newsIntent.putExtra("ARTICLE_ID", "" + newsDataList.get(position).get_newsID());
                newsDataList.get(position).set_isOpened(1);
                getActivity().startActivity(newsIntent);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mNewsAdapter != null) {
            mNewsAdapter.notifyDataSetChanged();
        }
    }

    /*@Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        try {
            mvisibleItemCount = visibleItemCount;
            if (firstVisibleItem > mfirstVisibleItem && !loading && (firstVisibleItem + visibleItemCount == totalItemCount) && totalItemCount > 0 && totalItemCount != visibleItemCount) {
                fetchDirection = fetchDirectionUP;
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && totalItemCount != 0) {
                    sendRequestWithScrollDirection(fetchDirectionUP, totalItemCount);
                    loading = true;
                }
            }
            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            mfirstVisibleItem = firstVisibleItem;
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.w("HARI-->DEBUG", e);
            }
        }
    }*/
    private void sendRequestWithScrollDirection(String DirectiontoFetch, int totalItemCount) {
        if (newsDataList.size() == totalItemCount) {
            fetchDirection = DirectiontoFetch;
            newsDataListTemp = newsDataList;
            if (myNewsCountList != 0) {
                if ((newsListCount - 10) >= 0) {
                    progressWheel_BOTTOM.setVisibility(View.VISIBLE);
                    if (Utility.isOnline(getActivity())) {
                        page_Num = page_Num + 1;
                        ifNetisAvilableGetNews(newsView, page_Num);
                    } else {
                        progressWheel_BOTTOM.setVisibility(View.GONE);
                        Utility.showCustomToast(getResources().getString(R.string.pls_connect_internet), getActivity());
                    }
                } else {
                    progressWheel_BOTTOM.setVisibility(View.GONE);
                }
            }
        }
    }
}