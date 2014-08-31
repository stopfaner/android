package kpi.ua.auttaa.update;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import kpi.ua.auttaa.PreferencesActivity;
import kpi.ua.auttaa.login.LoginActivity;

/**
 * Created by okovalchuk on 8/30/14.
 */
public class AuttaaUpdateService extends IntentService {

    public static final String URL = "http://php-auttaa.rhcloud.com";
    static final public String AUS_RESULT = "kpi.ua.auttaa.update.AUS_RESULT";
    private LocalBroadcastManager broadcaster;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    public AuttaaUpdateService() {
        super("AuttaaUpdateService");
    }

    public AuttaaUpdateService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendData();
    }

    private void sendData() {
        System.out.println("service-senddata");
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);
        String provider = LocationManager.GPS_PROVIDER;
        Location l = locationManager.getLastKnownLocation(provider);

        double lattitude = l.getLatitude();
        double longitude = l.getLongitude();

        SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", MODE_PRIVATE);
        String userId = prefs.getString(LoginActivity.USER_ID, "-1");
        String secretString = prefs.getString(LoginActivity.SECRET_STRING, "");

        try {
            HttpGet get = new HttpGet();
            StringBuilder querry = new StringBuilder(URL + "/?method=get_coordinates&user_id=");
            querry.append(userId).append("&token=").append(secretString).append("&crd=").append(lattitude).append(',').append(longitude);
            connectWithHttpGet(querry.toString());
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service-onstart-command");
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        double updateFreq = Double.parseDouble(prefs.getString(PreferencesActivity.PREF_UPD_FREQ, "0.25"));
        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long timeToRefresh = (long) (SystemClock.elapsedRealtime() + updateFreq*60*1000);
        alarmManager.setInexactRepeating(alarmType, timeToRefresh, (long)(updateFreq*60*1000), alarmIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service created!!!");
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        String ALARM_ACTION = kpi.ua.auttaa.update.AuttaaAlarmReciever.ACTION_UPDATE_AUTTAA;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void sendResult(String message) {
        Intent intent = new Intent(AUS_RESULT);
        if(message != null)
            intent.putExtra(AUS_RESULT, message);
        broadcaster.sendBroadcast(intent);
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

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    InputStream inputStream = httpResponse.getEntity().getContent();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();

                    String bufferedStrChunk = null;
                    while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
                        stringBuilder.append(bufferedStrChunk);
                    }
                    System.out.println("Returning value of doInBackground :" + stringBuilder.toString());
                    return stringBuilder.toString();

                } catch (ClientProtocolException cpe) {
                    System.out.println("Exception generates caz of httpResponse :" + cpe);
                    cpe.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println("Second exception generates caz of httpResponse :" + ioe);
                    ioe.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                sendResult(result);
            }
        }

        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        httpGetAsyncTask.execute(querry);
    }
}