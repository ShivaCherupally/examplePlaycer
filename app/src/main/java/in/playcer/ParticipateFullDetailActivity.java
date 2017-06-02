package in.playcer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import in.playcer.model.EventSingleItemData;
import in.playcer.model.EventSingleTicketsItemData;
import in.playcer.model.Place;
import in.playcer.model.PlaceData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.AppDataBaseHelper;
import in.playcer.utilities.Utility;

public class ParticipateFullDetailActivity extends Activity {
    public static final String EXTRA_PARAM_ID = "place_id";
    private ImageView mImageView;
    private TextView eventTitleTV, eventSubTitleTV, eventDateTV;
    private LinearLayout mTitleHolder;
    private boolean isEditTextVisible;
    private InputMethodManager mInputManager;
    private Place mPlace;
    int defaultColor;

    ProgressDialog ringProgressDialog;
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(this);
    public ArrayList<EventSingleItemData> singleEventsList = new ArrayList<EventSingleItemData>();

    Button registerEventBtn;

    String eventID = "";

    LinearLayout noNewsNetLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity_detail);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);

        mPlace = PlaceData.placeList().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        //mList = (ListView) findViewById(R.id.list);
        mImageView = (ImageView) findViewById(R.id.placeImage);
        eventTitleTV = (TextView) findViewById(R.id.eventTitleTVID);
        eventSubTitleTV = (TextView) findViewById(R.id.eventSubTitleTVID);
        eventDateTV = (TextView) findViewById(R.id.eventDateTVID);
        mTitleHolder = (LinearLayout) findViewById(R.id.placeNameHolder);

        defaultColor = getResources().getColor(R.color.colorPrimaryDark);

        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        isEditTextVisible = false;

        registerEventBtn = (Button) findViewById(R.id.registerEventBtnID);
        registerEventBtn.setVisibility(View.GONE);
        registerEventBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent eventsTickets = new Intent(ParticipateFullDetailActivity.this, ParticipateSingleBookingActivity.class);
                    eventsTickets.putExtra("EVENT_ID", ""+singleEventsList.get(0).get_event_ID());
                    eventsTickets.putExtra("EVENT_NAME", ""+singleEventsList.get(0).get_event_name());
                    eventsTickets.putExtra("EVENT_DATE", ""+singleEventsList.get(0).get_event_date());
                    eventsTickets.putExtra("EVENT_convenience_label", ""+singleEventsList.get(0).get_convenience_label());
                    eventsTickets.putExtra("EVENT_convenience_fee", ""+singleEventsList.get(0).get_convenience_fee());
                    startActivity(eventsTickets);
                } catch (IndexOutOfBoundsException e){
                    e.printStackTrace();
                } catch (Exception ee){
                    ee.printStackTrace();
                }
            }
        });

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                Intent bookingDoneIntent = new Intent(ParticipateFullDetailActivity.this, DashboardActivity.class);
                bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                bookingDoneIntent.putExtra("EXIT", true);
                startActivity(bookingDoneIntent);
                onBackPressedAnimationByCHK();
            } else {
                Bundle dataBundle = getIntent().getExtras();
                if (dataBundle != null) {
                    try {
                        ringProgressDialog = ProgressDialog.show(ParticipateFullDetailActivity.this, "Please wait ...", "Loading event details...", true);
                        ringProgressDialog.setCancelable(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    eventID = dataBundle.getString("EVENT_ID");

                    String cookieStr = dataBaseHelper.getRegisteredCookie();
                    RequestParams params = new RequestParams();
                    //params.put("city",cityStr);
                    params.put("cookie", cookieStr);
                    params.put("event_id", ""+eventID);
                    getEventDetails(AppConstants.SINGLE_EVENT_URL, params);
                }
                setupNavigation();
            }
        }
    }

    private void getEventDetails(String singleEventUrl, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(singleEventUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        /******* Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);

                        /*****
                         * Returns the value mapped by name if it exists and is a JSONArray.
                         ***/
                        /******* Returns null otherwise. *******/
                        JSONObject jsonMainNode = jsonResponse.getJSONObject("Event");

                        /*********** Process each JSON Node ************/

                        if (jsonMainNode.length() != 0) {
                            singleEventsList = new ArrayList<EventSingleItemData>();

                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                /****** Get Object for each JSON node. ***********/
                                //JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                int event_id = Integer.parseInt(jsonMainNode.optString("id").toString());
                                String event_title = jsonMainNode.optString("title").toString();
                                String event_content = jsonMainNode.optString("content").toString();

                                String event_excerpt = jsonMainNode.optString("excerpt").toString();
                                String event_date = jsonMainNode.optString("date").toString();
                                String event_image = jsonMainNode.optString("image").toString();
                                int ticket_count = Integer.parseInt(jsonMainNode.optString("ticket_count").toString());
                                String convenience_label = jsonMainNode.optString("convenience_label").toString();
                                String convenience_fee = jsonMainNode.optString("convenience_fee").toString();

                                Utility.singleEventTicketsListData.clear();
                                Utility.singleEventTicketsListData = null;
                                Utility.singleEventTicketsListData = new ArrayList<EventSingleTicketsItemData>();
                                if (ticket_count>0){
                                    registerEventBtn.setVisibility(View.VISIBLE);
                                    JSONArray jsonSubMenu = jsonMainNode.getJSONArray("event_tickets");

                                    for (int j = 0; j < jsonSubMenu.length(); j++) {
                                        /****** Get Object for each JSON node. ***********/
                                        JSONObject jsonChildSub = jsonSubMenu.getJSONObject(j);
                                        int ticket_id = Integer.parseInt(jsonChildSub.optString("id").toString());
                                        String ticket_title = jsonChildSub.optString("title").toString();
                                        String ticket_price = jsonChildSub.optString("price").toString();

                                        Utility.singleEventTicketsListData.add(new EventSingleTicketsItemData(ticket_id, ticket_title,ticket_price));
                                    }
                                } else {
                                    registerEventBtn.setVisibility(View.GONE);
                                }

                                singleEventsList.add(new EventSingleItemData(event_id, event_title, event_content, event_excerpt, event_date, event_image, ticket_count, Utility.singleEventTicketsListData, convenience_label, convenience_fee));
                            }

                            loadInWebView(""+singleEventsList.get(0).get_content()+"");

                            eventTitleTV.setText(""+ Html.fromHtml(singleEventsList.get(0).get_event_name()));
                            eventSubTitleTV.setText(""+ Html.fromHtml(singleEventsList.get(0).get_excerpt()));
                            eventDateTV.setText("Event Date : "+ Html.fromHtml(singleEventsList.get(0).get_event_date()));

                            Picasso.with(ParticipateFullDetailActivity.this).load(singleEventsList.get(0).get_eventImageUtl()).placeholder(R.drawable.placeholder).into(mImageView);
                        }
                    } catch (JSONException e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        allSlotsTroubleConnecting();
                    } catch (Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        allSlotsTroubleConnecting();
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog=null;
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
              //  progressWheel_CENTER.setVisibility(View.GONE);
                if (content!= null && content.contains("authfailed")) {
                    Intent authFailed = new Intent(ParticipateFullDetailActivity.this, DashboardActivity.class);
                    authFailed.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    authFailed.putExtra("EXIT", true);
                    authFailed.putExtra("EXIT_AUTH_FAILED", "authfailed");
                    startActivity(authFailed);
                    onBackPressedAnimationByCHK();
                } else {
                   allSlotsTroubleConnecting();
                }

                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                    ringProgressDialog=null;
                }
            }
        });
    }

    private void allSlotsTroubleConnecting() {
        noNewsNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);
        noNewsNetLL.setVisibility(View.VISIBLE);
        Button noNetWorkTryAgainBtn = (Button) findViewById(R.id.noNetWorkTryAgainBtnID);
        noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                noNewsNetLL.setVisibility(View.GONE);
                //progressWheel_CENTER_EVENTS.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (Utility.isOnline(ParticipateFullDetailActivity.this)) {
                            String cookieStr = dataBaseHelper.getRegisteredCookie();
                            RequestParams params = new RequestParams();
                            //params.put("city",cityStr);
                            params.put("cookie", cookieStr);
                            params.put("event_id", ""+eventID);
                            getEventDetails(AppConstants.SINGLE_EVENT_URL, params);
                        } else {
                           // progressWheel_CENTER_EVENTS.setVisibility(View.GONE);
                            noNewsNetLL.setVisibility(View.VISIBLE);
                        }
                    }
                }, 500);
            }
        });
    }

    private void loadInWebView(String webContent) {
        WebView webView = (WebView) findViewById(R.id.detailsWVID);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = Utility.hariEmailIntent(ParticipateFullDetailActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        String summary = "<html> <body align=\"justify\" style=\"font-family:Roboto;line-height:20px\">" + webContent + "</body></html>";
        //String summary = "<html> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <body style=\"font-family:Roboto;line-height:20px\">" + webContent + "</body></html>";

        summary = summary.replaceAll("//", "");
        // create text file
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            Log.d("PlaycerApp", "No SDCARD");
        else {
            File direct = new File(Environment.getExternalStorageDirectory()+ "/PlaycerApp");

            if (!direct.exists()) {
                if (direct.mkdir()) {
                    // directory is created;
                }
            }

            try {
                File root = new File(Environment.getExternalStorageDirectory()+ "/PlaycerApp");
                if (root.canWrite()) {
                    File file = new File(root, "PlaycerEventsDetails.html");
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter out = new BufferedWriter(fileWriter);
                    if (summary.contains("<iframe")) {
                        try {
                            int a = summary.indexOf("<iframe");
                            int b = summary.indexOf("</iframe>");
                            summary = summary.replace(
                                    summary.subSequence(a, b), "");
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();

                            }
                        }
                    }
                    out.write(summary);
                    out.close();
                }
            } catch (IOException e) {
                if (e != null) {
                    e.printStackTrace();

                }
            }

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d("PlaycerApp", "No SDCARD");
            } else {
                webView.loadUrl("file://"+ Environment.getExternalStorageDirectory()+ "/PlaycerApp" + "/PlaycerEventsDetails.html");
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.startsWith("tel:")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(intent);
                        } else if (url.startsWith("http:") || url.startsWith("https:")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else if (url.startsWith("mailto:")) {
                            MailTo mt = MailTo.parse(url);
                            Intent i = Utility.hariEmailIntent(ParticipateFullDetailActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                            startActivity(i);
                            view.reload();
                            return true;
                        } else {
                            view.loadUrl(url);
                        }
                        return true;
                    }
                });
            }
        }
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
        titleTV.setText("" + getResources().getString(R.string.tab_participate));

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
    }
}