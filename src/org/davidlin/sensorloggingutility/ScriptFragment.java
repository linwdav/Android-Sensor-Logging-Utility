package org.davidlin.sensorloggingutility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScriptFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate and return the layout
		// return inflater.inflate(R.layout.setfreq_fragment, container, false);
		TextView textView = new TextView(getActivity());
		textView.setText("SCRIPT FRAGMENT");
		return textView;
	}
	
}
