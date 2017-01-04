package com.falexander.util;

import android.os.Handler;

public class Utils
{
	// Delay mechanism

	public interface DelayCallback
	{
		void AfterDelay();
	}

	public static void Delay(int secs, final DelayCallback delayCallback)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				delayCallback.AfterDelay();
			}
		}, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.

		//myHandler.postDelayed(myRunnable, SPLASH_DISPLAY_LENGTH); 
		//And this to remove it. myHandler.removeCallbacks(myRunnable);
	}
}// class Utils
