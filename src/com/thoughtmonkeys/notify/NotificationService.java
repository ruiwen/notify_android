package com.thoughtmonkeys.notify;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import java.lang.Void;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class NotificationService extends AccessibilityService {

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		// TODO Auto-generated method stub
		Log.d("Notify", "Got Event: " + event);
		final int eventType = event.getEventType();
		String eventText = null;
		if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
			Log.d("Notify", "Text: " + event.getText());
			Log.d("Notify", "Type: " + event.getEventType());
			Log.d("Notify", "Package: " + event.getPackageName());
			
			
			// Create socket
			new Notify().execute(new String[] {event.getText().toString(), event.getPackageName().toString()});
			
		}
	}


	private class Notify extends AsyncTask<String, Void, Void> {
	
		@Override
		protected Void doInBackground(String... params) {
	        // Stuff
	    	try {
	    		Log.d("Notify", "Do in background");
				DatagramSocket dsock = new DatagramSocket();
				InetAddress[] addr = InetAddress.getAllByName("192.168.43.169");
				Log.d("Notify", "Addr: " + addr[0]);
				dsock.connect(addr[0], 9000);
				Log.d("Notify", "Connected: " + dsock.isConnected());
				
				// Parse params
				Log.d("Notify", "Parsing..");
				StringBuilder b = new StringBuilder();
				for(String p : params) {
					b.append(p + "|");
				}
				Log.d("Notify", "UDP: " + b.toString());
				DatagramPacket dpack = new DatagramPacket(b.toString().getBytes(), b.toString().length()-1);
				
				Log.d("Notify", "Sending..");
				dsock.send(dpack);
				
			} catch (SocketException e) {
				Log.d("Notify", "SocketException: " + e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				Log.d("Notify", "UnknownHostException: " + e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("Notify", "IOException: " + e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
	    }


		
//	     protected void onPostExecute(Bitmap result) {
//	         mImageView.setImageBitmap(result);
//	     }
	 }

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onServiceConnected() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.feedbackType = 1;
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.notificationTimeout = 100; 
		setServiceInfo(info);
	}
}

