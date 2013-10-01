package org.davidlin.sensorloggingutility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

public class SensorDataCollector implements Runnable {

	private static final int MAX_CORES = 8;
	
	public static double[] cpuFreq;
	public static double batteryLevel;

	private int sampleRate;
	private String csvFilename;
	private boolean includeCputemp;
	private boolean includeCpufreq;
	private boolean includeBattery;

	public SensorDataCollector(int sampleRate, String csvFilename,
			boolean includeCputemp, boolean includeCpufreq,
			boolean includeBattery) {
		this.sampleRate = sampleRate;
		this.csvFilename = csvFilename;
		this.includeCputemp = includeCputemp;
		this.includeCpufreq = includeCpufreq;
		this.includeBattery = includeBattery;
	}

	@Override
	public void run() {

	}

	/**
	 * Get CPU thermal zone temperature from /sys/class/thermal/thermal_zone0/temp
	 * @return	CPU temp
	 */
	public int getCpuTemperature() {
		int cpuTemp = 0;
		File file = new File("/sys/class/thermal/thermal_zone0/temp");
		if (file.exists()) {
			try {
				Log.d(MainActivity.LOGTAG, "CPU temp is " + FileUtils.readFileToString(file));
				cpuTemp = Integer.valueOf(FileUtils.readFileToString(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Log.d(MainActivity.LOGTAG, "Temp file not found");
		}
		return cpuTemp;
	}

	/**
	 * Get CPU frequency from /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq, up to 8 cores.
	 * @return	array of CPU frequencies
	 */
	public double[] getCpuFrequency() {
		double[] cpuFreq = new double[MAX_CORES];
		for (int i = 0; i < MAX_CORES; i++) {
			cpuFreq[i] = 0;
			File file = new File("/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq");
			if (file.exists()) {
				try {
					cpuFreq[i] = Double.valueOf(FileUtils.readFileToString(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cpuFreq;
	}

	/**
	 * Get battery level as an int between 0 to 100.
	 * @return	Battery level
	 */
	public int getBatteryLevel() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = MainActivity.context.registerReceiver(null, ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		Log.d(MainActivity.LOGTAG, "Battery percent is " + level);
		return level;
	}

	
	public void writeToCsv() {
		
	}

}
