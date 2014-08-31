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

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import kpi.ua.auttaa.login.LoginActivity;


public class AlarmActivity extends Activity {

    private TextView timer_text;
    public int tentime;
    private Timer timer;

    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String coords = getIntent().getStringExtra("COORDS");
        String[] latlng = coords.split(",");

        lat = Double.parseDouble(latlng[0]);
        lng = Double.parseDouble(latlng[1]);

        setContentView(R.layout.activity_alarm);
        timer_text = (TextView) findViewById(R.id.timer_text);
        tentime = 10;
        //Timer
        timer = new Timer();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        }, 0, 1000);


        //FAB
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.post_cancel);
        fab.setColor(getResources().getColor(R.color.red));
        fab.setDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                finish();
            }
        });

    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(TimerTick);
    }


    private Runnable TimerTick = new Runnable() {
        public void run() {
            if (tentime!=0) {
                timer_text.setText(String.valueOf(tentime--));
            }
            else {
                timer.cancel();
                sendEvent();
                finish();
            }

            //This method runs in the same thread as the UI.

            //Do something to the UI thread here

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm, menu);
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

    private void sendEvent() {
        System.out.println("Alarm-Activity--sendEvent");

        SharedPreferences prefs = getSharedPreferences("kpi.ua.auttaa", MODE_PRIVATE);
        String userId = prefs.getString(LoginActivity.USER_ID, "-1");
        String secretString = prefs.getString(LoginActivity.SECRET_STRING, "");

        try {
            StringBuilder querry = new StringBuilder("http://php-auttaa.rhcloud.com/?method=set_event&user_id=");
            querry.append(userId).append("&token=").append(secretString).append("&crd=").append(lat).append(',').append(lng);
            System.out.println("Querry: " + querry.toString());
            connectWithHttpGet(querry.toString());
        } catch (Exception e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
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
