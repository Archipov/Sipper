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

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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