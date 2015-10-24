package com.example.sipper;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

	 private static final String CLASS_NAME = RegistrationBroadcastReceiver.class.getCanonicalName();
	
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    final String action = intent.getAction();
	    // Registration Event
	    if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
	      NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
	      if(args == null){
	        Logger.d("DEBUG", "Invalid event args");
	        return;
	      }
	      switch(args.getEventType()){
	        case REGISTRATION_NOK:
	          Logger.d(CLASS_NAME, "Failed to register.");
	          break;
	        case UNREGISTRATION_OK:
	          Logger.d(CLASS_NAME, "You are now unregistered.");
	          break;
	        case REGISTRATION_OK:
	          Logger.d(CLASS_NAME, "You are now registered.");
	          break;
	        case REGISTRATION_INPROGRESS:
	          Logger.d(CLASS_NAME, "Trying to register.");
	          break;
	        case UNREGISTRATION_INPROGRESS:
	          Logger.d(CLASS_NAME, "Trying to unregister.");
	          break;
	        case UNREGISTRATION_NOK:
	          Logger.d(CLASS_NAME, "Failed to unregister.");
	          break;
	      }

	    }
	  }
	}