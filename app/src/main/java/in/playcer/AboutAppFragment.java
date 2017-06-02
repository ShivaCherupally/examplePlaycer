package in.playcer;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import in.playcer.libs.ProgressWheel;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.Utility;

/**
 * Created by poliveira on 11/03/2015.
 */
public class AboutAppFragment extends Fragment {
    public static final String TAG = "stats";
    public static RelativeLayout aboutScrollRLID;

    WebView webView;
    LinearLayout noNetLL;
    int repeat = 1;
    private ProgressWheel progressWheel_CENTER;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View _rootView = inflater.inflate(R.layout.about_app_fragment, container, false);
        aboutScrollRLID = (RelativeLayout) _rootView.findViewById(R.id.aboutScrollRLID);
        webView = (WebView) _rootView.findViewById(R.id.aboutAppWVID);
        noNetLL = (LinearLayout) _rootView.findViewById(R.id.noNetworkConnectRLID);
        progressWheel_CENTER = (ProgressWheel) _rootView.findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);

        if (Utility.isOnline(getActivity())) {
            getAboutAppDetails(_rootView);
        } else {
            noNetLL.setVisibility(View.VISIBLE);
            Button noNetWorkTryAgainBtn = (Button) _rootView.findViewById(R.id.noNetWorkTryAgainBtnID);
            noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    progressWheel_CENTER.setVisibility(View.VISIBLE);
                    noNetLL.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (Utility.isOnline(getActivity())) {
                                getAboutAppDetails(_rootView);
                            } else {
                                progressWheel_CENTER.setVisibility(View.GONE);
                                noNetLL.setVisibility(View.VISIBLE);
                            }
                        }
                    }, 500);
                }
            });
        }

        return _rootView;
    }

    private void getAboutAppDetails(View rootView) {
        progressWheel_CENTER.setVisibility(View.VISIBLE);
        getAboutApp(AppConstants.ABOUT_URL);
    }

    private void getAboutApp(String aboutUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(aboutUrl, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println(response);

                    /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                    /*******  Returns null otherwise.  *******/
                    JSONObject jsonMainNode = new JSONObject(response);
                    String status = jsonMainNode.getString("status");
                    String singleAbout = jsonMainNode.getString("page");

                    JSONObject page = new JSONObject(singleAbout);

                    String contentStr = page.getString("content");

                    /************ Show Output on screen/activity **********/
                    loadInWebView(contentStr);
                } catch (JSONException e) {
                    if (e != null) {
                        Log.w("HARI-->DEBUG", "" + e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                if (statusCode == 404) {
                    Utility.showCustomToast(getResources().getString(R.string.request_not_found), getActivity());
                } else if (statusCode == 500) {
                    Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), getActivity());
                } else {
                    Utility.showCustomToast(getResources().getString(R.string.unexpected_error), getActivity());
                }
            }
        });
    }

    private void loadInWebView(String webContent) {
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
                    Intent i = Utility.hariEmailIntent(getActivity(), mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        String summary = "<html><body style=\"font-family:Roboto;line-height:20px\">" + webContent + "</body></html>";

        summary = summary.replaceAll("//", "");
        // create text file
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            Log.d("Playcer", "No SDCARD");
        else {
            File direct = new File(Environment.getExternalStorageDirectory() + "/Playcer");

            if (!direct.exists()) {
                if (direct.mkdir()) {
                    // directory is created;
                }
            }

            try {
                File root = new File(Environment.getExternalStorageDirectory() + "/Playcer");
                if (root.canWrite()) {
                    File file = new File(root, "PlaycerAbout.html");
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
                Log.d("Playcer", "No SDCARD");
            } else {
                webView.loadUrl("file://" + Environment.getExternalStorageDirectory() + "/Playcer" + "/PlaycerAbout.html");
                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        Log.i("WEB_VIEW_TEST", "error code:" + errorCode);

                        super.onReceivedError(view, errorCode, description, failingUrl);
                    }
                });
            }
        }
        progressWheel_CENTER.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
