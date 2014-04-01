package com.mdelling.cpuminer;

import android.app.Activity;
import android.app.Application;

public class CPUMinerApplication extends Application {

	private native int detectCPUHasNeon();
	protected native int startMiner(int number, String parameters);
	private native void stopMiner();
	protected native long getAccepted();
	protected native long getRejected();
	protected native int getThreads();
	protected native double getHashRate(int cpu);

	private String textLog = "";
	private Thread worker = null;
	private Thread logger = null;
	private Activity currentActivity = null;

	static {
	    System.loadLibrary("curl");
	    System.loadLibrary("neondetect");
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	    if (detectCPUHasNeon() == 0)
	    {
	    	System.loadLibrary("cpuminer");
	    	appendToTextLog("Loading CPUMiner without NEON support");
	    }
	    else
	    {
	    	System.loadLibrary("cpuminer-neon");
	    	appendToTextLog("Loading CPUMiner with NEON support");
	    }
	}

	public String getTextLog()
	{
		return textLog;
	}

	public void appendToTextLog(String value)
	{
		textLog = textLog + value + "\n";
	}

	public void clearTextLog()
	{
		textLog = "";
	}

	public boolean hasWorker()
	{
		return worker != null;
	}

	public void startWorker()
	{
		if (worker == null) {
			worker = new Thread(new CPUMinerRunnable(this));
			worker.start();
		}
	}

	public void stopWorker()
	{
		if (worker == null)
			return;

		try {
			stopMiner();
			worker.join();
			worker = null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasLogger()
	{
		return logger != null;
	}

	public void startLogger()
	{
		if (logger == null) {
			logger = new Thread(new LoggerRunnable(this));
			logger.start();
		}
	}

	public void stopLogger()
	{
		if (logger == null)
			return;

		try {
			logger.interrupt();
			logger.join();
			logger = null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Activity getCurrentActivity()
	{
		return currentActivity;
	}

	public void setCurrentActivity(Activity activity)
	{
		currentActivity = activity;
	}
}
