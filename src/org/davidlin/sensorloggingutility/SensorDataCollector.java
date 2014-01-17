package org.davidlin.sensorloggingutility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

public class SensorDataCollector implements Runnable {

	private static final int MAX_CORES = 8;

	private volatile boolean isRunning = false;
	private final Object lockObj = new Object();

	private int sampleRate;
	private String csvFilename;
	private boolean includeCputemp;
	private boolean includeCpufreq;
	private boolean includeBattery;
	private Context context;

	public SensorDataCollector(Context context, int sampleRate, String csvFilename, boolean includeCputemp, boolean includeCpufreq, boolean includeBattery) {
		this.context = context;
		this.sampleRate = sampleRate;
		this.csvFilename = csvFilename;
		this.includeCputemp = includeCputemp;
		this.includeCpufreq = includeCpufreq;
		this.includeBattery = includeBattery;
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	/**
	 * Read sensor values in periodic intervals and write to csv file.
	 */
	public void run() {
		// Read values and write to csv file
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/sensordata");
		dir.mkdirs();
		File file = new File(dir, csvFilename);

		CSVWriter writer;
		try {
			writer = new CSVWriter(new FileWriter(file), ',');
			String[] entries = "DateTime,Cpu Temp,Cpu0 Freq,Cpu1 Freq,Cpu2 Freq,Cpu3 Freq,Cpu4 Freq,Cpu5 Freq,Cpu6 Freq,Cpu7 Freq,Batt Lvl".split(",");
			writer.writeNext(entries);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			double[] cpuFreq;
			isRunning = true;
			
			while (isRunning) {
				Log.d("org.davidlin.sensor", "Executing data collecting loop");
				StringBuilder s = new StringBuilder();
				s.append(sdf.format(new Date()) + ",");

				if (includeCputemp) {
					s.append(getCpuTemperature() + ",");
				} else {
					s.append("N/A,");
				}

				if (includeCpufreq) {
					cpuFreq = getCpuFrequency();
					for (int i = 0; i < cpuFreq.length; i++) {
						s.append(cpuFreq[i] + ",");
					}
				} else {
					for (int i = 0; i < MAX_CORES; i++) {
						s.append("N/A,");
					}
				}

				if (includeBattery) {
					s.append(getBatteryLevel() + ",");
				} else {
					s.append("N/A");
				}

				entries = s.toString().split(",");
				writer.writeNext(entries);
				
				synchronized(lockObj) {
					if (isRunning) {
						lockObj.wait(sampleRate);
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// Do nothing, continue
		}
	}
	
	public void stop() {
		synchronized(lockObj) {
			isRunning = false;
			lockObj.notify();
		}
	}

	/**
	 * Get CPU thermal zone temperature from
	 * /sys/class/thermal/thermal_zone0/temp
	 * 
	 * @return CPU temp
	 */
	public String getCpuTemperature() {
		String cpuTemp = "";
		File file = new File("/sys/class/thermal/thermal_zone0/temp");
		if (file.exists()) {
			try {
				cpuTemp = FileUtils.readFileToString(file);
				cpuTemp = cpuTemp.trim();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.d("org.davidlin.sensor", "Temp file not found");
		}
		return cpuTemp;
	}

	/**
	 * Get CPU frequency from
	 * /sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq, up to 8 cores.
	 * 
	 * @return array of CPU frequencies
	 */
	public double[] getCpuFrequency() {
		double[] cpuFreq = new double[MAX_CORES];
		for (int i = 0; i < MAX_CORES; i++) {
			cpuFreq[i] = 0;
			File file = new File("/sys/devices/system/cpu/cpu" + i
					+ "/cpufreq/scaling_cur_freq");
			if (file.exists()) {
				try {
					cpuFreq[i] = Double.valueOf(FileUtils
							.readFileToString(file));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cpuFreq;
	}

	/**
	 * Get battery level as an int between 0 to 100.
	 * 
	 * @return Battery level
	 */
	public int getBatteryLevel() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = context.registerReceiver(null,
				ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		return level;
	}
	
}
