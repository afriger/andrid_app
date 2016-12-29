package com.falexander.keepcalm;

import com.falexander.snakeaf.R;
import com.falexander.util.PreferencesHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class MainSettings
{
	public static final String	RESCUE_CONTACT_NUMBER	= "rescue_contact_number";
	public static final String	RESCUE_CONTACT_NAME		= "rescue_contact_name";
	private final Activity		m_activity;
	private String				m_contact_name			= "";
	private String				m_contact_phone			= "";
	private EditText			m_edit_name				= null;
	private EditText			m_edit_phone			= null;

	public interface Callback
	{
		void UpdateRescueContact();
	}

	private Callback mCallback = null;

	public MainSettings(Activity activity, Callback callback)
	{
		m_activity = activity;
		mCallback = callback;
	}

	public void MakeRescueContact(final Context context)
	{
		final ViewGroup nullParent = null;
		LayoutInflater li = LayoutInflater.from(context);
		final View promptsView = li.inflate(R.layout.input_dialog, nullParent);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Rescue Phone");

		builder.setView(promptsView);

		m_edit_name = (EditText) promptsView.findViewById(R.id.edMakeName);
		m_edit_phone = (EditText) promptsView.findViewById(R.id.edMakeNumber);

		String name = PreferencesHelper.Get(context).getString(MainSettings.RESCUE_CONTACT_NAME, "Empty");
		m_edit_name.setText(name);
		String number = PreferencesHelper.Get(context).getString(MainSettings.RESCUE_CONTACT_NUMBER, "Empty");
		m_edit_phone.setText(number);

		// Set up the buttons
		View ContactsCall = promptsView.findViewById(R.id.imbntContacts);
		ContactsCall.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				onBrowseForNumbersButtonClicked();
			}
		});

		builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if (null != m_edit_name)
				{
					PreferencesHelper.Edit(context).putString(RESCUE_CONTACT_NAME, m_edit_name.getText().toString()).commit();
				}
				if (null != m_edit_phone)
				{
					PreferencesHelper.Edit(context).putString(RESCUE_CONTACT_NUMBER, m_edit_phone.getText().toString()).commit();
				}
				if (null != mCallback)
				{
					mCallback.UpdateRescueContact();
				}
				m_edit_name = null;
				m_edit_phone = null;
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.cancel();
				m_edit_name = null;
				m_edit_phone = null;
			}
		});

		builder.show();
	}

	private void onBrowseForNumbersButtonClicked()
	{
		if (null != m_activity)
		{
			Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
			m_activity.startActivityForResult(contactPickerIntent, MainActivity.REQUEST_CONTACT_NUMBER);
		}
	}

	public void ContactPicked(Intent data)
	{
		ContentResolver cr = m_activity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		cur.moveToFirst();
		try
		{
			Uri uri = data.getData();
			cur = m_activity.getContentResolver().query(uri, null, null, null, null);
			cur.moveToFirst();
			m_contact_name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			m_contact_phone = "Empty";
			String idOfPhoneRecord = uri.getLastPathSegment();
			Cursor cursor = m_activity.getContentResolver().query(Phone.CONTENT_URI, new String[] { Phone.NUMBER }, Phone._ID + "=?", new String[] { idOfPhoneRecord }, null);
			if (cursor != null)
			{
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					m_contact_phone = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
				}
				cursor.close();
			}
			if (null != m_edit_name)
			{
				m_edit_name.setText(m_contact_name);
			}
			if (null != m_edit_phone)
			{
				m_edit_phone.setText(m_contact_phone);
			}
			return;
		}
		catch (Exception e)
		{
		}

	}
}// class MainSettings
