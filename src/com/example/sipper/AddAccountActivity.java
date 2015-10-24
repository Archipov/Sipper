package com.example.sipper;

import java.util.HashMap;
import java.util.Map;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddAccountActivity extends ActionBarActivity {

	//private NgnEngine mEngine = null;
	//private INgnSipService mSipService = null;
	
	private EditText sipServerEditText;
	private EditText serverPortEditText;
	private EditText domainEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	
	private static final String CLASS_NAME = AddAccountActivity.class.getCanonicalName();
	private String sipServerStr;
	private String serverPortStr;
	private String domainStr;
	private String usernameStr;
	private String passwordStr;
	private String impuStr;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);
		
		init();
		
		if(!Utilities.isNetworkAvailable(this))
		{
			Utilities.toast(this, "No network connection is available. Please enable your Wifi or data network.", true);
		}
		else
		{
			if(Common.isRegistered())
			{
				if(!populateAccountDetailsForm())
					Utilities.toast(this, "An error occurred when filling in the account details form.", true);
			}
		}
		
	}
	
	private void init()
	{
		// Initialise EditTexts.
		sipServerEditText = (EditText)findViewById(R.id.sipServerEditText);
		serverPortEditText = (EditText)findViewById(R.id.serverPortEditText);
		domainEditText = (EditText)findViewById(R.id.domainEditText);
		usernameEditText = (EditText)findViewById(R.id.usernameEditText);
		passwordEditText = (EditText)findViewById(R.id.passwordEditText);
		
		// Get engines
		//mEngine = NgnEngine.getInstance();
		//mSipService = mEngine.getSipService();
	}
	
	/*
	 * Populate the account details form with the previously saved account details.
	 */
	private boolean populateAccountDetailsForm() 
	{
		Map<String, String> accountDetailsMap = Config.getAccountDetails();
		if(accountDetailsMap == null || accountDetailsMap.isEmpty())
			return false;
		
		this.sipServerStr = accountDetailsMap.get(Config.SIP_SERVER_KEY);
		this.serverPortStr = accountDetailsMap.get(Config.SERVER_PORT_KEY);
		this.domainStr = accountDetailsMap.get(Config.DOMAIN_KEY);
		this.usernameStr = accountDetailsMap.get(Config.USERNAME_KEY);
		this.passwordStr = accountDetailsMap.get(Config.PASSWORD_KEY);
		this.impuStr = accountDetailsMap.get(Config.IMPU_KEY);
		
		if(this.sipServerEditText != null && this.sipServerStr != null && this.sipServerStr.trim().length() != 0)
			sipServerEditText.setText(sipServerStr);
		else 
			return false;
		
		if(this.serverPortEditText != null && this.serverPortStr != null && this.serverPortStr.trim().length() != 0)
			serverPortEditText.setText(serverPortStr);
		else 
			return false;
		
		if(this.domainEditText != null && this.domainStr != null && this.domainStr.trim().length() != 0)
			domainEditText.setText(domainStr);
		else 
			return false;
		
		if(this.usernameEditText != null && this.usernameStr != null && this.usernameStr.trim().length() != 0)
			usernameEditText.setText(usernameStr);
		else 
			return false;
		
		if(this.passwordEditText != null && this.passwordStr != null && this.passwordStr.trim().length() != 0)
			passwordEditText.setText(passwordStr);
		else 
			return false;
		
		return true;
	}
	
	
	
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.saveButton:
				if(!Utilities.isNetworkAvailable(this))
				{
					Utilities.toast(this, "No network connection is available. Please enable your Wifi or data network.", true);
				}
				else
				{
					if(saveAccountDetailsFromFrom())
					{
						if(Common.startNgNEngine())
						{
							if(Common.register())
								Utilities.toast(this, "Registered successfully.", true);
							else
								Utilities.toast(this, "Register failed.", true); //TODO: add more info to message.
						}
					}
				}
			break;
			case R.id.dermotTestingButton:
				// TODO: Remove this code. Just using it while developing to populate my account details into the form.
				if(sipServerEditText != null)
					sipServerEditText.setText("freespeech.ie");
				if(serverPortEditText != null)
					serverPortEditText.setText("5060");
				if(domainEditText != null)
					domainEditText.setText("freespeech.ie");
				if(usernameEditText != null)
					usernameEditText.setText("6692531");
				if(passwordEditText != null)
					passwordEditText.setText("4d3afe13");
			break;
			case R.id.cancelButton:
				finish();
			break;
		}
	}
	
	/*
	 * Save the account details that the user entered in the account details form.
	 */
	private boolean saveAccountDetailsFromFrom()
	{
		// Capture account details from form.
		// TODO: validate sip server and domain. e.g. using regex.
		String sipServerStr = sipServerEditText.getText().toString().trim();
		String serverPortStr = serverPortEditText.getText().toString().trim();
		String domainStr = domainEditText.getText().toString().trim();
		String usernameStr = usernameEditText.getText().toString().trim();
		String passwordStr = passwordEditText.getText().toString().trim();
	
		if(!Utilities.isStringNullOrEmpty(sipServerStr))
		{
			Utilities.toast(this, "SIP Server is required.", true);;
			return false;
		}
		if(!Utilities.isStringNullOrEmpty(serverPortStr))
		{
			Utilities.toast(this, "Server Port is required.", true);
			return false;
		}
		int serverPort = 0;
		try
		{
			serverPort = Integer.parseInt(serverPortStr);
		}
		catch(NumberFormatException ex)
		{
			Utilities.toast(this, "Server Port not a valid number.", true);
			return false;
		}
		if(serverPort < 0)
		{
			Utilities.toast(this, "Server Port is not valid as it is less than zero.", true);
			return false;
		}
		if(!Utilities.isStringNullOrEmpty(domainStr))
		{
			Utilities.toast(this, "Domain is required.", true);
			return false;
		}
		if(!Utilities.isStringNullOrEmpty(usernameStr))
		{
			Utilities.toast(this, "Username is required.", true);
			return false;
		}
		if(!Utilities.isStringNullOrEmpty(passwordStr))
		{
			Utilities.toast(this, "Password is required.", true);
			return false;
		}

		String impuStr = String.format("sip:%s@%s", usernameStr, domainStr);
		
		// Set SIP configuration details.
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService = mEngine.getConfigurationService();
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, usernameStr);
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, impuStr);
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, passwordStr);
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, sipServerStr);
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, serverPort);
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, domainStr);
		// By default, using 3G for calls disabled
		mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
		// TODO: You may want to leave the registration timeout to the default 1700 seconds
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT, 3600);
		
		if(mConfigurationService.commit())
		{
			Logger.i(CLASS_NAME, "Successfully saved configuration details to INgnConfigurationService");
			this.sipServerStr =  sipServerStr;
			this.serverPortStr = serverPortStr;
			this.domainStr = domainStr;
			this.usernameStr = usernameStr;
			this.passwordStr = passwordStr;
			this.impuStr = impuStr;
			
			Map<String, String> accountDetailsMap = new HashMap<String, String>();
			accountDetailsMap.put(Config.USERNAME_KEY, usernameStr);
			accountDetailsMap.put(Config.IMPU_KEY, impuStr);
			accountDetailsMap.put(Config.PASSWORD_KEY, passwordStr);
			accountDetailsMap.put(Config.SIP_SERVER_KEY, sipServerStr);
			accountDetailsMap.put(Config.SERVER_PORT_KEY, serverPortStr);
			accountDetailsMap.put(Config.DOMAIN_KEY, domainStr);
			
			if(!Config.setAccountDetails(accountDetailsMap))
				Logger.e(CLASS_NAME, "saveAccountDetailsFromFrom() - Config.setAccountDetails failed.");
			
			return true;
		}
		else
		{
			Logger.e(CLASS_NAME, "Could not save configuration details");
			return false;
		}

	}
	
	/*@Override
    public void onBackPressed() {
            super.onBackPressed();
            this.finish();
    }*/
}
