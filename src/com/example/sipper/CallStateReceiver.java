package com.example.sipper;

import org.doubango.ngn.NgnApplication;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import com.example.sipper.callmanagement.Call;
import com.example.sipper.callmanagement.CallManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	        	// Don't seem to ever get this state in an outgoing call.
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