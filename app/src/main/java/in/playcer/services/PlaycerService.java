package in.playcer.services;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import in.playcer.utilities.AppConstants;

/**
 * Created by HARIKRISHNA on 9/16/2015.
 * at CaretTech
 */
public class PlaycerService {
	private static int REQUEST_EVENT_TYPE = -1;
	private PlaycerServiceListener sCServiceListener;
	private static PlaycerService scienceClubService;

	public static PlaycerService getScienceClubService() {
		if (scienceClubService == null) {
			scienceClubService = new PlaycerService();
		}
		return scienceClubService;
	}

	public void sendRequestToServerRegistration(PlaycerServiceListener _hServiceListener, String _Url, RequestParams params, int _eventType) {
		REQUEST_EVENT_TYPE = _eventType;
		this.sCServiceListener = _hServiceListener;
		AsyncHttpClient client = new AsyncHttpClient();
		if (params != null) {
			client.post(_Url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String response) {
					if (response != null) {
						try {					
							sCServiceListener.onServiceComplete(response, REQUEST_EVENT_TYPE);
						} catch (Exception e) {
							e.printStackTrace();
							sCServiceListener.onServiceComplete(AppConstants.UNABLETOESTABLISHCONNECTION_URL, REQUEST_EVENT_TYPE);
						}
					} 
				}

				@Override
				public void onFailure(int statusCode, Throwable error, String content) {
					sCServiceListener.onServiceFailed(statusCode, error, content, REQUEST_EVENT_TYPE);
				}
			});
		} else {
			client.get(_Url, null, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(String response) {
					if (response != null) {
						try {					
							sCServiceListener.onServiceComplete(response, REQUEST_EVENT_TYPE);
						} catch (Exception e) {
							e.printStackTrace();
							sCServiceListener.onServiceComplete(AppConstants.UNABLETOESTABLISHCONNECTION_URL, REQUEST_EVENT_TYPE);
						}
					} 
				}

				@Override
				public void onFailure(int statusCode, Throwable error, String content) {
					sCServiceListener.onServiceFailed(statusCode, error, content, REQUEST_EVENT_TYPE);
				}
			});
		}	
	}

}
