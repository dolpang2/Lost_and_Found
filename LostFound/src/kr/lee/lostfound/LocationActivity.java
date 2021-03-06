package kr.lee.lostfound;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class LocationActivity extends FragmentActivity implements LocationListener {
	private GoogleMap mmap;
	private LocationManager locationManager;
	private String provider;
	private double lat;
	private double lng;
	private String mSmsUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		GooglePlayServicesUtil.isGooglePlayServicesAvailable(LocationActivity.this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);

		if (provider == null) { //위치정보 설정이 안되어 있으면 설정하는 엑티비티로 이동합니다
			new AlertDialog.Builder(LocationActivity.this).setTitle("위치서비스 동의")
					.setNeutralButton("이동", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(new Intent(
									android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
						}
					}).setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							finish();
						}
					}).show();
		} else { //위치 정보 설정이 되어 있으면 현재위치를 받아옵니다
			locationManager.requestLocationUpdates(provider, 1, 1, LocationActivity.this);
			setUpMapIfNeeded();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {//위치설정 엑티비티 종료 후 
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			provider = locationManager.getBestProvider(criteria, true);
			if (provider == null) { //사용자가 위치설정동의 안했을때 종료 
				finish();
			} else { //사용자가 위치설정 동의 했을때 
				locationManager.requestLocationUpdates(provider, 1L, 2F, LocationActivity.this);
				setUpMapIfNeeded();
			}
			break;
		}
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();

	}

	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}

	private void setUpMapIfNeeded() {
		if (mmap == null) {
			mmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mmap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mmap.setMyLocationEnabled(true);
		mmap.getMyLocation();

	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Override
	public void onLocationChanged(Location location) {
		WifiManager wManager = null;
		wManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wManager.setWifiEnabled(true);

		lat = location.getLatitude();
		lng = location.getLongitude();
		setLat(lat);
		setLng(lng);

		LatLng latLng = new LatLng(lat, lng);
		mmap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		mmap.animateCamera(CameraUpdateFactory.zoomTo(17));
	}

	public void setSmsUrl(String mSmsUrl) {
		this.mSmsUrl = mSmsUrl;
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}