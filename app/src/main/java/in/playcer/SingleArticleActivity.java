package in.playcer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import in.playcer.model.SingleNewsArticleData;
import in.playcer.utilities.AppConstants;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/21/2015.
 * at CaretTech
 */
@SuppressLint({ "SetJavaScriptEnabled", "ShowToast" })
public class SingleArticleActivity extends Activity {
    TextView postTitleTV;

    String articleID = "";

    //ProgressDialog mPDialog;
    
    private static final int RECOVERY_DIALOG_REQUEST = 1;

	// YouTube player view

	 int orientation;
	 
	 static int countList = 0;
	 
	 ArrayList<SingleNewsArticleData> singleArticle = new ArrayList<SingleNewsArticleData>();
	 
	ImageView articleThumbs;

	public ProgressDialog mPDialog;

	LinearLayout noNewsNetLL;

	WebView poweredbyWV;
	View poweredbyLine;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.full_article_view);
		overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);
		setupNavigation();
        orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

        articleThumbs = (ImageView) findViewById(R.id.articleImageIVID);
        articleThumbs.getLayoutParams().height = (int) (Utility.screenHeight / 2.9);

		poweredbyWV = (WebView) findViewById(R.id.poweredbyWVID);
		poweredbyLine = (View) findViewById(R.id.lineWVID);
        
        //youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        //youTubeView.getLayoutParams().height = (int)(Utility.screenHeight/2.9);
        
        postTitleTV = (TextView) findViewById(R.id.titleSCTVID);
        //postTitleTV.setTypeface(Utility.font_bold);

        RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
        headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);

        if (getIntent() != null) {
            Bundle dataBundle = getIntent().getExtras();

            if (dataBundle != null) {
				articleID = dataBundle.getString("ARTICLE_ID");
                    try {
						if (Utility.isOnline(SingleArticleActivity.this)) {
							ifNetisAvilableGetNews();
						} else {
							getNewsTroubleConnecting();
						}
					} catch (Exception e) {
						if (e != null) {
							e.printStackTrace();
						}
					}
            }
        }
    }

	private void getNewsTroubleConnecting() {
		noNewsNetLL = (LinearLayout) findViewById(R.id.noNetworkConnectRLID);
		noNewsNetLL.setVisibility(View.VISIBLE);
		Button noNetWorkTryAgainBtn = (Button) findViewById(R.id.noNetWorkTryAgainBtnID);
		noNetWorkTryAgainBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				noNewsNetLL.setVisibility(View.GONE);
				//progressWheel_CENTER_NEWS.setVisibility(View.VISIBLE);
				showPrgressDialogs("Loading Please Wait... ", SingleArticleActivity.this);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						if (Utility.isOnline(SingleArticleActivity.this)) {
							ifNetisAvilableGetNews();
						} else {
							//progressWheel_CENTER_NEWS.setVisibility(View.GONE);
							if (null != mPDialog) {
								mPDialog.dismiss();
								mPDialog = null;
							}
							noNewsNetLL.setVisibility(View.VISIBLE);
						}
					}
				}, 500);
			}
		});
	}

	private void ifNetisAvilableGetNews() {
		showPrgressDialogs("Loading Please Wait... ", SingleArticleActivity.this);
		POST(AppConstants.SINGLE_ARTICLE_URL + "id=" + articleID);
	}

	public void setupNavigation() {
		RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
		headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 11.2);

		RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
		backAllRL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressedAnimationByCHK();
			}
		});

		TextView titleTV = (TextView) findViewById(R.id.titleTVID);
		titleTV.setText("" + getResources().getString(R.string.nav_item_news));

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

		Button moreBtn = (Button) findViewById(R.id.moreBtnID);
		moreBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
		moreBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);
	}

	public void showPrgressDialogs(String _loading, Context _context) {
		mPDialog = new ProgressDialog(_context);
		mPDialog.setMessage(_loading);
		mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mPDialog.setIndeterminate(true);
		mPDialog.setCancelable(false);
		mPDialog.show();
	}
   
    @Override
    protected void onResume() {
    	super.onResume();
    	 orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    }

    public void POST(String url){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
				if (response != null){
					try {
						System.out.println(response);
						String OutputData = "";
						JSONObject jsonResponse;

						try {
							/****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
							jsonResponse = new JSONObject(response);

							/***** Returns the value mapped by name if it exists and is a JSONArray. ***/
							/*******  Returns null otherwise.  *******/
							JSONObject jsonMainNode = jsonResponse.getJSONObject("post");

							/*********** Process each JSON Node ************/
							countList =jsonResponse.length();

							String news_title ="";
							String news_content ="";

							if (countList != 0) {
								singleArticle = new ArrayList<SingleNewsArticleData>();
							}

							//for(int i=0; i < countList; i++)
							//{
								/****** Get Object for each JSON node.***********/
								//JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

								/******* Fetch node values **********/
								int news_id = Integer.parseInt(jsonMainNode.optString("id").toString());
								String news_type = jsonMainNode.optString("type").toString();
								String news_slug = jsonMainNode.optString("slug").toString();
								String news_url = jsonMainNode.optString("url").toString();
								news_title = jsonMainNode.optString("title").toString();
								news_content = jsonMainNode.optString("content").toString();
								String news_excerpt = jsonMainNode.optString("excerpt").toString();
								String news_modified = String.valueOf(jsonMainNode.optString("modified"));
								String news_thumbnail = String.valueOf(jsonMainNode.optString("thumbnail"));
								String poweredby = String.valueOf(jsonMainNode.optString("poweredby"));

								//videosList.add(new NewsCatWiseListData(article_id, user_id,name,alias, details, language, meta_description, image_extension));

								OutputData += "Node : \n\n     "+ news_id +" | " + news_slug +" | " + news_title +" \n\n ";
								Log.i("JSON parse", OutputData);
							//}

							//videoListCount = videoListCount + videosList.size();

							postTitleTV.setText("" + Html.fromHtml(news_title));
							loadInWebView(news_content);

							if (poweredby !=null && poweredby.trim().length()>2){
								loadInWebViewPowerdedBy(poweredby);
							} else {
								poweredbyLine.setVisibility(View.GONE);
								poweredbyWV.setVisibility(View.GONE);
							}

							// articleThumbs.setImageUrl("http://amaraawathi.com/files/article_images/"+article_id+"."+image_extension);

							if (news_thumbnail != null && news_thumbnail.trim().length()>1){
								Picasso.with(SingleArticleActivity.this)
										.load(news_thumbnail)
												//.resize(Utility.screenWidth, (int) (Utility.screenHeight / 3.2))
										.placeholder(R.drawable.placeholder)
										.into(articleThumbs);
							} else {
								articleThumbs.setVisibility(View.GONE);
							}


							if (null != mPDialog) {
								mPDialog.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							getNewsTroubleConnecting();
						}

					} catch (Exception e) {
						e.printStackTrace();
						getNewsTroubleConnecting();
					}
				} else {
					getNewsTroubleConnecting();
				}
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
				getNewsTroubleConnecting();
            	 if (statusCode == 404) {
                 	Utility.showCustomToast(getResources().getString(R.string.request_not_found), SingleArticleActivity.this);
                 } else if (statusCode == 500) {
					 Utility.showCustomToast(getResources().getString(R.string.some_went_wrong), SingleArticleActivity.this);
                 } else {
					 Utility.showCustomToast(getResources().getString(R.string.unexpected_error), SingleArticleActivity.this);
                 }
            }
        });
    }

	private void loadInWebViewPowerdedBy(String poweredby) {
		poweredbyWV.getSettings().setJavaScriptEnabled(true);
		poweredbyWV.getSettings().setAllowFileAccess(true);
		poweredbyWV.getSettings().setLoadsImagesAutomatically(true);
		WebSettings settings = poweredbyWV.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		poweredbyWV.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
		poweredbyWV.setWebViewClient(new WebViewClient() {

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
					Intent i = Utility.hariEmailIntent(SingleArticleActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
					startActivity(i);
					view.reload();
					return true;
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});

		String summary = "<html> <body align=\"justify\" style=\"font-family:Roboto;line-height:20px\">" + poweredby + "</body></html>";
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
					File file = new File(root, "newsPoweredby.html");
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
				poweredbyWV.loadUrl("file://"+ Environment.getExternalStorageDirectory()+ "/PlaycerApp" + "/newsPoweredby.html");
				poweredbyWV.setWebViewClient(new WebViewClient() {

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
							Intent i = Utility.hariEmailIntent(SingleArticleActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
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

	private int getScale(int PIC_WIDTH){
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int width = display.getWidth();
		Double val = new Double(width)/new Double(PIC_WIDTH);
		val = val * 100d;
		return val.intValue();
	}

	private void loadInWebView(String webContent) {
		WebView webView = (WebView) findViewById(R.id.detailsTVID);
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
					Intent i = Utility.hariEmailIntent(SingleArticleActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
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
					File file = new File(root, "PlaycerAppSingleDetails.html");
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
				webView.loadUrl("file://"+ Environment.getExternalStorageDirectory()+ "/PlaycerApp" + "/PlaycerAppSingleDetails.html");
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
							Intent i = Utility.hariEmailIntent(SingleArticleActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
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
}
