package com.falexander.keepcalm;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import com.falexander.util.LocationHelper;
import com.falexander.util.Logger;
import com.falexander.util.PhoneSMSHelper;
import com.falexander.util.PreferencesHelper;
import com.falexander.util.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AlwaysKeepsRunning extends Service implements PhoneSMSHelper.Callback
{
	private final String		tag						= "AlwaysKeepsRunning";
	private Logger				log						= Logger.Log;
	private BroadcastReceiver	m_PowerButtonReceiver	= null;
	private PhoneSMSHelper		m_sms					= null;

	private AtomicBoolean		m_needSend				= new AtomicBoolean(false);
	private LocationHelper		m_gps					= null;

	public AlwaysKeepsRunning()
	{
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		m_sms = new PhoneSMSHelper(this, this);
		m_sms.Start();
		m_gps = KeepCalmApp.GetApp().m_gps;

		// startForeground(LadiesApp.NOTIFICATION_ID, ((LadiesApp)
		// this.getApplication()).Note().build());
		m_PowerButtonReceiver = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent)
			{
				String action = intent.getAction();
				log.t(tag, action);
				if (action.equals(Intent.ACTION_SCREEN_OFF))
				{

				}
				else if (action.equals(Intent.ACTION_SCREEN_ON))
				{
					m_needSend.set(true);
					Utils.Delay(120, new Utils.DelayCallback()
					{
						@Override
						public void AfterDelay()
						{
							if (m_needSend.get())
							{
								log.t(tag, "SendHelp");
								KeepCalmApp.GetApp().SendHelp();
							}
						}
					});
				}
				else if (action.equals(Intent.ACTION_USER_PRESENT))
				{
					m_needSend.set(false);
				}
				return;
			}
		};
		if (null != m_PowerButtonReceiver)
		{
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_USER_PRESENT);
			registerReceiver(m_PowerButtonReceiver, filter);
		}
		KeepCalmApp.GetApp().m_AlwaysKeepsRunning = this;
	}// onReceive

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		log.t(tag, "START_STICKY");
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		log.t(tag, "onDestroy");
		if (null != m_PowerButtonReceiver)
		{
			unregisterReceiver(m_PowerButtonReceiver);
		}
		if (null != m_sms)
		{
			m_sms.Stop();
		}
		super.onDestroy();
	}

	@Override
	public void SpecialBody(String address)
	{
		SendLocation(address);
	}

	protected void SendLocation(String address)
	{
		log.d(tag, "SendLocation", address);
		if (null != m_gps && m_gps.canGetLocation())
		{
			m_gps.getLocation();
			String number = PreferencesHelper.Get(this).getString(MainSettings.RESCUE_CONTACT_NUMBER, null);
			String contact_number = (null == address) ? number : address;
			if (null == contact_number || contact_number.isEmpty())
			{
				return;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(KeepCalmApp.sdf.format(new Date())).append('\n').append(m_gps.getLatitude()).append(',').append(m_gps.getLongitude());
			sb.append('\n').append("http://maps.google.com/?q=").append(m_gps.getLatitude()).append(',').append(m_gps.getLongitude());
			if (KeepCalmApp._DEBUG_)
			{
				log.t("SendLocation",contact_number, sb.toString());
			}
			else
			{
				m_sms.Send(contact_number, sb.toString());
			}
		}
	}

}// class AlwaysKeepsRunning
