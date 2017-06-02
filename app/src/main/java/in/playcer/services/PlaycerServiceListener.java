package in.playcer.services;

/**
 * Created by HARIKRISHNA on 9/16/2015.
 * at CaretTech
 */
public interface PlaycerServiceListener {
	public void onServiceComplete(String response, int eventType);
	public void onServiceFailed(int statusCode, Throwable error, String content, int eventType);
}
