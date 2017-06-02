package in.playcer;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import in.playcer.libs.PagerSlidingTabStrip;
import in.playcer.utilities.Utility;

/**
 * Created by HARIKRISHNA on 8/24/2015. at CaretTech
 */
@SuppressLint("ShowToast")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyBookingsEventsSlotsActivity extends FragmentActivity {// implements ViewPager.OnPageChangeListener {
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bookings_tabs);
		overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
		Utility.setDimensions(this);

		RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
		headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 11.2);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setUnderlineHeight(1);
		tabs.setIndicatorHeight(5);
		//tabs.setDividerWidth(1);
		//tabs.setDividerColor(getResources().getColor(R.color.tabs_text));
		tabs.getLayoutParams().height = (int) (Utility.screenHeight / 10.6);
		pager = (ViewPager) findViewById(R.id.pager);
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// nothing
				Log.v("HARI--> ", "" + arg0);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				Log.v("HARI--> ", "Scrolled Pos->" + position);
			}

			@Override
			public void onPageSelected(int position) {
				Log.v("HARI-->", "Selected Pos->" + position);
			}
		});

		ArrayList<String> tabNames = new ArrayList<String>();
		tabNames.add("SPORTS");
		tabNames.add("EVENTS");

		adapter = new MyPagerAdapter(getSupportFragmentManager(), tabNames);
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		tabs.setVisibility(View.VISIBLE);

		setupNavigation();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		GoogleAnalytics.getInstance(MyBookingsEventsSlotsActivity.this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		GoogleAnalytics.getInstance(MyBookingsEventsSlotsActivity.this).reportActivityStop(this);
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		ArrayList<String> mTabsTitlesList;

		public MyPagerAdapter(FragmentManager fm, ArrayList<String > _mTabsTitlesList) {
			super(fm);
			this.mTabsTitlesList = _mTabsTitlesList;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (mTabsTitlesList != null) {
				if (mTabsTitlesList.size() > 0) {
					return mTabsTitlesList.get(position);
				}
			}
			return null;
		}

		@Override
		public int getCount() {
			return mTabsTitlesList.size();
		}

		@Override
		public Fragment getItem(int position) {
			if (position == 0) {
				return MyBooksSlotsListFragment.newInstance();
			} else {
				return MyBooksParticipateListFragment.newInstance();
			}
		}		
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
		titleTV.setText("MY BOOKINGS");

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

		Button cartViewIcon = (Button) findViewById(R.id.moreBtnID);
		cartViewIcon.setVisibility(View.GONE);
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