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
package com.dermotblair.sipper.callmanagement;

import com.dermotblair.sipper.Logger;
import android.support.v4.util.LongSparseArray;

public class CallManager {

	private static LongSparseArray<Call> callList = new LongSparseArray<Call>();
	private static CallManager instance;
	private final static Object lock = new Object();
	private static final String CLASS_NAME = CallManager.class.getCanonicalName();
	
	private CallManager(){}
	
	public static CallManager newInstance()
	{
		synchronized(lock)
		{
			return getInstance();
		}
	}
	
	public static CallManager getInstance()
	{
		synchronized(lock)
		{
			if(instance != null)
				return instance;
			else
			{
				instance = new CallManager();
				return instance;
			}
		}
	}
	
	/*public void addCall(long callId, Call call)
	{
		synchronized(lock)
		{
			addCallToList(callId, call);
		}
	}*/
	
	/*
	 * Adds a call to the list of the calls, sets it to active and sets all other calls
	 * in the call list to inactive.
	 */
	public void addActiveCall(long callId, Call call)
	{
		Logger.i(CLASS_NAME, "addActiveCall() entered, callId = " + callId);
		synchronized(lock)
		{
			addCallToList(callId, call);
			call.setActive(true);
		}
	}
	
	private void addCallToList(long callId, Call call)
	{
		Logger.i(CLASS_NAME, "addCallToList() entered, callId = " + callId);
		synchronized(lock)
		{
			if(call == null)
				throw new IllegalArgumentException("Call is null");
			
			callList.put(callId, call);
		}
	}
	
	public Call getCall(long callId)
	{
		synchronized(lock)
		{
			return callList.get(callId);
		}
	}
	
	/*
	 * Returns the active call. There is only one active call at a time. Returns null
	 * if there is no active call.
	 */
	public Call getActiveCall()
	{
		Logger.d(CLASS_NAME, "getActiveCall() entered.");
		synchronized(lock)
		{
			for(int i=0, size=callList.size(); i<size; i++)
			{
				Call callAtIndex = callList.valueAt(i);
				
				if(callAtIndex != null && callAtIndex.isActive())
				{
					return callAtIndex;
				}
			}
			return null;
		}
	}
	
	/*public boolean endCall()
	{
		synchronized(lock)
		{
			Logger.i(CLASS_NAME, "endCall() entered.");
			
			boolean callEnded = false;
			int callsEndedCount = 0;
			
			// There can only be one active call at the time so break once we have ended 
			// the first active call we find.
			for(int i=0, size=callList.size(); i<size; i++)
			{
				Call callAtIndex = callList.valueAt(i);
				
				if(callAtIndex !=null && callAtIndex.isActive())
				{
					 // If any of the calls were ended we want to return true so using
					 // compound assignment operator.
					 callEnded |= callAtIndex.endCall(); 
					 
					 long id = callAtIndex.getId();
					 if(callEnded)
					 {
						 callList.removeAt(i);
						 
						 Logger.d(CLASS_NAME, "endCall() - call with id " + id + "ended.");
						 callsEndedCount++;
					 }
					 else
					 {
						 Logger.e(CLASS_NAME, "endCall() - could not call with id " + id);
					 }
					 break;
				}
			}
			
			if(callsEndedCount > 1)
			{
				Logger.e(CLASS_NAME, "endCall() - more than one call was ended. Number of calls ended = " + callsEndedCount);
			}
			
			return callEnded;
		}
	}*/
	
	public boolean endCall(Call callToEnd)
	{
		Logger.i(CLASS_NAME, "endCall() entered.");
		synchronized(lock)
		{
			if(callToEnd == null)
			{
				Logger.d(CLASS_NAME, "endCall() - call to end is null.");
				return false;
			}
			
			Logger.d(CLASS_NAME, "endCall() - call ID = " + callToEnd.getId() + ".");
			
			Logger.d(CLASS_NAME, "endCall() - call list size before delete = " + callList.size() + ".");
			callList.delete(callToEnd.getId());
			Logger.d(CLASS_NAME, "endCall() - call list size after delete = " + callList.size() + ".");
			
			return callToEnd.endCall();
		}
	}
	
	public boolean holdCall(Call callToEnd)
	{
		Logger.i(CLASS_NAME, "holdCall() entered.");
		synchronized(lock)
		{
			if(callToEnd == null)
			{
				Logger.d(CLASS_NAME, "holdCall() - call to end is null.");
				return false;
			}
			
			Logger.d(CLASS_NAME, "holdCall() - call ID = " + callToEnd.getId() + ".");

			return callToEnd.holdCall();
		}
	}
	
	public boolean resumeCall(Call callToEnd)
	{
		Logger.i(CLASS_NAME, "resumeCall() entered.");
		synchronized(lock)
		{
			if(callToEnd == null)
			{
				Logger.d(CLASS_NAME, "resumeCall() - call to end is null.");
				return false;
			}
			
			Logger.d(CLASS_NAME, "resumeCall() - call ID = " + callToEnd.getId() + ".");

			return callToEnd.resumeCall();
		}
	}
	
	public void setCallWithIdActive(long callId)
	{
		Logger.i(CLASS_NAME, "setCallWithIdActive() entered, callId = " + callId);
		synchronized(lock)
		{
			for(int i=0, size=callList.size(); i<size; i++)
			{
				Call callAtIndex = callList.valueAt(i);
				
				if(callAtIndex != null)
				{
					if(callAtIndex.getId() == callList.keyAt(i))
					{
						callAtIndex.setActive(true);
					}
					else
						callAtIndex.setActive(false);
				}
			}
		}
	}
	
	/*
	 * Sets all calls to inactive except the call which has an ID equal to callId.
	 * 
	 * FIXME: This seems messy. Call.setActive() calls out to this method which calls
	 * back into setActive. If setActive(true) is ever called in this method, it will
	 * result in an infinite loop.
	 */
	/*public void setAllCallsInactiveExcept(long callId)
	{
		synchronized(lock)
		{
			for(int i=0, size=callList.size(); i<size; i++)
			{
				Call callAtIndex = callList.valueAt(i);
				
				if(callAtIndex != null)
				{
					if(callAtIndex.getId() != callId && callAtIndex.isActive())
					{
						callAtIndex.setActive(false);
					}
				}
			}
		}
	}*/
}
