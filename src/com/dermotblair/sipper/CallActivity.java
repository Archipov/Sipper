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

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;

import com.dermotblair.sipper.R;
import com.dermotblair.sipper.callmanagement.Call;
import com.dermotblair.sipper.callmanagement.CallManager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CallActivity extends ActionBarActivity{
	
	private NgnEngine mEngine;
	private INgnSipService mSipService;
	private String callStatus;
	
	private TextView callStatusTextView;
	private Button endCallButton;
	private Button holdCallButton;
	private Button speakerToggleButton;
	
	private boolean callIsHeldLocally; // If true, user has put the active call on hold locally.
	private boolean speakerPhoneEnabled;
	
	private static final String CLASS_NAME = CallActivity.class.getCanonicalName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call);
		
		init();
	}
	
	private void init()
	{
		Logger.d(CLASS_NAME, "init() entered.");
		
		Intent intent = getIntent();
	    if(intent != null && intent.hasExtra(CallStateReceiver.CALL_STATUS_KEY))
	    {
	         callStatus = intent.getStringExtra(CallStateReceiver.CALL_STATUS_KEY);  
	         Logger.d(CLASS_NAME, "init() - received intent with extra: CallStateReceiver.CALL_STATUS_KEY, call status = " + callStatus);
	    }
	    else
	    	Logger.d(CLASS_NAME, "init() - intent null or intent does not have extra: CallStateReceiver.CALL_STATUS_KEY");

	    callStatusTextView = (TextView)findViewById(R.id.callStatusTextView);
	    
	    setCallStatus(callStatus);
	    
	    endCallButton = (Button)findViewById(R.id.endCallButton);
	    holdCallButton = (Button)findViewById(R.id.holdCallButton);
	    speakerToggleButton = (Button)findViewById(R.id.speakerToggleButton);
	    
		// Get engines
		mEngine = NgnEngine.getInstance();
		mSipService = mEngine.getSipService();
	}
	
	public void onClick(View view)
	{
		switch(view.getId()) 
		{
	    	case R.id.endCallButton:
	    		doEndCall();
	    	break;
	    	case R.id.holdCallButton:
	    		if(callIsHeldLocally)
	    			doResumeCall();
	    		else
	    			doHoldCall();
	    	break;
	    	case R.id.speakerToggleButton:
	    		if(speakerPhoneEnabled)
	    			setSpeakerPhoneEnabled(false);
	    		else
	    			setSpeakerPhoneEnabled(true);
	    	break;
	    	
		}
	}

	private void doEndCall()
	{
		Logger.i(CLASS_NAME, "doEndCall() entered.");
		
		if(endCallButton != null)
			endCallButton.setEnabled(false);
		if(holdCallButton != null)
			holdCallButton.setEnabled(false);
		
		Call activeCall = CallManager.getInstance().getActiveCall();
		if(activeCall != null)
		{
			if(CallManager.getInstance().endCall(activeCall))
			{
				Logger.i(CLASS_NAME, "doEndCall() - Call " + activeCall.getId() + " ended successfully.");
			}
			else
			{
				Logger.e(CLASS_NAME, "doEndCall() - Ending call " + activeCall.getId() + " failed.");
			}
		}
		else
		{
			Logger.e(CLASS_NAME, "doEndCall() - Ending call " + activeCall.getId() + " failed, no active call returned by CallManager.");
		}
	}
	
	private void doHoldCall()
	{
		Logger.i(CLASS_NAME, "doHoldCall() entered.");
		
		Call activeCall = CallManager.getInstance().getActiveCall();
		if(activeCall != null)
		{
			if(CallManager.getInstance().holdCall(activeCall))
			{
				callIsHeldLocally = true;
				if(holdCallButton != null)
					holdCallButton.setText("Resume");
				Logger.i(CLASS_NAME, "doHoldCall() - Call " + activeCall.getId() + " put on hold successfully.");
			}
			else
			{
				Logger.e(CLASS_NAME, "doHoldCall() - Putting call " + activeCall.getId() + " on hold failed.");
			}
		}
		else
		{
			Logger.e(CLASS_NAME, "doHoldCall() - Ending call " + activeCall.getId() + " failed, no active call returned by CallManager.");
		}
	}
	
	private void doResumeCall()
	{
		Logger.i(CLASS_NAME, "doResumeCall() entered.");
		
		Call activeCall = CallManager.getInstance().getActiveCall();
		if(activeCall != null)
		{
			if(CallManager.getInstance().resumeCall(activeCall))
			{
				callIsHeldLocally = false;
				if(holdCallButton != null)
					holdCallButton.setText("Hold");
				Logger.i(CLASS_NAME, "doResumeCall() - Call " + activeCall.getId() + " resumed successfully.");
			}
			else
			{
				Logger.e(CLASS_NAME, "doResumeCall() - Resuming call " + activeCall.getId() + " failed.");
			}
		}
		else
		{
			Logger.e(CLASS_NAME, "doResumeCall() - Ending call " + activeCall.getId() + " failed, no active call returned by CallManager.");
		}
	}
	
	private void setCallStatus(String callStatus)
	{
		Logger.i(CLASS_NAME, "setCallStatus(" + callStatus + ")");
		
		if(callStatusTextView != null && callStatus != null)
	    	callStatusTextView.setText(callStatus);
	}
	
	private void setSpeakerPhoneEnabled(boolean enabled) 
	{
		Logger.i(CLASS_NAME, "setSpeakerPhoneEnabled(" + enabled + ") entered.");
		
		Call activeCall = CallManager.getInstance().getActiveCall();
		if(activeCall == null)
		{
			Logger.e(CLASS_NAME, "setSpeakerPhoneEnabled() - failed, no active call returned by CallManager.");
			return;
		}
		
		NgnAVSession avSession = activeCall.getAvSession();
		if(avSession == null)
		{
			Logger.e(CLASS_NAME, "setSpeakerPhoneEnabled() - failed, avSession is null.");
			return;
		}
		if(!avSession.isActive())
		{
			Logger.e(CLASS_NAME, "setSpeakerPhoneEnabled() - failed, avSession.isActive() returned false. Call state = " + ( (avSession.getState() != null) ? avSession.getState().name() : "null"));
			return;
		}
		
		if(enabled && avSession.isSpeakerOn())
		{
			Logger.e(CLASS_NAME, "setSpeakerPhoneEnabled() - turning speaker phone on failed as speaker phone is already turned on.");
			return;
		}
		else if(!enabled && !avSession.isSpeakerOn())
		{
			Logger.e(CLASS_NAME, "setSpeakerPhoneEnabled() - turning speaker phone off failed as speaker phone is already turned off.");
			return;
		}
		
		avSession.setSpeakerphoneOn(enabled);
		if(enabled)
		{
			Logger.d(CLASS_NAME, "setSpeakerPhoneEnabled() - turned speaker phone on successfully.");
			if(speakerToggleButton != null)
				speakerToggleButton.setText("Speaker Off");
			speakerPhoneEnabled = true;
		}
		else
		{
			Logger.d(CLASS_NAME, "setSpeakerPhoneEnabled() - turned speaker phone off successfully.");
			if(speakerToggleButton != null)
				speakerToggleButton.setText("Speaker");
			speakerPhoneEnabled = false;
		}	
	}
}
