package org.davidlin.sensorloggingutility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.util.Log;

public class SensorDataCollector implements Runnable {

	public static int cpuTemp;
	public static double cpuFreq;
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

	public void getCpuTemperature() {
		File file = new File("/sys/class/thermal/thermal_zone0/temp");
		if (file.exists()) {
			try {
				Log.d(MainActivity.LOGTAG, "The CPU temp is " + FileUtils.readFileToString(file));
				cpuTemp = Integer.valueOf(FileUtils.readFileToString(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Log.d(MainActivity.LOGTAG, "Temp file not found");
			cpuTemp = -9999;
		}
	}

	public void getCpuFrequency() {

	}

	public void getBatteryLevel() {

	}

	public void writeToCsv() {

	}

}
