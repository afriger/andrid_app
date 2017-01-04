package com.falexander.keepcalm;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.falexander.util.LocationHelper;

import android.app.Application;
import android.content.Intent;

public class KeepCalmApp extends Application implements  LocationHelper.Callback
{
	static final SimpleDateFormat	sdf						= new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss", Locale.getDefault());
	private static KeepCalmApp		m_app					= null;
	private MainActivity			m_mainActivity			= null;
	AlwaysKeepsRunning				m_AlwaysKeepsRunning	= null;
	public LocationHelper			m_gps					= null;
	static boolean					_DEBUG_					= true;

	public static KeepCalmApp GetApp()
	{
		return m_app;
	}

	public KeepCalmApp()
	{
		m_app = this;
		m_gps = new LocationHelper(this, this);

	}

	void SetMainActivity(MainActivity mainActivity)
	{
		m_mainActivity = mainActivity;
	}

	static MainActivity MainA()
	{
		return m_app.m_mainActivity;
	}

	static LocationHelper gps()
	{
		return m_app.m_gps;
	}

	void SendHelp()
	{
		if (null != m_AlwaysKeepsRunning)
		{
			m_AlwaysKeepsRunning.SendLocation(null);
		}
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		Intent AlwaysKeepsRunning = new Intent(this, AlwaysKeepsRunning.class);
		startService(AlwaysKeepsRunning);
	}

	@Override
	public void UpdateLocation()
	{
		// TODO Auto-generated method stub
		
	}

}// class KeepCalmApp
