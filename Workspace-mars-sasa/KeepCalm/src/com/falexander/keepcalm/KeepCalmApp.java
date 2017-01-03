package com.falexander.keepcalm;

import android.app.Application;
import android.content.Intent;

public class KeepCalmApp extends Application
{
	
	private static KeepCalmApp	m_app;
	private MainActivity		m_mainActivity	= null;

	public static KeepCalmApp GetApp()
	{
		return m_app;
	}

	public KeepCalmApp()
	{
		m_app = this;
	}

	void SetMainActivity(MainActivity mainActivity)
	{
		m_mainActivity = mainActivity;
	}

	void SendHelp()
	{
		if (null != m_mainActivity)
		{
			m_mainActivity.SendLocation(null);
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Intent AlwaysKeepsRunning = new Intent(this, AlwaysKeepsRunning.class);
		startService(AlwaysKeepsRunning);
	}

}// class KeepCalmApp
