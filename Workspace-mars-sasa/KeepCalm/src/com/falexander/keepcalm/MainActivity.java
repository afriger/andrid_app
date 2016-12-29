package com.falexander.keepcalm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.falexander.snakeaf.R;
import com.falexander.util.LocationHelper;
import com.falexander.util.Logger;
import com.falexander.util.PhoneSMSHelper;
import com.falexander.util.PreferencesHelper;
import com.falexander.util.SimpleGestureFilter;
import com.falexander.util.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements SimpleGestureListener, LocationHelper.Callback, PhoneSMSHelper.Callback, MainSettings.Callback
{
	public static final int		REQUEST_CONTACT_NUMBER	= 11011;

	private Logger				log						= Logger.Log;
	PhoneSMSHelper				m_sms					= null;
	final SimpleDateFormat		sdf						= new SimpleDateFormat("dd-MM-yyyy-hh:mm:ss", Locale.getDefault());
	private SimpleGestureFilter	m_gesture_detector		= null;

	LocationHelper				m_gps					= null;
	private ImageButton			m_bntSend				= null;
	private TextView			m_phone_name			= null;
	private TextView			m_phone_number			= null;
	private TextView			m_phone_rescue			= null;
	private View				m_rescue_image			= null;
	private OnClickListener		m_rescue_phone_settings	= null;

	private View				m_settings_screen		= null;;
	private MainSettings		m_settings				= null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_main);

		// Window window = getWindow();
		//
		// window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		//// window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
		// getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//		 getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
//		 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//		
//		WindowManager.LayoutParams params = getWindow().getAttributes();  
//	
//		params.height /= 2;  
//		params.width =500;  
//	
//		this.getWindow().setAttributes(params); 

		
		
		m_gesture_detector = new SimpleGestureFilter(this, this);
		m_settings = new MainSettings(this, this);
		m_gps = new LocationHelper(this, this);
		m_sms = new PhoneSMSHelper(this, this);
		m_sms.Start();

		m_rescue_phone_settings = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				m_settings.MakeRescueContact(MainActivity.this);
			}
		};
		m_settings_screen = findViewById(R.id.main_setings);
		m_settings_screen.setVisibility(View.GONE);
		m_phone_name = (TextView) findViewById(R.id.tvRescuePhoneName);
		m_phone_number = (TextView) findViewById(R.id.tvRescuePhoneNumber);
		m_rescue_image = findViewById(R.id.imageView1);
		m_phone_rescue = (TextView) findViewById(R.id.tvRescuePhone);
		m_rescue_image.setOnClickListener(m_rescue_phone_settings);
		m_phone_rescue.setOnClickListener(m_rescue_phone_settings);
		m_bntSend = (ImageButton) findViewById(R.id.bntSend);
		m_bntSend.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				SendLocation(null);
			}
		});

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		log.t("Started");
		if (null != m_settings_screen)
		{
			m_settings_screen.setVisibility(View.GONE);
		}
		UpdateRescueContact();
		// SendLocation(null);
		if (!m_gps.canGetLocation())
		{
			m_gps.showSettingsAlert();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			if (!m_gps.canGetLocation())
			{
				m_gps.showSettingsAlert();
			}
			m_settings_screen.setVisibility(View.VISIBLE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		m_sms.Stop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK)
		{
			if (data != null && requestCode == REQUEST_CONTACT_NUMBER)
			{
				m_settings.ContactPicked(data);
			}
		}
		else
		{
			log.t("RESULT_CANCEL");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void SpecialBody(final String address)
	{
		SendLocation(address);
	}

	@Override
	public void UpdateLocation()
	{
		// TODO Auto-generated method stub

	}

	private void SendLocation(String address)
	{
		if (null != m_gps && m_gps.canGetLocation())
		{
			m_gps.getLocation();
			String contact_number = (null == address) ? m_phone_number.getText().toString() : address;
			if (null == contact_number || contact_number.isEmpty())
			{
				return;
			}

			StringBuilder sb = new StringBuilder();
			sb.append(sdf.format(new Date())).append('\n').append(m_gps.getLatitude()).append(',').append(m_gps.getLongitude());
			sb.append('\n').append("http://maps.google.com/?q=").append(m_gps.getLatitude()).append(',').append(m_gps.getLongitude());
			m_sms.Send(contact_number, sb.toString());
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		this.m_gesture_detector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public void onSwipe(int direction)
	{
		switch (direction)
		{
			case SimpleGestureFilter.SWIPE_RIGHT:
			case SimpleGestureFilter.SWIPE_LEFT:
			{
				if (m_settings_screen.isShown())
				{
					m_settings_screen.setVisibility(View.GONE);
				}
				break;
			}
			case SimpleGestureFilter.SWIPE_DOWN:
			case SimpleGestureFilter.SWIPE_UP:
			{
				int state = View.VISIBLE;
				if (m_settings_screen.isShown())
				{
					state = View.GONE;
				}
				m_settings_screen.setVisibility(state);
				break;
			}
		}

	}

	@Override
	public void onDoubleTap()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void UpdateRescueContact()
	{
		if (null != m_phone_name)
		{
			String name = PreferencesHelper.Get(this).getString(MainSettings.RESCUE_CONTACT_NAME, "Empty");
			m_phone_name.setText(name);
		}
		if (null != m_phone_number)
		{
			String number = PreferencesHelper.Get(this).getString(MainSettings.RESCUE_CONTACT_NUMBER, "Empty");
			m_phone_number.setText(number);
		}

	}

}// class MainActivity
