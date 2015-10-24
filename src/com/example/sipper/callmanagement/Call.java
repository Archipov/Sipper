package com.example.sipper.callmanagement;

import java.util.concurrent.TimeUnit;

import org.doubango.ngn.sip.NgnAVSession;

import android.util.Log;

import com.example.sipper.CallStateReceiver;
import com.example.sipper.Logger;
import com.example.sipper.Utilities;

public class Call {

	private long id = 0;
	private boolean active;
	private String callingPartyUri;
	private String calledPartyUri;
	private long startTimeMillis = 0;
	
	private long endTimeMillis = 0;
	private NgnAVSession avSession; //audio/video call session.
	private static final String CLASS_NAME = Call.class.getCanonicalName();
	private final Object callFunctionalityLock = new Object();

	public static Call newInstance(String callingPartyUri, String calledPartyUri,
			NgnAVSession avSession)
	{
		Logger.i(CLASS_NAME, "newInstance() entered");
		
		if(!Utilities.isStringNullOrEmpty(callingPartyUri) || !Utilities.isStringNullOrEmpty(calledPartyUri))
			throw new IllegalArgumentException("Calling or called party is null or empty");
		
		if(avSession == null)
			throw new IllegalArgumentException("avSession is null");
		
		Call call = new Call(callingPartyUri, calledPartyUri, avSession);
		
		return call;
	}
	
	private Call(String callingPartyUri, String calledPartyUri,
			NgnAVSession avSession)
	{
		Logger.i(CLASS_NAME, "Call(" + callingPartyUri + "," + calledPartyUri 
				+ "," + avSession + ") entered, call ID = " + avSession.getId());
		
		this.callingPartyUri = callingPartyUri;
		this.calledPartyUri = calledPartyUri;
		this.avSession = avSession;
		this.id = avSession.getId();
	}
	
	public boolean isActive() 
	{
		return this.active;
	}

	public long getId() 
	{
		return this.id;
	}

	// package-private as it only needs to be called by CallManager.
	void setActive(boolean enabled) 
	{
		this.active = enabled;
	}

	// package-private as it only needs to be called by CallManager.
	boolean endCall() 
	{
		Logger.i(CLASS_NAME, "endCall() entered.");
		Logger.i(CLASS_NAME, "endCall() call id = " + id);
		
		synchronized(callFunctionalityLock)
		{
			if(avSession != null)
			{
				if(!avSession.isActive())
				{
					Logger.e(CLASS_NAME, "endCall() - failed, avSession.isActive() returned false. Call state = " + ( (avSession.getState() != null) ? avSession.getState().name() : "null"));
					return false;
				}
				
				if(avSession.hangUpCall())
				{
					Logger.i(CLASS_NAME, "endCall() - succeeded, avSession.hangUpCall() returned true.");
					return true;
				}
				else
				{
					Logger.e(CLASS_NAME, "endCall() - failed, avSession.hangUpCall() returned false.");
					return false;
				}
			}
			else
			{
				Logger.e(CLASS_NAME, "endCall() - failed, avSession is null.");
				return false;
			}
		}
	}
	
	// package-private as it only needs to be called by CallManager.
	boolean holdCall()
	{
		Logger.i(CLASS_NAME, "holdCall() entered.");
		Logger.i(CLASS_NAME, "holdCall() call id = " + id);
		synchronized(callFunctionalityLock)
		{
			if(avSession != null)
			{
				if(!avSession.isActive())
				{
					Logger.e(CLASS_NAME, "holdCall() - failed, avSession.isActive() returned false. Call state = " + ( (avSession.getState() != null) ? avSession.getState().name() : "null"));
					return false;
				}
				
				if(avSession.isLocalHeld())
				{
					Logger.e(CLASS_NAME, "holdCall() - call already on hold locally, avSession.isLocalHeld() returned true.");
					return true;
				}
				
				if(avSession.holdCall())
				{
					Logger.i(CLASS_NAME, "holdCall() - succeeded, avSession.holdCall() returned true.");
					return true;
				}
				else
				{
					Logger.e(CLASS_NAME, "holdCall() - failed, avSession.holdCall() returned false.");
					return false;
				}
			}
			else
			{
				Logger.e(CLASS_NAME, "holdCall() - failed, avSession is null.");
				return false;
			}
		}
	}
	
	// package-private as it only needs to be called by CallManager.
	boolean resumeCall()
	{
		Logger.i(CLASS_NAME, "resumeCall() entered.");
		Logger.i(CLASS_NAME, "resumeCall() call id = " + id);
		
		synchronized(callFunctionalityLock)
		{
			if(avSession != null)
			{
				if(!avSession.isActive())
				{
					Logger.e(CLASS_NAME, "resumeCall() - failed, avSession.isActive() returned false. Call state = " + ( (avSession.getState() != null) ? avSession.getState().name() : "null"));
					return false;
				}
				
				if(!avSession.isLocalHeld()) 
				{
					Logger.e(CLASS_NAME, "resumeCall() - cannot resume call as it is not on hold, avSession.isLocalHeld() returned false.");
					return false;
				}
				
				if(avSession.resumeCall())
				{
					Logger.i(CLASS_NAME, "resumeCall() - succeeded, avSession.resumeCall() returned true.");
					return true;
				}
				else
				{
					Logger.e(CLASS_NAME, "resumeCall() - failed, avSession.resumeCall() returned false.");
					return false;
				}
			}
			else
			{
				Logger.e(CLASS_NAME, "resumeCall() - failed, avSession is null.");
				return false;
			}
		}
	}
	
	public long getStartTimeMillis() 
	{
		return startTimeMillis;
	}

	public void setStartTimeMillis(long startTimeMillis) 
	{
		Logger.d(CLASS_NAME, "setStartTimeMillis(" + startTimeMillis + ")");
		this.startTimeMillis = startTimeMillis;
	}

	public long getEndTimeMillis() 
	{
		return endTimeMillis;
	}

	public void setEndTimeMillis(long endTimeMillis) 
	{
		Logger.d(CLASS_NAME, "setEndTimeMillis(" + endTimeMillis + ")");
		this.endTimeMillis = endTimeMillis;
	}

	public NgnAVSession getAvSession() 
	{
		return avSession;
	}
}