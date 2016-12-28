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
	private final Context		mContext;
	public boolean				isGPSEnabled					= false;
	boolean						isNetworkEnabled				= false;
	public boolean				canGetLocation					= false;
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
		this.mContext = context;
		getLocation();
	}

	public Location getLocation()
	{
		try
		{
			m_locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = m_locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			log.t("isGPSEnabled", "=" + isGPSEnabled);

			// getting network status
			isNetworkEnabled = m_locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			log.t("isNetworkEnabled", "=" + isNetworkEnabled);

			if (isGPSEnabled == false && isNetworkEnabled == false)
			{
				// no network provider is enabled
			}
			else
			{
				this.canGetLocation = true;
				if (isNetworkEnabled)
				{
					m_location = null;
					m_locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					log.t("Network", "Network");
					if (m_locationManager != null)
					{
						m_location = m_locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (m_location != null)
						{
							m_latitude = m_location.getLatitude();
							m_longitude = m_location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled)
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

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 */
	public boolean canGetLocation()
	{
		return this.canGetLocation;
	}

	/**
	 * Function to show settings alert dialog On pressing Settings button will lauch Settings Options
	 */
	public void showSettingsAlert()
	{
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

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
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub

	}

}// LocationHelper
