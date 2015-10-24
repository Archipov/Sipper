package com.example.sipper;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.services.INgnSipService;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity{

	private NgnEngine mEngine;
	private INgnSipService mSipService;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	private CallStateReceiver callStateReceiver;
	
	//private Button editAccountDetailsButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		init();
		
		if(!Utilities.isNetworkAvailable(this))
		{
			Utilities.toast(this, "No network connection is available. Please enable your Wifi or data network.", true);
		}
		else
		{
			if(Common.attemptRegisterationOnStartup())
			{
				Utilities.toast(this, "Successfully registered using previously saved account details.", true);
			}
			else
				Utilities.toast(this, "Could not register using previously saved account details. Please add account details.", true);
		}
	}
	
	private void init()
	{
		//editAccountDetailsButton = (Button)findViewById(R.id.editAccountDetailsButton);
		
		// Get engines
		mEngine = NgnEngine.getInstance();
		mSipService = mEngine.getSipService();
	  
		// Register broadcast receivers
		regBroadcastReceiver = new RegistrationBroadcastReceiver();
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		registerReceiver(regBroadcastReceiver, intentFilter);
		
		// Call state broadcast receiver
		final IntentFilter intentRFilter = new IntentFilter();
		callStateReceiver = new CallStateReceiver();
		intentRFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		registerReceiver(callStateReceiver, intentRFilter);  
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		// TODO unregister receivers
		unregisterReceiver(regBroadcastReceiver);
		unregisterReceiver(callStateReceiver);
		
		// TODO stop engine if required. Stopping engine stops all services.
		
	}
	
	public void onClick(View view)
	{
		switch(view.getId()) 
		{
	    	case R.id.openAccountDetailsFormButton:
	    		Intent openAccountDetailsFormIntent = new Intent(MainActivity.this, AddAccountActivity.class);
	    		startActivity(openAccountDetailsFormIntent);
	    	break;
	    	case R.id.openDialerButton:
	    		Intent openDialerIntent = new Intent(MainActivity.this, DialerActivity.class);
	    		startActivity(openDialerIntent);
	    	break;
		}
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
