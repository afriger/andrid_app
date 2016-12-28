package com.falexander.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper
{
	private static final String MY_PREFS_NAME = "KEEP_CALM";

	public static SharedPreferences.Editor Edit(Context context)
	{
		return Get(context).edit();
	}

	public static SharedPreferences Get(Context context)
	{
		return context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
	}

}// class PreferencesHelper
