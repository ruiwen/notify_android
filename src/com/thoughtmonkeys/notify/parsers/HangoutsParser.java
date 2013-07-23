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
	
		Log.d(this.context.getString(R.string.log_tag), "[debug HangoutsParser:parse()]");
		
		super.parse();
		
		try {

			// Multiple on-going chats
			// eg. "2 new messages: Chat 1, Chat 2
			if(this.results.get("title").startsWith("new messages", 2)) {
				Log.d(this.context.getString(R.string.log_tag), "[debug HangoutsParser:parse()] multiple chats");
				Log.d(this.context.getString(R.string.log_tag), this.results.get("title"));
				String[] parts = this.results.get("title").split(": ");
				this.results.put("title", parts[0]);				
				this.results.put("text", parts[1]);
			}
					
			// Single chat
			// eg. Ruiwen Chua: hello world
			// eg. "Ruiwen Chua: 2 new messages"
			else {
				String title = ((TextView) this.localViews.findViewById(TITLE_ID)).getText().toString();
				this.results.put("title", title);
			
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
