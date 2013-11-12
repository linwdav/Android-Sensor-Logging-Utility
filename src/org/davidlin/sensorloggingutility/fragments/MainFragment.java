package org.davidlin.sensorloggingutility.fragments;

import java.util.Calendar;
import java.util.Date;

import org.davidlin.sensorloggingutility.MainActivity;
import org.davidlin.sensorloggingutility.R;
import org.davidlin.sensorloggingutility.SensorDataCollector;

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

public class MainFragment extends Fragment implements OnClickListener {
	
	private boolean isCputempSelected;
	private boolean isBatterySelected;
	private boolean isCpufreqSelected;
	private Button startStopButton;
	private CheckBox cpuTempCheckBox;
	private CheckBox batteryCheckBox;
	private CheckBox cpuFreqCheckBox;
	private EditText csvFilenameBox;
	private EditText sampleRateBox;
	private View mainFragmentView;
	private String savedFilename;
	private Context context;
	
	private static Thread dataCollectorThread = null;
	private static SensorDataCollector sdc = null;
	
	private final int SAMPLE_RATE = 1;
	private final String START = "Start";
	private final String STOP = "Stop";
	private final int NOTIFICATION_ID = 999;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainFragmentView = inflater.inflate(R.layout.fragment_main, container, false);
		context = mainFragmentView.getContext();
		setupViews();
		return mainFragmentView;
	}
	
	private void setupViews() {
		cpuTempCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbCputemp);
		batteryCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbBattery);
		cpuFreqCheckBox = (CheckBox) mainFragmentView.findViewById(R.id.cbCpufreq);
		startStopButton = (Button) mainFragmentView.findViewById(R.id.btStartstop);
		cpuTempCheckBox.setOnClickListener(this);
		batteryCheckBox.setOnClickListener(this);
		cpuFreqCheckBox.setOnClickListener(this);
		startStopButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        	case R.id.cbCputemp:
        		if (cpuTempCheckBox.isChecked()) {
					isCputempSelected = true;
				} else {
					isCputempSelected = false;
				}
        		break;
        	case R.id.cbBattery:
        		if (batteryCheckBox.isChecked()) {
					isBatterySelected = true;
				} else {
					isBatterySelected = false;
				}
        		break;
        	case R.id.cbCpufreq:
        		if (cpuFreqCheckBox.isChecked()) {
					isCpufreqSelected = true;
				} else {
					isCpufreqSelected = false;
				}
        		break;
        	case R.id.btStartstop:
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
					sdc = new SensorDataCollector(context, sampleRate, savedFilename, isCputempSelected, isCpufreqSelected, isBatterySelected);
					sdc.isRunning = true;
					dataCollectorThread = new Thread(sdc);
					dataCollectorThread.start();
					Toast toast = Toast.makeText(context, "Writing data to " + savedFilename, Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.BOTTOM, 0, 0);
					toast.setDuration(Toast.LENGTH_LONG);
					toast.show();
					
					addNotification();
				} else {
					// Set button text to Start
					startStopButton.setText(START);
					sdc.isRunning = false;
					try {
						dataCollectorThread.join();
						sdc = null;
						dataCollectorThread = null;
						
						Toast toast = Toast.makeText(context, "Data saved to " + savedFilename, Toast.LENGTH_SHORT);
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
        		break;
        }
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
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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
