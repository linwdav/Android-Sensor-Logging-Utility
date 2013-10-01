package org.davidlin.sensorloggingutility;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
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
	private static boolean isCputempSelected;
	private static boolean isBatterySelected;
	private static boolean isCpufreqSelected;
	private static Button startStopButton;
	private static CheckBox cpuTempCheckBox;
	private static CheckBox batteryCheckBox;
	private static CheckBox cpuFreqCheckBox;
	private static EditText csvFilenameBox;
	private static EditText sampleRateBox;
	
	private static final int SAMPLE_RATE = 1;
	private static final String START = "Start";
	private static final String STOP = "Stop";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		
		// Set default csv filename
		csvFilenameBox = (EditText) view.findViewById(R.id.etCsvfilename);
		csvFilenameBox.setText(generateCsvName());
		
		// Set default sample rate
		sampleRateBox = (EditText) view.findViewById(R.id.etSamplerate);
		sampleRateBox.setText(String.valueOf(SAMPLE_RATE));
		
		cpuTempCheckBox = (CheckBox) view.findViewById(R.id.cbCputemp);
		cpuTempCheckBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (cpuTempCheckBox.isChecked()) {
					isCputempSelected = true;
				} else {
					isCputempSelected = false;
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
					// Set button text to Start
					startStopButton.setText(START);
					
					// Stop background thread
					
				} else {
					Log.d(MainActivity.LOGTAG, "Temperature selected: " + isCputempSelected);
					Log.d(MainActivity.LOGTAG, "Battery selected: " + isBatterySelected);
					Log.d(MainActivity.LOGTAG, "CPU frequency selected: " + isCpufreqSelected);
					Log.d(MainActivity.LOGTAG, "Sampling rate: " + sampleRateBox.getText());
					Log.d(MainActivity.LOGTAG, "CSV filename: " + csvFilenameBox.getText());
					
					// Set button text to Stop
					startStopButton.setText(STOP);
					
					// Convert samples per second into milliseconds
					double desiredSampleRate = Double.valueOf(sampleRateBox.getText().toString());
					if (desiredSampleRate <= 0) {
						// Throw error
					}
					int sampleRate = 1000 / (int) desiredSampleRate;
					
					// Start background thread to get sensor data
					IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
					Intent batteryStatus = MainActivity.context.registerReceiver(null, ifilter);
					int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
					Log.d(MainActivity.LOGTAG, "Battery percent is " + level);
				}
			}
		});
		return view;
	}
	
	private String generateCsvName() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		
		String monthString = String.valueOf(month);
		String dayString = String.valueOf(day);
		String hourString = String.valueOf(hour);
		String minuteString = String.valueOf(minute);
		String secondString = String.valueOf(second);
		
		if (month < 10) {
			monthString = "0" + month;
		}
		if (day < 10) {
			dayString = "0" + day;
		}
		if (hour < 10) {
			hourString = "0" + hour;
		}
		if (minute < 10) {
			minuteString = "0" + minute;
		}
		if (second < 10) {
			secondString = "0" + second;
		}
		
		String csvName = "sensorlog_" + year + monthString + dayString + "_" + hourString + minuteString + secondString + ".csv";
		return csvName;
	}
	
}
