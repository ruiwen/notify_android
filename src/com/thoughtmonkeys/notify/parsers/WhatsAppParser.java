package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import com.thoughtmonkeys.notify.donate.R;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

public class WhatsAppParser extends BaseParser {

	public WhatsAppParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {
	
		super.parse();
		super.componentise();

		try {

			String notification_title = ((TextView) this.localViews.findViewById(TITLE_ID)).getText().toString();
			// txt is expected to be of the format:
			// <contact name> @ <chat group name>: <message>
			String txt = ((TextView) this.localViews.findViewById(INBOX0_ID)).getText().toString();
			
			// Multiple on-going chats
			//if(notification_title.equals("WhatsApp")) {
			if(txt.contains("@")) {
				
				String[] parts = txt.split(": ");
				String msg = parts[1];
				parts = parts[0].split(" @ ");
				msg = parts[0] + ": " + msg;
				String title = parts[1];
				
				this.results.put("title", title);
				this.results.put("text", msg);
				
			}

			// only one on-going chat group
			else {
				String[] parts = txt.split(": ");
				this.results.put("title", parts[0]);
				this.results.put("text", parts[1]);
				
			}
			
		}
		catch(NullPointerException e) {
			Log.d(this.context.getString(R.string.log_tag), e.toString());
		}

		return this.results;
	}

}
