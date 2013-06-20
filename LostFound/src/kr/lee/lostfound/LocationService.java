package kr.lee.lostfound;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service implements LocationListener {
	boolean mQuit;
	boolean firstMessage = true;
	LocationManager location = null;
	double latitude = 0;
	double longitude = 0;
	String address = "";

	@Override
	public void onCreate() {
		super.onCreate();
		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.NO_REQUIREMENT); //GPS_PROVIDER or NETWORK_PROVIDER
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		String provider = location.getBestProvider(criteria, true);

		location.requestLocationUpdates(provider, 1000, 0, this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mQuit = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		address = intent.getStringExtra("address");

		mQuit = false;
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
		longitude = location.getLongitude();
		
		if(firstMessage){
			String mSmsUrl = "http://maps.google.de/maps?z=17&q=loc:" + latitude + "," + longitude;
			
	
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(address, null, mSmsUrl, null, null);
			firstMessage = false;
			this.stopSelf();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
