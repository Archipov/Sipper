package com.example.sipper;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

// Holds the configuration details for the user currently logged in.
public class Config {

	//private static Config configInstance = null;
	private static Map<String, String> accountDetails = new HashMap<String, String>();
	
	// Keys to access values returned in the Map from getAccountDetails()
	public static String USERNAME_KEY = "username";
	public static String IMPU_KEY = "impu";
	public static String PASSWORD_KEY = "password";
	public static String SIP_SERVER_KEY = "sipServer";
	public static String SERVER_PORT_KEY = "serverPort";
	public static String DOMAIN_KEY = "domain";
	private static final Object lock = new Object();
	
	private static final String CLASS_NAME = Config.class.getCanonicalName();
	
	private Config(){}
	
	/*public static Config getInstance()
	{
		if(configInstance != null)
			return configInstance;
		else
			return new Config();
	}*/
	
	public static Map<String, String> getAccountDetails()
	{
		synchronized(lock)
		{
			if(accountDetails.isEmpty())
				return new HashMap<String, String>();
			else // return deep copy, key and value (String's) are immutable so copy constructor will suffice.
				return new HashMap<String, String>(accountDetails);
		}
	}
	
	/*
	 * Sets the account details. Returns true on success or false on failure.
	 */
	public static boolean setAccountDetails(Map<String, String> detailsMap)
	{
		Logger.i(CLASS_NAME, "setAccountDetails() entered.");
		synchronized(lock)
		{
			HashMap<String, String> hashMap = new HashMap<String, String>(detailsMap);
			if(isValid(hashMap))
			{
				accountDetails = hashMap;
				Logger.d(CLASS_NAME, "setAccountDetails() - returning true as the parameter map is valid.");
				return true;
			}
			
			Logger.e(CLASS_NAME, "setAccountDetails() - returning false as the parameter map is not valid.");
			return false;
		}
	}
	
	private static boolean isValid(Map<String, String> detailsMap)
	{
		Logger.d(CLASS_NAME, "isValid() entered.");
		
		if(detailsMap == null || detailsMap.isEmpty())
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the parameter map is null or empty.");
			return false;
		}
		
		if(detailsMap.get(Config.USERNAME_KEY) == null || detailsMap.get(Config.USERNAME_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.USERNAME_KEY is null or empty.");
			return false;
		}
		if(detailsMap.get(Config.IMPU_KEY) == null || detailsMap.get(Config.IMPU_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.IMPU_KEY is null or empty.");
			return false;
		}
		if(detailsMap.get(Config.PASSWORD_KEY) == null || detailsMap.get(Config.PASSWORD_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.PASSWORD_KEY is null or empty.");
			return false;
		}
		if(detailsMap.get(Config.SIP_SERVER_KEY) == null || detailsMap.get(Config.SIP_SERVER_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.SIP_SERVER_KEY is null or empty.");
			return false;
		}
		if(detailsMap.get(Config.SERVER_PORT_KEY) == null || detailsMap.get(Config.SERVER_PORT_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.SERVER_PORT_KEY is null or empty.");
			return false;
		}
		if(detailsMap.get(Config.DOMAIN_KEY) == null || detailsMap.get(Config.DOMAIN_KEY).trim().length() == 0)
		{
			Logger.e(CLASS_NAME, "isValid() - returning false as the value in the map corresponding to Config.DOMAIN_KEY is null or empty.");
			return false;
		}
		
		Logger.d(CLASS_NAME, "isValid() - returning true.");
		return true;
	}
	
	/*
	 * Gets a value from the Config hashmap using a given key.
	 * 
	 * Returns null if the hashmap does not exist or the key does not exist in the hashmap.
	 */
	public static String get(String key)
	{
		Logger.d(CLASS_NAME, "get() entered.");
		Logger.d(CLASS_NAME, "get() - key = " + key);
		
		if(key == null || accountDetails == null)
		{
			Logger.e(CLASS_NAME, "get() - key or account details is null, returning null");
			return null;
		}
		
		String returnValue = accountDetails.get(key);
		Logger.e(CLASS_NAME, "get() - returning " + returnValue);
		return returnValue;
	}
}
