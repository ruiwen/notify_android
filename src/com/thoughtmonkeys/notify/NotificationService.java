package com.thoughtmonkeys.notify;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;


public class NotificationService extends AccessibilityService {

	SharedPreferences pref = null;

	Resources res = null;
	int highPort = 0;  // High port, if we've been told of one 
	int defaultPort = 0; // Default port
	InetAddress cAddr = null;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {

		pref = getSharedPreferences(getString(R.string.pref_file), 0);

		res = getResources();
		defaultPort = res.getInteger(R.integer.port_default);

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

			
		    try {
		        
				// UDP broadcast code obtained from
				// https://code.google.com/p/boxeeremote/wiki/AndroidUDP
				Notification notification = (Notification) event.getParcelableData();
				Log.d("Notify", "notification: " + notification);
				Log.d("Notify", "tickerText: " + notification.tickerText);
				
						
				RemoteViews views = notification.contentView;
//				RemoteViews views = notification.tickerView;
				Class secretClass = views.getClass();
		        
			        
		        Map<Integer, String> txt = new HashMap<Integer, String>();

		        Field outerFields[] = secretClass.getDeclaredFields();
		        for (int i = 0; i < outerFields.length; i++) {
		            if (!outerFields[i].getName().equals("mActions")) continue;

		            outerFields[i].setAccessible(true);

	                Object value = null;
	                Integer type = null;
	                Integer viewId = null;
	                
	                ArrayList<Object> actions = (ArrayList<Object>) outerFields[i].get(views);
		            for (Object action : actions) {
		                Field innerFields[] = action.getClass().getDeclaredFields();

		                for (Field field : innerFields) {
		                    field.setAccessible(true);
		                    
		                    try {
			                    if (field.getName().equals("value")) {
			                        value = field.get(action);
			                        Log.d("Notify", "value: " + value.toString());
			                    } 
			                    if (field.getName().equals("type")) {
			                        type = field.getInt(action);
			                    }
			                    if (field.getName().equals("viewId")) {
			                        viewId = field.getInt(action);
			                    }
		                    
		                    }
		                    catch(Exception e) { e.printStackTrace(); }

		                }

		                if (type != null && (type == 9 || type == 10)) {
		                    

		                	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH &&
		                			Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			                    txt.put(viewId, value.toString());
			                    
			                    // Set title
					            for(int item :  new int[]{16908310, 2131230870, 2131231209}) {
					            	if(txt.get(item) != null) {
					            		title = txt.get(item);
					            		//break;
					            	}
					            }
					            		                    
			                    // Set text
				            	for(int item : new int[] {16908358, 2131230787, 2131231210}) {
				            		if(txt.get(item) != null && !txt.get(item).equals('0')) {
				            			text = txt.get(item);
				            			//break;
				            		}
				            	}
		                	}
		                	
		                	else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		                		
	                			if(title == null) {
	                				title = value.toString();
	                			}
	                			
	                			else if(text == null) {
	                				text = value.toString();
	                			}
		                	}
		                    
		                    Log.d("Notify", "title: " + title + " " + "text: " + text);
		                    
		                }
		                

//			            info = txt.get(16909082);
			    
		            	
		            	// Abort if we've filled both title and text
		            	if(title != null && text != null) {
		            		break;
		            	}
		            }

		            
		        }

				
				boolean send = true;
				String packageName = event.getPackageName().toString();
				// Set/check preferences
				try {
				
					// Get a preference editor
					SharedPreferences.Editor prefEdit = pref.edit();
//					PackageManager pm = getPackageManager();
//					ApplicationInfo appInfo = pm.getApplicationInfo(event.getPackageName().toString(), PackageManager.GET_META_DATA);
//					String appName = (String) pm.getApplicationLabel(appInfo);
					
					
					if(pref.contains(packageName)) {
						Log.d("Notify", "Preference for " + packageName + ": " + pref.getBoolean(packageName, true));
						send = pref.getBoolean(packageName, true);
					}
					else {
						Log.d("Notify", "Preference for " + packageName + " not found");
						prefEdit.putBoolean(packageName, true);
						prefEdit.apply();
					}
				}
				catch (Exception e) { e.printStackTrace(); }
				
				if(send) {
					// Create socket
					new Notify().execute(new String[] {title, text, packageName.toString()});
				}
				else {
					Log.d("Notify", "Send aborted by preference: " + packageName);
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
				StringBuilder out = new StringBuilder();
				out.append(res.getString(R.string.header_tag) + "|");
				for(String p : params) {
					out.append(p + "|");
				}
				
				// TODO: XOR output string

				DatagramPacket outpack = new DatagramPacket(
                        out.toString().getBytes("UTF-8"), 
                        out.toString().getBytes("UTF-8").length, 
                        cAddr, highPort);
				
				
				Log.d("Notify", "Preparing to send");
				Log.d("Notify", "highPort: " + highPort);

				if(highPort > 0 && cAddr != null) {
					// Looks like we have what we need, let's send
					dsock.send(outpack);
				}
				else { // No high port, let's ask for one
				
					StringBuilder b = new StringBuilder();
					b.append(res.getString(R.string.header_tag) + "|");
					b.append(res.getString(R.string.handshake_tag));
				
					// TODO: XOR output string
				
					Log.d("Notify", "Sending handshake");
					Log.d("Notify", "Handshake: " + b.toString());
				
					DatagramPacket dpack = new DatagramPacket(
					                               b.toString().getBytes("UTF-8"),
					                               b.toString().getBytes("UTF-8").length,
					                               getBroadcastAddress(), defaultPort);
					
					// Set the timeout
					dsock.setSoTimeout(5000);
									
					// Send the handshake
					dsock.send(dpack);

					
					// Craft an empty DatagramPacket and wait for a reply
					dsock.setReceiveBufferSize(400);
					byte[] data = new byte[400];
					DatagramPacket rcvpack = new DatagramPacket(data, data.length);
					dsock.receive(rcvpack);
					
					// Process the output - export a port number
					String response = (new String(rcvpack.getData(), "UTF-8")).trim(); 
					
					
					// TODO: XOR incoming string
					String[] items = response.split("\\|"); 
					if(items[0].equals(res.getString(R.string.header_tag))) {
						// We've got a header tag, so parse the highPort
						highPort = Integer.parseInt(items[1]);
					
						// Set the client address
						cAddr = rcvpack.getAddress();
					
						// Now that we have the highPort, we can send the notification
						Log.d("Notify", "Sending for real");
						outpack.setAddress(cAddr);
						outpack.setPort(highPort);
						dsock.send(outpack);
					}
				
				}
				
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

	@Override
	public void onInterrupt() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onServiceConnected() {
		AccessibilityServiceInfo info = new AccessibilityServiceInfo();
		info.feedbackType = 1;
		info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
		info.notificationTimeout = 5; 
		setServiceInfo(info);
	}
}

