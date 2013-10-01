package org.davidlin.sensorloggingutility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

public class SensorDataCollector implements Runnable {

	private static final int MAX_CORES = 8;
	
	public static volatile boolean isRunning = false;

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
		// Read values and write to csv file
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/sensordata");
		dir.mkdirs();
		File file = new File(dir, csvFilename);
		try {
			FileOutputStream f = new FileOutputStream(file);
			// Write headers
			f.write("DateTime,Cpu Temp,Cpu0 Freq,Cp1u Freq,Cpu2 Freq,Cpu3 Freq,Cpu4 Freq,Cpu5 Freq,Cpu6 Freq,Cpu7 Freq,Batt Lvl".getBytes());
			double[] cpuFreq;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
			StringBuilder s = new StringBuilder();
			
			while (isRunning) {
				// Reset StringBuilder
				s.setLength(0);
				// Add datetime
				s.append(sdf.format(new Date()));
				
				if (includeCputemp) {
					s.append("," + getCpuTemperature());
				}
				else {
					s.append(",");
				}
				
				if (includeCpufreq) {
					cpuFreq = getCpuFrequency();
					for (int i = 0; i < cpuFreq.length; i++) {
						s.append("," + cpuFreq[i]);
					}
				}
				else {
					for (int i = 0; i < MAX_CORES; i++) {
						s.append(",");
					}
				}
				
				if (includeBattery) {
					s.append("," + getBatteryLevel());
				}
				
				Log.d(MainActivity.LOGTAG, "Writing " + s);
				f.write(s.toString().getBytes());
				Thread.sleep(sampleRate);
			}
			f.flush();
			f.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get CPU thermal zone temperature from /sys/class/thermal/thermal_zone0/temp
	 * @return	CPU temp
	 */
	public String getCpuTemperature() {
		String cpuTemp = "";
		File file = new File("/sys/class/thermal/thermal_zone0/temp");
		if (file.exists()) {
			try {
				cpuTemp = FileUtils.readFileToString(file);
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
		return level;
	}

}
