package com.arretadogames.pilot.android;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class KeyboardManager {
	
	public interface InputFinishListener {
		public void onInputFinish(String typedString);
	}
	
	private static String AVAILABLE_CHARACTERS = "abcdefghijlmnkopqrstuvxyz@._-";
	
	private static boolean isShowing = false;
	private static boolean isConfigured = false;
	private static Activity activity;
	
	private static String currentString = "";

	private static InputFinishListener listener;
	
	public static boolean isShowing() {
		return isShowing;
	}
	
	public static void show() {
		validateConfiguration();
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        currentString = "";
        isShowing = true;
	}
	
	public static void hide() {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY,0);
		isShowing = false;
		listener.onInputFinish(currentString);
	}
	
	private static void validateConfiguration() {
		if (!isConfigured)
			throw new IllegalStateException("Manager no configured");
	}
	
	public static void setOnInputFinishListener(InputFinishListener listener) {
		KeyboardManager.listener = listener;
	}

	public static void setup(Activity mainActivity) {
		KeyboardManager.activity = mainActivity;
		if (activity != null)
			isConfigured = true;
	}

	public static boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			System.out.println(event.getKeyCode());
			if (event.getKeyCode() == KeyEvent.KEYCODE_DEL && currentString.length() > 0) {
				currentString = currentString.substring(0, currentString.length() - 1);
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
				hide();
			} else {
				String s = Character.toString((char)event.getUnicodeChar());
				if (AVAILABLE_CHARACTERS.contains(s.toLowerCase()))
					currentString += s;
				System.out.println("DispatchKeyEvent " + currentString);
			}
		}
		
		return true;
	}

	public static String getText() {
		return currentString;
	}
	

}
