package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import com.thoughtmonkeys.notify.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

public class WhatsAppParser extends BaseParser {

	public WhatsAppParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {

		Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()]");
		
		super.parse();
		super.componentise();

		try {

			String notification_title = ((TextView) this.localViews.findViewById(TITLE_ID)).getText().toString();
			// txt is expected to be of the format:
			// <contact name> @ <chat group name>: <message>
			String txt = "";
			try {
				txt = ((TextView) this.localViews.findViewById(BIGTEXT_ID)).getText().toString();
				Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()] Use BIGTEXT");
				
				// This is the first new WhatsApp notification
				this.results.put("title", notification_title);
				this.results.put("text", txt);				
			}
			catch (NullPointerException e) {
				txt = ((TextView) this.localViews.findViewById(INBOX0_ID)).getText().toString();
				Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()] Use INBOX0");

				// Multiple on-going chats
				if(notification_title.contains("WhatsApp")) { 
					if(this.notification.tickerText.toString().contains("@")) {
						Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()] ticker has @");
						
						String[] parts = txt.split(": ");
						String msg = parts[1];
						parts = parts[0].split(" @ ");
						msg = parts[0] + ": " + msg;
						String title = parts[1];
						
						this.results.put("title", title);
						this.results.put("text", msg);					
					}
	
					else if(txt.contains(": ")) {
						String[] parts = txt.split(": ", 2);
						
						if(parts.length == 2) {
							Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()]] 2 parts");
							this.results.put("title", parts[0]);
							this.results.put("text",  parts[1]);
						}
					}
				}
				
				else {
					Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()] fallthrough");
					
					this.results.put("title", notification_title);
					this.results.put("text", txt);
				}
			}

			Log.d(this.context.getString(R.string.log_tag), "title: "+ notification_title);
			Log.d(this.context.getString(R.string.log_tag), "text: "+ txt);
			
		}
		catch(NullPointerException e) {
			Log.d(this.context.getString(R.string.log_tag), "[debug WhatsAppParser:parse()] parse() failed");
			Log.d(this.context.getString(R.string.log_tag), e.toString());
		}

		return this.results;
	}

}
