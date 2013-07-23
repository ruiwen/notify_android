package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import com.thoughtmonkeys.notify.dev.R;

import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class SMSParser extends BaseParser {

	public SMSParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {
	
		Log.d(this.context.getString(R.string.log_tag), "[debug SMSParser:parse()]");
		
		super.parse();
		
		try {
			if(this.results.get("title").contains(": ")) {
				
				String[] parts = this.results.get("title").split(": ", 2);
				this.results.put("title", parts[0]);
				this.results.put("text", parts[1]);
			}
		}
		catch(IndexOutOfBoundsException e) {
		}
	
		return this.results;
	}
}
