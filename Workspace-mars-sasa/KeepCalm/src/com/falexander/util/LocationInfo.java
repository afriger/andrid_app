package com.falexander.util;

import java.text.DecimalFormat;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationInfo implements LocationListener
{
	private final long			minTime				= 5000;
	private final float			minDistance			= 0;						// 1;
	private final DecimalFormat	_df					= new DecimalFormat("#.##");
	private Location			_previousLocation	= null;
	private float				_speed				= 0;
	private float				_calcSpeed			= 0;

	public interface Callback
	{
		void UpdateLocation();
	}

	private Callback mCallback = null;

	public LocationInfo(Context context, Callback Callback)
	{
		mCallback = Callback;
		Start(context);
	}

	public void Start(Context context)
	{
		if (null == context)
		{
			return;
		}
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (null == locationManager)
		{
			return;
		}
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
		}
		else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
		}
		_previousLocation = LastKnownLocation(locationManager);
	}

	Location LastKnownLocation(LocationManager locationManager)
	{
		Location location = null;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		location = locationManager.getLastKnownLocation(bestProvider);
		if (location != null)
		{
			Logger.Log.t("Location", bestProvider, location.getLongitude() + " " + location.getLatitude());
		}
		return location;
	}


	public String GetInfo()
	{

		String info = "";
		if (null != _previousLocation)
		{
			info += _previousLocation.getLatitude() + ",";
			info += _previousLocation.getLongitude() + "\n";
		}
		info += _df.format(_speed) + "[m/s]\n";
		info += _df.format(_calcSpeed) + "[m/s]\n";
		return info;
	}

	@Override
	public void onLocationChanged(Location location)
	{
		if (location.hasSpeed())
		{
			_speed = location.getSpeed();
		}
		else
		{
			_speed = -1;
		}
		if (null != _previousLocation)
		{
			float distance = location.distanceTo(_previousLocation);// [m]
			float timeTaken = ((location.getTime() - _previousLocation.getTime()) / 1000);// [sec]
			if (timeTaken > 0)
			{
				_calcSpeed = distance / timeTaken;
				// Logger.Log.AppendToFile("d: " + distance + "; t: " +
				// timeTaken + "; v: " + _calcSpeed+ " - "+ _speed);
			}
		}
		_previousLocation = location;
		if (null != mCallback)
		{
			mCallback.UpdateLocation();
		}
		// String geoUri = String.format("geo:%s,%s?z=15",
		// Double.toString(location.getLatitude()),
		// Double.toString(location.getLongitude()));
		// Uri geo = Uri.parse(geoUri);
		// Intent geoIntent = new Intent(Intent.ACTION_VIEW, geo);
		// MainActivity.This.startActivity(geoIntent);

	}

	@Override
	public void onProviderDisabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		// TODO Auto-generated method stub

	}

}// class LocationInfo
