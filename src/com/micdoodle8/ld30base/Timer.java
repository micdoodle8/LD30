package com.micdoodle8.ld30base;

import org.lwjgl.Sys;

public class Timer 
{
	private long lastTime;
	private boolean isPaused = false;
	private int deltaTime;
	
	public Timer()
	{
		lastTime = getTime();
	}

	public static long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void update()
	{
		if (!this.isPaused)
		{
			long time = getTime();
		    this.deltaTime = (int) (time - lastTime);
		    lastTime = time;
		}
	}
	
	public int getDelta()
	{
		return this.deltaTime;
	}
	
	public boolean isPaused()
	{
		return isPaused;
	}
	
	public void setPaused(boolean newPaused)
	{
		this.isPaused = newPaused;
	}
}
