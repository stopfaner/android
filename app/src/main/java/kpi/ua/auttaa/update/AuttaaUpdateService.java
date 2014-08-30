package kpi.ua.auttaa.update;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;

import kpi.ua.auttaa.PreferencesActivity;
import kpi.ua.auttaa.login.LoginActivity;

/**
 * Created by okovalchuk on 8/30/14.
 */
public class AuttaaUpdateService extends IntentService {

    public static final String URL = "http://php-auttaa.rhcloud.com";

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
            StringBuilder querry = new StringBuilder(URL + "/?method=auth&user_id=");
            querry.append(userId).append("&token=").append(secretString).append("&crd=").append(lattitude).append(',').append(longitude);
            get.setURI(new URI(querry.toString()));
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.execute(get);
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPD_FREQ, "2"));
        int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq*60*1000;
        alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFreq*60*1000, alarmIntent);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        String ALARM_ACTION = kpi.ua.auttaa.update.AuttaaAlarmReciever.ACTION_UPDATE_AUTTAA;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
    }
}
