package com.thoughtmonkeys.notify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Random;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.thoughtmonkeys.notify.donate.R;

import com.thoughtmonkeys.notify.parsers.NotificationParser;

public class NotificationService extends AccessibilityService {

	// GA tracking
	private Tracker mGaTracker;
	private GoogleAnalytics mGaInstance;

	SharedPreferences pref = null;

	Resources res = null;
	int highPort = 0;  // High port, if we've been told of one 
	int defaultPort = 0; // Default port
	InetAddress cAddr = null;

	@Override
	protected void onServiceConnected() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.feedbackType = 1;
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.notificationTimeout = 5; 
		setServiceInfo(info);
		
		// Initialise other bits
		mGaInstance = GoogleAnalytics.getInstance(this);
		mGaTracker = mGaInstance.getTracker(getString(R.string.ga_trackingId));

		pref = getSharedPreferences(getString(R.string.pref_file), 0);
		res = getResources();
		defaultPort = res.getInteger(R.integer.port_default);

	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {


		// TODO Auto-generated method stub
		// Log.d("Notify", "Got Event: " + event);
		final int eventType = event.getEventType();
		String eventText = null;
		if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
//			Log.d("Notify", "Text: " + event.getText());
//			//Log.d("Notify", "Type: " + event.getEventType());
//			Log.d("Notify", "Package: " + event.getPackageName());
//			Log.d("Notify", "Parcel: " + event.getParcelableData());
			
			
			String title = null;
			String info = null;
			String text = null; //event.getText().toString();

			// Track the event in GA
			mGaTracker.sendEvent("notification", event.getPackageName().toString(), "", 1L);

			
		    try {

				HashMap<String, String> results = NotificationParser.parse(this.getApplicationContext(), event);
				
				Log.d(getString(R.string.log_tag), "res: "+ results);
				
				title = results.get("title");
				text = results.get("text");
				
				Log.d(getString(R.string.log_tag), "title: "+ title);
				Log.d(getString(R.string.log_tag), "text: "+ text);
								
				boolean send = true;
				String packageName = event.getPackageName().toString();
				// Set/check preferences
				try {
				
					// Get a preference editor
					SharedPreferences.Editor prefEdit = pref.edit();
					
					if(pref.contains(packageName)) {
						Log.d("Notify", "Preference for " + packageName + ": " + pref.getBoolean(packageName, true));
						send = pref.getBoolean(packageName, true);
						
						if(!send) {
							// Track the preference block in GA
							mGaTracker.sendEvent("notification_blocked_preference", packageName, "", 1L);
							
							Log.d("Notify", "Send aborted by preference: " + packageName);
						}
					}
					else {
						Log.d("Notify", "Preference for " + packageName + " not found");
						prefEdit.putBoolean(packageName, true);
						prefEdit.apply();
					}
					
					// Check wifi connectivity
					// If it isn't block the send
					if(!isWifiConnected()) { 
						send = false; 
					
						// Track the preference block in GA
						mGaTracker.sendEvent("notification_blocked_wifi", packageName, "", 1L);
						
						Log.d("Notify", "Send aborted by lack of wifi: " + packageName);
					} 
				}
				catch (Exception e) { e.printStackTrace(); }
				
				if(send) {
					// Create socket
					new Notify().execute(new String[] {title, text, packageName.toString()});
				}
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			
		}
	}


	private class Notify extends AsyncTask<String, Void, Void> {
	
//		protected String getWifiIpAddr() {
//		   WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//		   int ip = wifiInfo.getIpAddress();
//
//		   String ipString = String.format(
//		   "%d.%d.%d.%d",
//		   (ip & 0xff),
//		   (ip >> 8 & 0xff),
//		   (ip >> 16 & 0xff),
//		   (ip >> 24 & 0xff));
//
//		   Log.d("Notify", "ipString: " + ipString);
//
//		   return ipString;
//		}
//
//
//		protected NetworkInterface getWifiInterface(InetAddress addr) throws SocketException {
//			return NetworkInterface.getByInetAddress(addr);
//		}	

		protected byte[] munge(String str, String key) {
			// Returns the munged string, or null on error

			byte[] inStr;
			byte[] keyStr;
			byte[] out = null;
			try {
				inStr = str.getBytes("UTF-8");
				keyStr = key.getBytes("UTF-8");
				
			    out = new byte[inStr.length];
			    for (int i = 0; i < inStr.length; i++) {
			        out[i] = (byte) (inStr[i] ^ keyStr[i%keyStr.length]);
			    }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				return null;
			} catch (ArithmeticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				// pass
				return null;
			}
			return out;
		}

		protected InetAddress getBroadcastAddress() throws IOException {
		    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow

		    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    return InetAddress.getByAddress(quads);
		}
	
		protected InetAddress getInetAddress() throws IOException {
		    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		    DhcpInfo dhcp = wifi.getDhcpInfo();
		    // handle null somehow

		    int broadcast = dhcp.ipAddress;
		    byte[] quads = new byte[4];
		    for (int k = 0; k < 4; k++)
		      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		    return InetAddress.getByAddress(quads);
		}
	
		@Override
		protected Void doInBackground(String... params) {
	        
	        DatagramSocket dsock = null;
	        
			// Stuff
	    	try {
	    		//Log.d("Notify", "Do in background");
	    		dsock = new DatagramSocket(defaultPort, getInetAddress());
				dsock.setBroadcast(true);
				
				
				// Parse params
				StringBuilder ob = new StringBuilder();
				ob.append(genSalt() + "|");
				ob.append(res.getString(R.string.header_tag) + "|");
				for(String p : params) {
					ob.append(p + "|");
				}
				
				// XOR output string
				Log.d("Notify", "out: " + ob.toString());
				String key = pref.getString(res.getString(R.string.pwd_key), ""); Log.d("Notify", "key: " + key);
				
				// Hash the key
				MessageDigest hash = MessageDigest.getInstance("MD5");
				hash.update(key.getBytes("UTF-8"));
				String hex = new BigInteger(1, hash.digest()).toString(16);
				Log.d("Notify", "hash: " + hex);
				byte[] munged = munge(ob.toString(), hex); Log.d("Notify", "munged: " + munged.toString());
				byte[] out = Base64.encode(munged, Base64.NO_WRAP); Log.d("Notify", "out: " + out.toString());

				DatagramPacket outpack = new DatagramPacket(
                        out, 
                        out.length, 
                        getBroadcastAddress(), defaultPort);
				
				
				Log.d("Notify", "Preparing to send");

				dsock.send(outpack);

//				if(highPort > 0 && cAddr != null) {
//					// Looks like we have what we need, let's send
//					dsock.send(outpack);
//				}
//				else { // No high port, let's ask for one
//				
//					StringBuilder b = new StringBuilder();
//					b.append(res.getString(R.string.header_tag) + "|");
//					b.append(res.getString(R.string.handshake_tag));
//				
//					// TODO: XOR output string
//				
//					Log.d("Notify", "Sending handshake");
//					Log.d("Notify", "Handshake: " + b.toString());
//				
//					DatagramPacket dpack = new DatagramPacket(
//					                               b.toString().getBytes("UTF-8"),
//					                               b.toString().getBytes("UTF-8").length,
//					                               getBroadcastAddress(), defaultPort);
//					
//					// Set the timeout
//					dsock.setSoTimeout(5000);
//									
//					// Send the handshake
//					dsock.send(dpack);
//
//					
//					// Craft an empty DatagramPacket and wait for a reply
//					dsock.setReceiveBufferSize(400);
//					byte[] data = new byte[400];
//					DatagramPacket rcvpack = new DatagramPacket(data, data.length);
//					dsock.receive(rcvpack);
//					
//					// Process the output - export a port number
//					String response = (new String(rcvpack.getData(), "UTF-8")).trim(); 
//					
//					
//					// TODO: XOR incoming string
//					String[] items = response.split("\\|"); 
//					if(items[0].equals(res.getString(R.string.header_tag))) {
//						// We've got a header tag, so parse the highPort
//						highPort = Integer.parseInt(items[1]);
//					
//						// Set the client address
//						cAddr = rcvpack.getAddress();
//					
//						// Now that we have the highPort, we can send the notification
//						Log.d("Notify", "Sending for real");
//						outpack.setAddress(cAddr);
//						outpack.setPort(highPort);
//						dsock.send(outpack);
//					}
//				
//				}
				
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
				
				// Something likely went wrong with the sending
				// reset the target address and high port, and
				// perform the request again on the next notification
				cAddr = null;
				highPort = 0;
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
			
				// Close the socket on each run
				dsock.close();
			}
			
			return null;
	    }


		
//	     protected void onPostExecute(Bitmap result) {
//	         mImageView.setImageBitmap(result);
//	     }
	 }

	// Utils
	
	protected boolean isWifiConnected() {
		// Tells us if Wifi is connected and is able to form connections
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return mWifi.isConnected();
	}


	protected String genSalt() throws NoSuchAlgorithmException {
		
		// Define a list of allowed characters
		final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

		// Generates a random-length (between 1-13 chars) salt
		Random r = new Random();
		int length = r.nextInt(12) + 1;  // +1 so that length will never be 0
				
		char[] genSalt = new char[length];
		for(int i = 0; i < length; i++) {
			genSalt[i] = ALLOWED_CHARACTERS.charAt(r.nextInt(ALLOWED_CHARACTERS.length()));
		}
		
		return new String(genSalt);
	}

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}
	
}

