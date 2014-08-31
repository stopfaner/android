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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.alert_widget);

        Intent intent = new Intent(context, AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, 0);
        views.setOnClickPendingIntent(R.id.widget_start, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
        Toast.makeText(context,"Added widet", Toast.LENGTH_SHORT).show();
    }
}


