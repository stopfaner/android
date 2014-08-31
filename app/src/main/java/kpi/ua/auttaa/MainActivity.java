package kpi.ua.auttaa;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import kpi.ua.auttaa.login.DataClass;
import kpi.ua.auttaa.login.LoginActivity;
import kpi.ua.auttaa.login.SessionStore;
import kpi.ua.auttaa.update.AuttaaUpdateService;


public class MainActivity extends Activity {

    public double updateFreq = 0.25;
    public static final int SHOW_PREFERENCES = 0;
    public static final int SHOW_LOGIN = 1;

    private GoogleMap googleMap;
    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", Context.MODE_PRIVATE);
        if (!prefs.contains(LoginActivity.IS_USER_LOGGED_IN) || !prefs.getBoolean(LoginActivity.IS_USER_LOGGED_IN, false)) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(loginIntent, SHOW_LOGIN);
        }

        //

        setContentView(R.layout.activity_main);

        // There is floating actionButton initialization
        FloatingActionButton alarmFloatingButton = (FloatingActionButton) findViewById(R.id.button_alarm);
        alarmFloatingButton.setColor(getResources().getColor(R.color.red700));
        alarmFloatingButton.setDrawable(getResources().getDrawable(R.drawable.ic_allert));
        alarmFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "AlarmButton clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                Location location = googleMap.getMyLocation();
                if (location == null) {
                    return;
                }
                String coords = location.getLatitude()+","+location.getLongitude();
                intent.putExtra("COORDS", coords);
                startActivity(intent);
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
                    JSONArray data = new JSONArray(jsonString);
                    googleMap.clear();
                    for (int i = 0; i < data.length(); i++) {
                        String coordString = data.getString(i);

                        String[] coords = coordString.split(",");
                        double latitude = Double.parseDouble(coords[0]);
                        double longitude = Double.parseDouble(coords[1]);



                        googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(latitude + "," + longitude).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark)));
                        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Intent alertfullIntent = new Intent(MainActivity.this, AlertFull.class);
                                alertfullIntent.putExtra("COORDS", marker.getTitle());
                                startActivity(alertfullIntent);
                                return true;
                            }
                        });
                    }
                } catch (Exception ex) {
                    Log.e(MainActivity.this.getClass().getName(), ex.getMessage(), ex);
                }

            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                moveToLocation();
                updateFromPreferences();
            }
        }, 2000); //delay to wait while map is loading


    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(AuttaaUpdateService.AUS_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
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
            case R.id.action_logout:
                SessionStore.clear(getApplicationContext());
                SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", MODE_PRIVATE);
                prefs.edit().clear().apply();
                finish();
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
        } else if (requestCode == SHOW_LOGIN) {
            SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", Context.MODE_PRIVATE);
            if (!prefs.contains(LoginActivity.IS_USER_LOGGED_IN) || !prefs.getBoolean(LoginActivity.IS_USER_LOGGED_IN, false)) {
                finish();
            } else {
                DataClass.userId=prefs.getString(LoginActivity.USER_ID, "-1");
                DataClass.secretToken=prefs.getString(LoginActivity.SECRET_STRING, "");
            }

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

