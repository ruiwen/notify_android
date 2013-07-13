package com.thoughtmonkeys.notify.parsers;

import java.util.HashMap;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

public class NotificationParser extends BaseParser {

	public NotificationParser(Context context, AccessibilityEvent event) {
		super(context, event);
		// TODO Auto-generated constructor stub
	}

	@Override
	public HashMap<String, String> parse() {

		return results;	
	}

}
