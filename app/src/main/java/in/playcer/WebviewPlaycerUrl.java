package in.playcer;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.playcer.R;

/**
 * Created by PlaycerTeam on 6/30/2016.
 */
public class WebviewPlaycerUrl extends Activity {
    WebView playcerWeburlWVID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weburl_activity);
        playcerWeburlWVID = (WebView) findViewById(R.id.playcerWeburlWVID);
        playcerWeburlWVID.setWebViewClient(new WebViewClient());
        playcerWeburlWVID.getSettings().setJavaScriptEnabled(true);
        playcerWeburlWVID.getSettings().setDomStorageEnabled(true);
        playcerWeburlWVID.loadUrl("http://playcer.in/");

    }
}
