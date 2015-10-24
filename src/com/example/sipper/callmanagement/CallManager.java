package com.example.sipper.callmanagement;

import com.example.sipper.Logger;

import android.support.v4.util.LongSparseArray;
import android.util.Log;

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
