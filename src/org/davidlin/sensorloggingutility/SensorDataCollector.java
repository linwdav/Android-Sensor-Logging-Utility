package org.davidlin.sensorloggingutility;

public class SensorDataCollector implements Runnable {
	
	public static int cpuTemp;
	public static double cpuFreq;
	public static double batteryLevel;
	
	private int sampleRate;
	private String csvFilename;
	private boolean includeCputemp;
	private boolean includeCpufreq;
	private boolean includeBattery;
	
	public SensorDataCollector(int sampleRate, String csvFilename, boolean includeCputemp, boolean includeCpufreq, boolean includeBattery) {
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
		
	}
	
	public void getCpuFrequency() {
		
	}
	
	public void getBatteryLevel() {
		
	}
	
	public void writeToCsv() {
		
	}

}
