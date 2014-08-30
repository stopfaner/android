package kpi.ua.auttaa;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import kpi.ua.auttaa.login.LoginActivity;


public class MainActivity extends Activity {

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //

        setContentView(R.layout.activity_main);

        // There is floating actionButton initialization
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.start_alarm);
        floatingActionButton.setColor(getResources().getColor(R.color.pink_900));
        floatingActionButton.setDrawable(getResources().getDrawable(android.R.drawable.ic_menu_add));
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "LOLOLOLO", Toast.LENGTH_SHORT).show();
            }
        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int choice = item.getItemId();
        switch (choice) {
            case R.id.action_locate:
                moveToLocation();
                return true;
            case R.id.action_settings:
                //TODO: launch settings activity
                return true;
            case R.id.action_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void moveToLocation() {
        Location myLocation = googleMap.getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, R.string.location_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        double lattitude = myLocation.getLatitude();
        double longitude = myLocation.getLongitude();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude, longitude), 13.5f));
    }
}

