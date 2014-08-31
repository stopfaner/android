package kpi.ua.auttaa.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import kpi.ua.auttaa.AlarmActivity;
import kpi.ua.auttaa.R;

/**
 * Implementation of App Widget functionality.
 */
public class AlertWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for(int i=0; i<appWidgetIds.length; i++){
            int currentWidgetId = appWidgetIds[i];

            Intent intent = new Intent(context, AlarmActivity.class);

            PendingIntent pending = PendingIntent.getActivity(context, 0,
                    intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.alert_widget);
            views.setOnClickPendingIntent(R.id.widget_start, pending);
            appWidgetManager.updateAppWidget(currentWidgetId,views);
            Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show();
        }
    }
}


