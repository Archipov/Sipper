/*  
* Sipper
* - A SIP Client for the Android O/S
*
* Copyright (C) 2015, Dermot Blair.
* 
* Contact: Dermot Blair <webvulscan(at)gmail(dot)com>
*
* This file is part of the Sipper Project (https://github.com/dermotblair/Sipper)
*
* Sipper is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Sipper is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Sipper.  If not, see <http://www.gnu.org/licenses/>.
*
* This project consumes, and includes, the imsdroid project which
* is also licensed under the GNU GPL v3 license.
*
* The following section is the copyright notice of the imsdroid project:
*
* Copyright (C) 2010-2011, Mamadou Diop.
* Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
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
