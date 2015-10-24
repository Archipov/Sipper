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

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import com.dermotblair.sipper.callmanagement.Call;
import com.dermotblair.sipper.callmanagement.CallManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CallStateReceiver extends BroadcastReceiver {
	
	private static final String CLASS_NAME = CallStateReceiver.class.getCanonicalName();
	public final static String CALL_STATUS_KEY = "ACTIVITY_STATUS_KEY";
	
	  @Override
	  public void onReceive(Context context, Intent intent) {

	    final String action = intent.getAction();
	    
	    if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action))
	    {
	      NgnInviteEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
	      if(args == null)
	      {
	        Logger.e(CLASS_NAME, "onReceive() - Invalid event args");
	        return;
	      }

	      NgnAVSession avSession = NgnAVSession.getSession(args.getSessionId());
	      if (avSession == null) 
	      {
	        return;
	      }

	      final InviteState callState = avSession.getState();
	      NgnEngine mEngine = NgnEngine.getInstance();
	      
	      if(callState == null)
	    	  return;
	      
	      Logger.i(CLASS_NAME, "onReceive() - callState = " + callState.name());
	      
	      switch(callState)
	      {
	        case NONE:
	        default:
	        break;
	        case INCOMING:
	          // Ring
	          mEngine.getSoundService().startRingTone();
	          break;
	        
	        case INCALL:
	          mEngine.getSoundService().stopRingTone();
	          mEngine.getSoundService().stopRingBackTone();
	          
	          Call activeCallInCall = CallManager.getInstance().getActiveCall();
	          if(activeCallInCall != null)
	        	  activeCallInCall.setStartTimeMillis(System.currentTimeMillis());
	          else
	        	  Logger.e(CLASS_NAME, "onReceive() - no active call available to set start time.");
	          
	          //TODO: check if activity is already open before re-opening it.
	          
	          Intent intentCallConnected = new Intent(context.getApplicationContext(), CallActivity.class);
	          intentCallConnected.putExtra(CALL_STATUS_KEY, callState.name());
	          context.startActivity(intentCallConnected);
	    	  
	          break;
	        case TERMINATED:
	          mEngine.getSoundService().stopRingTone();
	          mEngine.getSoundService().stopRingBackTone();
	          
	          // TODO: delete call from call manager.
	          // TODO: write call to call logs.
	          
	          Call activeCallTerminated = CallManager.getInstance().getActiveCall();
	          if(activeCallTerminated != null)
	        	  activeCallTerminated.setEndTimeMillis(System.currentTimeMillis());
	          else
	        	  Logger.e(CLASS_NAME, "onReceive() - no active call available to set end time.");
	          break;
	          

	        case INPROGRESS:
	        	mEngine.getSoundService().startRingBackTone();
	        	
	        	Intent intentCallInProgress = new Intent(context.getApplicationContext(), CallActivity.class);
	        	intentCallInProgress.putExtra(CALL_STATUS_KEY, callState.name());
	        	context.startActivity(intentCallInProgress);
	        	break;
	        	 	
	        case REMOTE_RINGING:
	        	// Does not seem to ever get this state in an outgoing call.
	        	// It goes straight from INPROGRESS TO INCALL.
	        	break;
	        	
	        case EARLY_MEDIA:
	        	break;
	        	
	        case TERMINATING:
	        	break;
	      }
	      
	      //broadcastCallStateEvent(NgnInviteEventArgs args);
	    }
	  }
	  
	  /*private void broadcastCallStateEvent(NgnInviteEventArgs args)
	  {
		  final Intent intent = new Intent(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		  intent.putExtra(NgnInviteEventArgs.EXTRA_EMBEDDED, args);
		  //intent.putExtra(NgnInviteEventArgs.EXTRA_SIPCODE, 0);
		  NgnApplication.getContext().sendBroadcast(intent);
	  }*/
	}