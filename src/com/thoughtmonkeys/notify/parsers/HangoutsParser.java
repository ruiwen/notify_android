package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

public class HangoutsParser extends BaseParser {

	public HangoutsParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {
		this.results = super.parse();
		this.componentise();
		
		try {
			
			// Multiple on-going chats
			if(this.notification.tickerText.toString().matches("new messages")) {
				
			}
		
		}
		catch(NullPointerException e) {
		}
		
		return this.results;
	}
}
