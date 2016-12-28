package com.falexander.util;

import com.falexander.util.Logger.Severity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

public class LocationHelper implements LocationListener
{

	private Logger				log								= Logger.Log;
	private final Context		m_context;
	private boolean				m_isGPSEnabled					= false;
	boolean						m_isNetworkEnabled				= false;
	Location					m_location;
	double						m_latitude;
	double						m_longitude;

	// The minimum distance to change Updates in meters
	private static final long	MIN_DISTANCE_CHANGE_FOR_UPDATES	= 1;			// 10 meters
	// The minimum time between updates in milliseconds
	private static final long	MIN_TIME_BW_UPDATES				= 1;			// 1 minute
	// Declaring a Location Manager
	protected LocationManager	m_locationManager;

	public interface Callback
	{
		void UpdateLocation();
	}

	private Callback mCallback = null;

	public LocationHelper(Context context, Callback Callback)
	{
		log.SetSeverity(Severity.trace);
		mCallback = Callback;
		this.m_context = context;
		getLocation();
	}

	public Location getLocation()
	{
		try
		{
			m_locationManager = (LocationManager) m_context.getSystemService(Context.LOCATION_SERVICE);
			// getting GPS status
			m_isGPSEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			log.t("isGPSEnabled", "=" + m_isGPSEnabled);
			// getting network status
			m_isNetworkEnabled = m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			log.t("isNetworkEnabled", "=" + m_isNetworkEnabled);
			if (m_isGPSEnabled == false && m_isNetworkEnabled == false)
			{
				// no network provider is enabled
			}
			else
			{
				if (m_isNetworkEnabled)
				{
					m_location = null;
					m_locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					log.t("Network", "Network");
					if (m_locationManager != null)
					{
						m_location = m_locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (m_location != null)
						{
							m_latitude = m_location.getLatitude();
							m_longitude = m_location.getLongitude();
						}
					}
				}

				if (m_isGPSEnabled)
				{
					m_location = null;
					if (m_location == null)
					{
						m_locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						log.t("GPS Enabled", "GPS Enabled");
						if (m_locationManager != null)
						{
							m_location = m_locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (m_location != null)
							{
								m_latitude = m_location.getLatitude();
								m_longitude = m_location.getLongitude();
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return m_location;
	}

	public void stopUsingGPS()
	{
		if (m_locationManager != null)
		{
			m_locationManager.removeUpdates(LocationHelper.this);
		}
	}

	public double getLatitude()
	{
		if (m_location != null)
		{
			m_latitude = m_location.getLatitude();
		}
		return m_latitude;
	}

	public double getLongitude()
	{
		if (m_location != null)
		{
			m_longitude = m_location.getLongitude();
		}
		return m_longitude;
	}

	public boolean canGetLocation()
	{
		if (null == m_locationManager)
		{
			m_locationManager = (LocationManager) m_context.getSystemService(Context.LOCATION_SERVICE);
		}
		if (null == m_locationManager)
		{
			return false;
		}
		m_isGPSEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		m_isNetworkEnabled = m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		log.t("isNetworkEnabled", "=" + m_isNetworkEnabled);
		return !(m_isGPSEnabled == false && m_isNetworkEnabled == false);
	}

	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(m_context);
		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");
		// Setting Dialog Message
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						m_context.startActivity(intent);
					}
				});
		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location arg0)
	{
		if (null != mCallback)
		{
			mCallback.UpdateLocation();
		}
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
	}

	@Override
	public void onProviderEnabled(String arg0)
	{
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
	}

}// class LocationHelper
