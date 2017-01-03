package com.falexander.keepcalm;

import java.util.concurrent.atomic.AtomicBoolean;

import com.falexander.util.Logger;
import com.falexander.util.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AlwaysKeepsRunning extends Service
{
	private final String		tag						= "AlwaysKeepsRunning";
	private Logger				log						= Logger.Log;
	private BroadcastReceiver	m_PowerButtonReceiver	= null;
	private AtomicBoolean m_needSend = new AtomicBoolean(false);
	
	public AlwaysKeepsRunning()
	{
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
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
							if(m_needSend.get())
							{
								log.t(tag,"SendHelp");
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

	}//

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
		super.onDestroy();
	}
}// class AlwaysKeepsRunning
