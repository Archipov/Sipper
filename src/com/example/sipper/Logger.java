package com.example.sipper;

import android.util.Log;

public class Logger 
{
    private static final boolean DEBUG = true; // TODO: if releasing app, set this to false.
     
    public static void d(String tag, String msg) 
    {
        /*if (Log.isLoggable(TAG, Log.DEBUG)) 
        {
            Log.d(TAG, className + "." + msg);
        }*/
    	
    	if(DEBUG)
    		Log.d(tag, msg);
    }
    
    public static void i(String tag, String msg) 
    {
    	if(DEBUG)
    		Log.i(tag, msg);
    }
    
    public static void w(String tag, String msg) 
    {
    	if(DEBUG)
    		Log.w(tag, msg);
    }
    
    public static void w(String tag, String msg, Throwable exception) 
    {
    	if(DEBUG)
    		Log.w(tag, msg, exception);
    }
    
    public static void e(String tag, String msg) 
    {
    	if(DEBUG)
    		Log.e(tag, msg);
    }  
    
    public static void e(String tag, String msg, Throwable exception) 
    {
    	if(DEBUG)
    		Log.e(tag, msg, exception);
    }  
}
