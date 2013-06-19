
package com.tistory.pierce.mapservice;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class MapActivity extends FragmentActivity {
    private GoogleMap mGoogleMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        setGoogleMap(((SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map)).getMap());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    public GoogleMap getGoogleMap() {
        return mGoogleMap;
    }

    public void setGoogleMap(GoogleMap mGoogleMap) {
        this.mGoogleMap = mGoogleMap;
    }

}
