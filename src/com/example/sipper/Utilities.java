package com.example.sipper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/*
 * Commonly used functions unrelated to the SIP library.
 */
public class Utilities {
	
	private static Toast toast;

	public static void toast(Context context, String message, boolean longDuration)
	{
		if (toast != null)
			toast.cancel();

		toast = Toast.makeText(context, message, (longDuration ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT ));
		toast.show();
	}
	
	public static boolean isNetworkAvailable(Context context) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public static boolean isStringNullOrEmpty(String str)
	{
		if(str != null && !"".equals(str.trim()))
			return true;
		return false;
	}
}
