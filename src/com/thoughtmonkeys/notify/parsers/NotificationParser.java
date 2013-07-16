package com.thoughtmonkeys.notify.parsers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.thoughtmonkeys.notify.donate.R;
import com.thoughtmonkeys.notify.parsers.*;

public class NotificationParser {

	public static HashMap<String, String> parse(Context context, AccessibilityEvent event) {
		
		BaseParser parser = new BaseParser(context, event);
		
		try {
			int classResource = context.getResources().getIdentifier(event.getPackageName().toString(), "string", context.getPackageName());
			
			Class<BaseParser> parserClass = (Class<BaseParser>) Class.forName(NotificationParser.class.getPackage().getName() +  "." + context.getResources().getString(classResource));
			Class[] types = {android.content.Context.class, event.getClass()};
			Constructor<BaseParser> constructor = parserClass.getConstructor(types);
			
			parser = (BaseParser) constructor.newInstance(context, event);			

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFoundException e) {
			Log.d(context.getString(R.string.log_tag), "No parser found for " + event.getPackageName().toString());
		}
	
		return parser.parse();
	}

}
