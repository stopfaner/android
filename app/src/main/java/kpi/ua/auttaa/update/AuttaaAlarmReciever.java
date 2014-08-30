package kpi.ua.auttaa.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by okovalchuk on 8/30/14.
 */
public class AuttaaAlarmReciever extends BroadcastReceiver {
    public static final String ACTION_UPDATE_AUTTAA = "kpi.ua.auttaa.update.ACTION_UPDATE_AUTTAA";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, AuttaaUpdateService.class);
        context.startService(startIntent);
    }
}
