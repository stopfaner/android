package kpi.ua.auttaa;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import kpi.ua.auttaa.login.LoginActivity;


public class AlertFull extends Activity {

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_full);
        TextView street = (TextView) findViewById(R.id.street_text);
        TextView coordinates = (TextView) findViewById(R.id.coordinates_text);
        street.setText("");

        String coords = getIntent().getStringExtra("COORDS");
        if (coords != null && coords.length() != 0) {
            coordinates.setText(coords);
        }

        String[] latlng = coords.split(",");
        if (latlng != null && latlng.length == 2) {

            latitude = Double.parseDouble(latlng[0]);
            longitude = Double.parseDouble(latlng[1]);

            GoogleMap googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.open_map_fragment)).getMap();
            if (googleMap != null) {
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
                googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_mark)));
            }
        }

            //

        //FAB implementation
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fake_button);
        fab.setColor(getResources().getColor(R.color.red500));
        fab.setDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", MODE_PRIVATE);
                String userId = prefs.getString(LoginActivity.USER_ID, "-1");
                String secretString = prefs.getString(LoginActivity.SECRET_STRING, "");

                try {
                    StringBuilder querry = new StringBuilder("http://php-auttaa.rhcloud.com/?method=set_fake&user_id=");
                    querry.append(userId).append("&token=").append(secretString).append("&crd=").append(latitude).append(',').append(longitude);
                    connectWithHttpGet(querry.toString());
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                }
                AlertFull.this.finish();
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.alert_full, menu);
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
    }

    private void connectWithHttpGet(String querry) {
        System.out.println("service-connectWithHttpGet");

        class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String querryX = params[0];
                System.out.println(querryX);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(querryX);

                try {
                    httpClient.execute(httpGet);
                    return null;
                } catch (ClientProtocolException cpe) {
                    System.out.println("Exception generates caz of httpResponse :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second exception generates caz of httpResponse :" + ioe);
                    ioe.printStackTrace();
                }
                return null;
            }
        }

        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        httpGetAsyncTask.execute(querry);
    }
}
