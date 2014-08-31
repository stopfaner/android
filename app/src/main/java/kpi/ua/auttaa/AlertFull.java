package kpi.ua.auttaa;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;


public class AlertFull extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_full);
        TextView street = (TextView) findViewById(R.id.street_text);
        TextView coordinates = (TextView) findViewById(R.id.coordinates_text);

        //TODO: add simple implementation to textfields via intent

        //FAB implementation
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fake_button);
        fab.setColor(getResources().getColor(R.color.red500));
        fab.setDrawable(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alert_full, menu);
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
