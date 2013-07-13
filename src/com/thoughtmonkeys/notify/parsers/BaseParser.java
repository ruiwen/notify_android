package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import android.app.Notification;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

public class BaseParser {

	protected int TITLE_ID = 16908310;
	protected int TEXT_ID = 16908358;
	protected int INBOX0_ID = 16909101;
	protected int INBOX1_ID = 16909102;
	protected int INBOX2_ID = 16909103;
	protected int INBOX3_ID = 16909104;
	protected int INBOX4_ID = 16909105;
	protected int INBOX5_ID = 16909106;
	protected int INBOX6_ID = 16909107;
	protected int INBOXMORE_ID = 16909108;

	protected RemoteViews remoteViews;
	protected LayoutInflater inflater;
	protected ViewGroup localViews;

	protected Context context;
	protected String packageName;
	protected Notification notification;

	protected HashMap<String, String> results;

	public BaseParser(Context context, AccessibilityEvent event) {
		this.context = context;
		this.packageName = event.getPackageName().toString();
		this.notification = (Notification) event.getParcelableData();
	}

	public HashMap<String, String> parse() {
		
		this.results.put("packageName", this.packageName);
		
		String[] tickerText = this.notification.tickerText.toString().split(": ");
		this.results.put("title", tickerText[0]);
		this.results.put("text", tickerText.length > 1 ? tickerText[1]: "");
		
		
		return results;
	}	

	protected void componentise() {
		try {
			if(android.os.Build.VERSION.SDK_INT < 16) {
				this.remoteViews = this.notification.contentView;
			}
			else if(android.os.Build.VERSION.SDK_INT >= 16) {
				this.remoteViews = this.notification.bigContentView;
			}
		
			this.inflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
	        this.localViews = (ViewGroup) inflater.inflate(remoteViews.getLayoutId(), null);
		}
	    catch(NullPointerException e) {
	    }
	}

}
