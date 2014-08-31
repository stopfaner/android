package kpi.ua.auttaa;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
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

import org.json.JSONObject;

import kpi.ua.auttaa.login.LoginActivity;
import kpi.ua.auttaa.update.AuttaaUpdateService;


public class MainActivity extends Activity {

    public double updateFreq = 0.25;
    public static final int SHOW_PREFERENCES = 0;

    private GoogleMap googleMap;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateFromPreferences();
        //

        setContentView(R.layout.activity_main);

        // There is floating actionButton initialization
        FloatingActionButton alarmFloatingButton = (FloatingActionButton) findViewById(R.id.button_alarm);
        alarmFloatingButton.setColor(getResources().getColor(R.color.pink_900));
        alarmFloatingButton.setDrawable(getResources().getDrawable(R.drawable.ic_action_alarm));
        alarmFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "AlarmButton clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                //TODO: Send ALARM NOW!!!!
            }
        });

        //Map configuration
        googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map_fragment)).getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String jsonString = intent.getStringExtra(AuttaaUpdateService.AUS_RESULT);
                try {
                    JSONObject data = new JSONObject(jsonString);
                } catch (Exception ex) {
                    Log.e
                }

                googleMap.clear();

            }
        };

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
                Intent settingsIntent = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivityForResult(settingsIntent, SHOW_PREFERENCES);
                return true;
            case R.id.action_login:
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SHOW_PREFERENCES) {
            updateFromPreferences();
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

    private void updateFromPreferences() {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        updateFreq = Double.parseDouble(prefs.getString(PreferencesActivity.PREF_UPD_FREQ, "0.25"));

        startService(new Intent(MainActivity.this, AuttaaUpdateService.class));
    }


}

