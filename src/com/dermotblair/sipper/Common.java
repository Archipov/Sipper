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
package com.dermotblair.sipper;

import java.util.HashMap;
import java.util.Map;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.app.Activity;
import android.util.Log;

/*
 * Commonly used functions related to the SIP library.
 * TODO: rename this class.
 */
public class Common {
	
	private static final String CLASS_NAME = Common.class.getCanonicalName();
	private static NgnEngine mEngine;
	private static INgnSipService mSipService;
	
	private static void initEngineAndSipService()
	{
		Logger.d(CLASS_NAME, "initEngineAndSipService() entered.");
		// Get engines
		mEngine = NgnEngine.getInstance();
		mSipService = mEngine.getSipService();
	}
	
	/*
	 * Start the NgNEngine.
	 */
	public static  boolean startNgNEngine()
	{
		Logger.d(CLASS_NAME, "startNgNEngine() entered.");
		if(mEngine == null)
		{
			initEngineAndSipService();
		}
			
		// If engine is still null.
		if(mEngine == null)
		{
			Logger.e(CLASS_NAME, "startNgNEngine() - engine is null");
			return false;
		}
		
		if(!mEngine.isStarted())
		{
			if(mEngine.start())
			{
				Logger.d(CLASS_NAME, "startNgNEngine() - engine started successfully.");
				return true;
			}
			else
			{
				Logger.e(CLASS_NAME, "startNgNEngine() - failed to start the engine.");
				return false;
			}
		}
		else
		{
			Logger.d(CLASS_NAME, "startNgNEngine() - engine is already started.");
			return true;
		}  	  
	}
	
	/*
	 * Attempts to register using previously saved SIP account details if
	 * they are available.
	 */
	public static boolean attemptRegisterationOnStartup() 
	{
		Logger.i(CLASS_NAME, "attemptRegisterationOnStartup() entered.");
		if(checkIfSipAccountAvailable())
		{
			Logger.i(CLASS_NAME, "attemptRegisterationOnStartup() - a previously saved sip account is available.");
			if(startNgNEngine())
			{
				if(register())
				{
					Logger.i(CLASS_NAME, "attemptRegisterationOnStartup() - success, Common.register() returned true.");
					return true;
				}
				else
				{
					Logger.e(CLASS_NAME, "attemptRegisterationOnStartup() - failed, Common.register() returned false.");
					return false;
				}
			}
			else
			{
				Logger.e(CLASS_NAME, "attemptRegisterationOnStartup() - failed, startNgNEngine returned false.");
				return false;
			}
			
		}
		else
		{
			Logger.i(CLASS_NAME, "attemptRegisterationOnStartup() - failed, no previously saved sip account available.");
			return false;
		}
		
	}
	
	/*
	 * Checks if a previously configured SIP account is available and sets the details
	 * of this account in the Config class.
	 */
	public static boolean checkIfSipAccountAvailable()
	{
		Logger.i(CLASS_NAME, "checkIfSipAccountAvailable() entered.");
		Map<String, String> accountDetailsMap = new HashMap<String, String>();
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
		if(mConfigurationService == null)
		{
			Logger.e(CLASS_NAME, "checkIfSipAccountAvailable() - mConfigurationService is null");
			return false;
		}
		
		String usernameStr = null;
		String impuStr = null;
		String passwordStr = null;
		String sipServerStr = null;
		String serverPortStr = null;
		String domainStr = null;
		
		usernameStr = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPI, null);
		//if(usernameStr == null || usernameStr.trim().length() == 0)
		//	return false;
		
		impuStr = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_IMPU, null);
		//if(impuStr == null || impuStr.trim().length() == 0)
		// false;
		
		passwordStr = mConfigurationService.getString(NgnConfigurationEntry.IDENTITY_PASSWORD, null);
		//if(passwordStr == null || passwordStr.trim().length() == 0)
		//	return false;
		
		sipServerStr = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, null);
		//if(sipServerStr == null || sipServerStr.trim().length() == 0)
		//	return false;
		
		serverPortStr = Integer.toString(mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 0));
		//if(serverPortStr == null || serverPortStr.trim().length() == 0 || serverPortStr.trim().equals("0"))
		//	return false;
		
		domainStr = mConfigurationService.getString(NgnConfigurationEntry.NETWORK_REALM, null);
		//if(domainStr == null || domainStr.trim().length() == 0)
		//	return false;
		
		// No need to validate the strings as they will be validated in Config.isValid().
		
		accountDetailsMap.put(Config.USERNAME_KEY, usernameStr);
		accountDetailsMap.put(Config.IMPU_KEY, impuStr);
		accountDetailsMap.put(Config.PASSWORD_KEY, passwordStr);
		accountDetailsMap.put(Config.SIP_SERVER_KEY, sipServerStr);
		accountDetailsMap.put(Config.SERVER_PORT_KEY, serverPortStr);
		accountDetailsMap.put(Config.DOMAIN_KEY, domainStr);
		
		Logger.d(CLASS_NAME, "checkIfSipAccountAvailable() - attempting to set account details equal to the following:");
		Logger.d(CLASS_NAME, "checkIfSipAccountAvailable() - " + accountDetailsMap.toString());
		
		if(Config.setAccountDetails(accountDetailsMap))
		{
			Logger.i(CLASS_NAME, "checkIfSipAccountAvailable() - returning true as Config.setAccountDetails() returned true.");
			return true;
		}
		else
		{
			Logger.e(CLASS_NAME, "checkIfSipAccountAvailable() - returning false as Config.setAccountDetails() returned false.");
			return false;
		}

	}
	
	/*
	 * Register the user with the SIP server. This uses the SIP details saved in the
	 * Configuration service. 
	 */
	public static boolean register()
	{
		Logger.i(CLASS_NAME, "register() entered.");
		if(mSipService == null)
		{
			initEngineAndSipService();
		}
		
		// If sip service is still null.
		if(mSipService == null)
		{
			Logger.e(CLASS_NAME, "register() - sip service still null after calling initEngineAndSipService().");
			return false;
		}

		// Register
		if(!mSipService.isRegistered())
		{
			Logger.i(CLASS_NAME, "register() - INgnSipService is not already registered. Attempting to register.");
			if(mSipService.register(null)) // TODO: does a context need to be passed to this? It says it can be null.
			{
				Logger.i(CLASS_NAME, "register() - successfully registered.");
				return true;
			}
			else
			{
				Logger.e(CLASS_NAME, "register() - failed to register.");
				return false;
			}
		}
		else
		{
		  Logger.i(CLASS_NAME, "register() - already registered.");
		  return true;
		}
	}
	
	/*
	 * Checks if the SIP service is currently registered with a SIP server.
	 */
	public static boolean isRegistered()
	{
		Logger.d(CLASS_NAME, "isRegistered() entered.");
		if(mSipService == null)
		{
			initEngineAndSipService();
		}
		
		// If sip service is still null.
		if(mSipService == null)
		{
			Logger.e(CLASS_NAME, "isRegistered() - sip service still null after calling initEngineAndSipService().");
			return false;
		}
		
		if(mSipService.isRegistered())
		{
			Logger.d(CLASS_NAME, "isRegistered() - already registered, returning true as INgnSipService.isRegistered() returned true.");
			return true;
		}
		else
		{
			Logger.e(CLASS_NAME, "isRegistered() - returning false as INgnSipService.isRegistered() returned false.");
			return false;
		}
	}
}
