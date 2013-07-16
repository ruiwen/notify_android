package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import com.thoughtmonkeys.notify.R;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

public class HangoutsParser extends BaseParser {

	public HangoutsParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {
		super.parse();
		super.componentise();
		
		try {
			String notification_title = ((TextView) this.localViews.findViewById(TITLE_ID)).getText().toString();
			
			if(this.results.get("text").contains(", ")) {
				return this.results;
			}
			// Multiple on-going chats
			// INBOX0_ID should be populated
			else if(notification_title.contains("new messages")) {
				String txt = ((TextView) this.localViews.findViewById(INBOX0_ID)).getText().toString();	
				this.results.put("text", txt);
			}
		
			// Try for BIGTEXT_ID
			else {
				Log.d(this.context.getString(R.string.log_tag), "BIGTEXT");
				String txt = ((TextView) this.localViews.findViewById(BIGTEXT_ID)).getText().toString();	
				String[] parts = txt.split("[\\r\\n]+");
				this.results.put("text", parts[parts.length-1]);
			}
		
		}
		catch(NullPointerException e) {
			Log.d(this.context.getString(R.string.log_tag), e.toString());
			Log.d(this.context.getString(R.string.log_tag), "" + e.getMessage());
		}
		
		return this.results;
	}
}
