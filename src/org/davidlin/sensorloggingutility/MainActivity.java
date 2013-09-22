package org.davidlin.sensorloggingutility;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	
	public final String LOGTAG = "org.davidlin";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void setupActionBar() {
		// Obtain action bar
		final ActionBar aBar = getActionBar();
		// Set action bar to tab mode
		aBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Add tab listener to handle changing of current fragment
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				Log.d(LOGTAG, "onTabReselected");
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// mViewPager.setCurrentItem(tab.getPosition());
				Log.d(LOGTAG, "onTabSelected");
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				Log.d(LOGTAG, "onTabUnselected");
			}

		};
		// Add tabs and register event handlers
		aBar.addTab(aBar.newTab().setText("MAIN").setTabListener(tabListener));
		aBar.addTab(aBar.newTab().setText("SET FREQ").setTabListener(tabListener));
		aBar.addTab(aBar.newTab().setText("SCRIPT").setTabListener(tabListener));
	}

}
