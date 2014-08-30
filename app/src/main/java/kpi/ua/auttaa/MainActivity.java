package kpi.ua.auttaa;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends Activity {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                moveToLocation();
            }
        }, 1000); //delay to wait while map is loading
    }

    private void moveToLocation() {
        Location myLocation = googleMap.getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, R.string.location_invalid, Toast.LENGTH_SHORT).show();
            return;
        } else {
            double lattitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 13.5f));
        }
    }
}

