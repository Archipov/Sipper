package com.example.sipper;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

import com.example.sipper.callmanagement.Call;
import com.example.sipper.callmanagement.CallManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class DialerActivity extends ActionBarActivity{

	//private NgnEngine mEngine = null;
	//private INgnSipService mSipService = null;
	
	private EditText numbertoCallEditText;
	
	private static final String CLASS_NAME = DialerActivity.class.getCanonicalName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialer);
	
		init();
	}
	
	private void init()
	{
		// Get engines
		//mEngine = NgnEngine.getInstance();
		//mSipService = mEngine.getSipService();
		
		numbertoCallEditText = (EditText)findViewById(R.id.numbertoCallEditText);
	}
	
	
	public void onClick(View view)
	{
		switch(view.getId()) 
		{
	    	case R.id.callButton:
	    		if(numbertoCallEditText != null)
	    		{
	    			/*int numberToCall = 0;
	    			try
	    			{
	    				numberToCall = Integer.parseInt(numbertoCallEditText.getText().toString().trim());
	    			}
	    			catch(NumberFormatException ex)
	    			{
	    				Utilities.toast(this, "The number entered is not valid. Only digits are accepted.", true);
	    			}*/
	    			
	    			String remotePartyToCall = numbertoCallEditText.getText().toString().trim();
	    				
	    			if(makeOutgoingCall(remotePartyToCall, NgnMediaType.Audio))
	    			{
	    				Intent intentCallConnected = new Intent(DialerActivity.this, CallActivity.class);
	    		        //intentCallConnected.putExtra(CALL_STATUS_KEY, callState.name());
	    		        startActivity(intentCallConnected);
	    			}
	    			else
	    				Utilities.toast(this, "Cannot call: " + remotePartyToCall, true);
	    		}
	    	break;
	    	
		}
	}
	
	private boolean makeOutgoingCall(String remotePartyToCall, NgnMediaType callType)
	{
		Logger.i(CLASS_NAME, "makeOutgoingCall() entered.");
		
		if(!Common.isRegistered())
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, user is not registered.");
			return false;
		}
		
		/*if(!isNumberRemotePartyToCall(remotePartyToCall))
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, isNumberValid() returned false.");
			return false;
		}*/
		
		/*final String username = Config.get(Config.USERNAME_KEY);
		if(!Utilities.isStringNullOrEmpty(username))
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, cannot find calling party username.");
			return false;
		}*/
		
		final String domain = Config.get(Config.DOMAIN_KEY);
		if(!Utilities.isStringNullOrEmpty(domain))
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, cannot find a domain.");
			return false;
		}
		
		final String callingPartyImpu = Config.get(Config.IMPU_KEY);
		if(!Utilities.isStringNullOrEmpty(callingPartyImpu))
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, cannot find an impu.");
			return false;
		}
		
		/*final String impuToCall = String.format("sip:%s@%s", Integer.toString(numberToCall), domain);
		if(!Utilities.isStringNullOrEmpty(impuToCall))
		{
			Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, impu to call is null or empty.");
			return false;
		}*/
		
		// makeValidSipUri() accepts a number, username or a uri.
		final String remotePartyValidUri = NgnUriUtils.makeValidSipUri(remotePartyToCall);
	    
		if(!Utilities.isStringNullOrEmpty(remotePartyValidUri) || remotePartyValidUri.contains("invalid")) //makeValidSipUri can return "sip:invalid@open-ims.test"
		{
	      Logger.e(CLASS_NAME, "makeOutgoingCall() - cannot make call, makeValidSipUri() returned null, empty or invalid uri.");
	      return false;
	    }
	    
	    NgnAVSession avSession = NgnAVSession.createOutgoingSession(NgnEngine.getInstance().getSipService().getSipStack(), callType);
	    
	    Logger.i(CLASS_NAME, "makeOutgoingCall() - Attempting to call " + remotePartyValidUri);
	    
	    if(avSession.makeCall(remotePartyValidUri))
	    {
	    	Logger.d(CLASS_NAME, "makeOutgoingCall() - NgnAVSession.makeCall() returned true.");
	
	    	Call call = Call.newInstance(callingPartyImpu, remotePartyValidUri, avSession);
	    	CallManager callManager = CallManager.getInstance();
	    	callManager.addActiveCall(avSession.getId(), call);
	    	 	
	    	return true;
	    }
	    else
	    {
	    	Logger.e(CLASS_NAME, "makeOutgoingCall() - NgnAVSession.makeCall() returned false.");
	    	return false;
	    }
	}
	
	/*
	 *  Checks if a number which will be used to make an outgoing call to, is valid.
	 */
	/*private static boolean isNumberValid(int numberToCall)
	{
		// TODO: add more validation checks
		if(numberToCall > -1)
			return true;
		else
			return false;
	}*/
	
	/*@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
    }*/
}
