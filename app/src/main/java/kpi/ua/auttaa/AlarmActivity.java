package kpi.ua.auttaa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;

import java.util.Timer;
import java.util.TimerTask;


public class AlarmActivity extends Activity {

    private TextView timer_text;
    public int tentime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        timer_text = (TextView) findViewById(R.id.timer_text);
        tentime = 10;
        //Timer
        Timer timer = new Timer();


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
                //TODO: POST about new Alarm
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
}
