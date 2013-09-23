package org.davidlin.sensorloggingutility;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainFragment extends Fragment {
	
	private static boolean isStarted = false;
	private static boolean isTempSelected;
	private static boolean isBatterySelected;
	private static boolean isCpufreqSelected;
	private static Button startStopButton;
	private static CheckBox tempCheckBox;
	private static CheckBox batteryCheckBox;
	private static CheckBox cpuFreqCheckBox;
	private static EditText csvFilenameBox;
	private static EditText sampleRateBox;
	
	private static final int SAMPLE_RATE = 1;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		// Set default csv filename
		csvFilenameBox = (EditText) view.findViewById(R.id.etCsvfilename);
		csvFilenameBox.setText(generateCsvName());
		
		// Set default sample rate
		sampleRateBox = (EditText) view.findViewById(R.id.etSamplerate);
		sampleRateBox.setText(String.valueOf(SAMPLE_RATE));
		
		tempCheckBox = (CheckBox) view.findViewById(R.id.cbTemperature);
		tempCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tempCheckBox.isChecked()) {
					isTempSelected = true;
				} else {
					isTempSelected = false;
				}
			}
		});
		
		batteryCheckBox = (CheckBox) view.findViewById(R.id.cbBattery);
		batteryCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (batteryCheckBox.isChecked()) {
					isBatterySelected = true;
				} else {
					isBatterySelected = false;
				}
			}
		});
		
		cpuFreqCheckBox = (CheckBox) view.findViewById(R.id.cbCpufreq);
		cpuFreqCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cpuFreqCheckBox.isChecked()) {
					isCpufreqSelected = true;
				} else {
					isCpufreqSelected = false;
				}
			}
		});
		
		startStopButton = (Button) view.findViewById(R.id.btStartstop);
		startStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isStarted) {
					
				} else {
					Log.d(MainActivity.LOGTAG, "Temperature selected: " + isTempSelected);
					Log.d(MainActivity.LOGTAG, "Battery selected: " + isBatterySelected);
					Log.d(MainActivity.LOGTAG, "CPU frequency selected: " + isCpufreqSelected);
				}
			}
		});
		return view;
	}
	
	private String generateCsvName() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH));
		String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(cal.get(Calendar.MINUTE));
		String second = String.valueOf(cal.get(Calendar.SECOND));
		
		String csvName = "sensorlog_" + year + month + day + "_" + hour + minute + second + ".csv";
		return csvName;
	}
	
}
