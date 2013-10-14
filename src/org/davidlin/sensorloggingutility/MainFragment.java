package org.davidlin.sensorloggingutility;

import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainFragment extends Fragment {
	
	private static boolean isCputempSelected;
	private static boolean isBatterySelected;
	private static boolean isCpufreqSelected;
	private static Button startStopButton;
	private static CheckBox cpuTempCheckBox;
	private static CheckBox batteryCheckBox;
	private static CheckBox cpuFreqCheckBox;
	private static EditText csvFilenameBox;
	private static EditText sampleRateBox;
	private View mainFragmentView;
	private String savedFilename;
	
	private static Thread dataCollectorThread = null;
	private static SensorDataCollector sdc = null;
	
	private static final int SAMPLE_RATE = 1;
	private static final String START = "Start";
	private static final String STOP = "Stop";
	private static final int NOTIFICATION_ID = 999;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
		
		cpuTempCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbCputemp);
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
		
		batteryCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbBattery);
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
		
		cpuFreqCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbCpufreq);
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
		
		startStopButton = (Button) mainFragmentView.findViewById(R.id.btStartstop);
		startStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if (sdc == null && dataCollectorThread == null) {
					disableOptions();
					// Set button text to Stop
					startStopButton.setText(STOP);
					// Convert samples per second into milliseconds
					double desiredSampleRate = Double.valueOf(sampleRateBox.getText().toString());
					if (desiredSampleRate <= 0) {
						// Throw error
					}
					int sampleRate = (int) (1000 / desiredSampleRate);
					savedFilename = csvFilenameBox.getText().toString();
					sdc = new SensorDataCollector(sampleRate, savedFilename, isCputempSelected, isCpufreqSelected, isBatterySelected);
					sdc.isRunning = true;
					dataCollectorThread = new Thread(sdc);
					dataCollectorThread.start();
					Toast toast = Toast.makeText(MainActivity.context, "Writing data to " + savedFilename, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.show();
					
					addNotification();
				}
				else {
					// Set button text to Start
					startStopButton.setText(START);
					sdc.isRunning = false;
					try {
						dataCollectorThread.join();
						sdc = null;
						dataCollectorThread = null;
						
						Toast toast = Toast.makeText(MainActivity.context, "Data saved to " + savedFilename, Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.BOTTOM, 0, 0);
						toast.setDuration(Toast.LENGTH_LONG);
						toast.show();
						
						// Set default csv filename
			 		 	csvFilenameBox = (EditText) mainFragmentView.findViewById(R.id.etCsvfilename);
			 		 	csvFilenameBox.setText(generateCsvName());
			 		 	
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					enableOptions();
					removeNotification();
				}
			}
		});
		return mainFragmentView;
	}
	
	@Override
	public void onResume() {
	    super.onResume();
 		if (sdc == null && dataCollectorThread == null) {			
 			// Set default csv filename
 		 	csvFilenameBox = (EditText) mainFragmentView.findViewById(R.id.etCsvfilename);
 		 	csvFilenameBox.setText(generateCsvName());
 		 		
 	 		// Set default sample rate
 	 		sampleRateBox = (EditText) mainFragmentView.findViewById(R.id.etSamplerate);
 	 		sampleRateBox.setText(String.valueOf(SAMPLE_RATE));
 		}
	}
	
	private void addNotification() {
		Context context = MainActivity.context;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher);      

		Intent intent = new Intent(context, MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		builder.setContentIntent(pIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		Notification notif = builder.setContentTitle("Sensor Logging Utility").build();
		notif.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(NOTIFICATION_ID, notif);
	}
	
	private void removeNotification() {
		NotificationManager mNotificationManager = (NotificationManager) MainActivity.context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(NOTIFICATION_ID);
	}
	
	private void disableOptions() {
		cpuTempCheckBox.setEnabled(false);
		cpuFreqCheckBox.setEnabled(false);
		batteryCheckBox.setEnabled(false);
		sampleRateBox.setEnabled(false);
		csvFilenameBox.setEnabled(false);
	}
	
	private void enableOptions() {
		cpuTempCheckBox.setEnabled(true);
		cpuFreqCheckBox.setEnabled(true);
		batteryCheckBox.setEnabled(true);
		sampleRateBox.setEnabled(true);
		csvFilenameBox.setEnabled(true);
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
