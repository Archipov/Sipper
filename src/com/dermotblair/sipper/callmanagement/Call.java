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

import org.doubango.ngn.sip.NgnAVSession;
import com.dermotblair.sipper.Logger;
import com.dermotblair.sipper.Utilities;

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