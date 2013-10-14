package org.davidlin.sensorloggingutility;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

	public static final String LOGTAG = "org.davidlin";
	public static Context context;
	
	List<Fragment> tabFragments;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this.getApplicationContext();
		setupActionBar();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	private void setupActionBar() {
		// Add tab fragments into list
		tabFragments = new ArrayList<Fragment>();
		tabFragments.add(new MainFragment());
		tabFragments.add(new SetFreqFragment());
		tabFragments.add(new ScriptFragment());
		// Obtain action bar
		final ActionBar actionBar = getActionBar();
		// Set action bar to tab mode
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Add tab listener to handle changing of current fragment
		ActionBar.TabListener tabListener = new ActionBar.TabListener() {

			@Override
			public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
				//Log.d(LOGTAG, "onTabReselected");
			}

			@Override
			public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
				//Log.d(LOGTAG, "onTabSelected");
				//Log.d(LOGTAG, "The tab position is " + tab.getPosition());
				Fragment fragment = tabFragments.get(tab.getPosition());
				Bundle args = new Bundle();
				args.putInt("", tab.getPosition());
				fragment.setArguments(args);
				getSupportFragmentManager().beginTransaction().add(R.id.fragment1, fragment).commit();
			}

			@Override
			public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
				//Log.d(LOGTAG, "onTabUnselected");
				getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment1)).commit();
			}
		};
		// Add tabs and register event handlers
		actionBar.addTab(actionBar.newTab().setText("MAIN").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("SET FREQ").setTabListener(tabListener));
		actionBar.addTab(actionBar.newTab().setText("SCRIPT").setTabListener(tabListener));
	}

}
